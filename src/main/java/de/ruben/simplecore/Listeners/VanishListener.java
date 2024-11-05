package de.ruben.simplecore.Listeners;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import de.ruben.simplecore.Utility.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;

public class VanishListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public VanishListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.vanish.active")) {
            config.set("modules.vanish.active", true);
        }
        plugin.saveConfig();
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            VanishManager vm = VanishManager.getMainManager();
            if (vm.isVanished(player) || vm.isVanished(damager)) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        VanishManager vm = VanishManager.getMainManager();
        Player player = event.getPlayer();
        if (vm.isVanished(player)) {
            player.setFireTicks(0);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        VanishManager vm = VanishManager.getMainManager();
        if (vm.isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        VanishManager vm = VanishManager.getMainManager();
        if (vm.isVanished(event.getPlayer())) {
            event.setCancelled(true);
            System.out.println("Test");
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        VanishManager vm = VanishManager.getMainManager();

        for (UUID vanishedUUID : vm.getVanished()) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedUUID);
            if (vanishedPlayer != null) {
                if (!player.hasPermission("simplecore.vanish")) {
                    player.hidePlayer(vanishedPlayer);
                } else {
                    player.showPlayer(vanishedPlayer);
                }
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (vm.isVanished(onlinePlayer)) {
                if (!onlinePlayer.hasPermission("simplecore.vanish")) {
                    onlinePlayer.hidePlayer(player);
                } else {
                    onlinePlayer.showPlayer(player);
                }
            }
        }
    }
}

