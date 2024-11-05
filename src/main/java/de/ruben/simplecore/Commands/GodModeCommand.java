package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GodModeCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Set<UUID> godModePlayers;

    public GodModeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.godModePlayers = new HashSet<>();
        setupDefaultConfig();

        // Register the event listener to handle damage cancellation
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.godmode.active")) {
            config.set("modules.godmode.active", true);
        }
        if (!config.contains("godmode.prefix")) {
            config.set("godmode.prefix", "&bSimple&fCore &8» ");
        }
        if (!config.contains("messages.de.godmode.enabled")) {
            config.set("messages.de.godmode.enabled", "&7Der &eGodmode&7 wurde &aaktiviert&7.");
        }
        if (!config.contains("messages.de.godmode.disabled")) {
            config.set("messages.de.godmode.disabled", "&7Der &eGodmode&7 wurde &cdeaktiviert&7.");
        }
        if (!config.contains("messages.de.godmode.no-permission")) {
            config.set("messages.de.godmode.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.godmode.no-player")) {
            config.set("messages.de.godmode.no-player", "&7Der angegebene Spieler existiert&c nicht&7.");
        }
        if (!config.contains("messages.en.godmode.enabled")) {
            config.set("messages.en.godmode.enabled", "&7The &egodmode&7 was &aenabled&7.");
        }
        if (!config.contains("messages.en.godmode.disabled")) {
            config.set("messages.en.godmode.disabled", "&7The &egodmode&7 was &cdisabled&7.");
        }
        if (!config.contains("messages.en.godmode.no-permission")) {
            config.set("messages.en.godmode.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.godmode.no-player")) {
            config.set("messages.en.godmode.no-player", "&7The specified player does &cnot&7 exist.");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.no-console")));
            return true;
        }

        Player player = (Player) sender;

        // Check if the godmode module is active
        if (!config.getBoolean("modules.godmode.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.inactive")));
            return true;
        }

        // Check for godmode permission
        if (!player.hasPermission("simplecore.godmode")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.no-permission")));
            return true;
        }

        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.no-player")));
                return true;
            }
        }

        if (godModePlayers.contains(target.getUniqueId())) {
            godModePlayers.remove(target.getUniqueId());
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.disabled")));
            if (!target.equals(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.disabled").replace("{target}", target.getName())));
            }
        } else {
            godModePlayers.add(target.getUniqueId());
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.enabled")));
            if (!target.equals(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("godmode.enabled").replace("{target}", target.getName())));
            }
        }

        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");  // Default language is German
        return ChatColor.translateAlternateColorCodes('&', config.getString("godmode.prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (godModePlayers.contains(player.getUniqueId())) {
                event.setCancelled(true); // Prevent any damage if player is in godmode
            }
        }
    }
}
