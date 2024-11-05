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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class InvseeCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public InvseeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin); // Register event listener
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.invsee.active")) {
            config.set("modules.invsee.active", true);
        }
        if (!config.contains("invsee-prefix")) {
            config.set("invsee.prefix", "&bSimple&fCore &8» &7");
        }
        if (!config.contains("messages.de.invsee.no-permission")) {
            config.set("messages.de.invsee.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.invsee.no-player")) {
            config.set("messages.de.invsee.no-player", "&7Der angegebene Spieler existiert&c nicht&7.");
        }
        if (!config.contains("messages.de.invsee.view-only")) {
            config.set("messages.de.invsee.view-only", "&7Du siehst &anun&7 das Inventar von &e{target}&7.");
        }
        if (!config.contains("messages.de.invsee.edit-mode")) {
            config.set("messages.de.invsee.edit-mode", "&7Du kannst &anun&7 das Inventar von &e{target}&7 bearbeiten.");
        }
        if (!config.contains("messages.en.invsee.no-permission")) {
            config.set("messages.en.invsee.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.invsee.no-player")) {
            config.set("messages.en.invsee.no-player", "&7The specified player does &cnot&7 exist.");
        }
        if (!config.contains("messages.en.invsee.view-only")) {
            config.set("messages.en.invsee.view-only", "&7You are viewing the inventory of &e{target}&7.");
        }
        if (!config.contains("messages.en.invsee.edit-mode")) {
            config.set("messages.en.invsee.edit-mode", "&7You are in edit mode for &e{target}&7's inventory.");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.no-console")));
            return true;
        }

        Player player = (Player) sender;

        // Check if the invsee module is active
        if (!config.getBoolean("modules.invsee.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.inactive")));
            return true;
        }

        // Check for invsee permission
        if (!player.hasPermission("simplecore.invsee")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.no-permission")));
            return true;
        }

        // Check if a player name was provided
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.no-player")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.no-player")));
            return true;
        }

        // Get inventory title with prefix
        String inventoryTitle = ChatColor.translateAlternateColorCodes('&', "§e§0§f§l§r" + config.getString("invsee.prefix") + target.getName());

        if (player.hasPermission("simplecore.invsee.change")) {
            // Open the actual inventory in edit mode
            player.openInventory(target.getInventory());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.edit-mode").replace("{target}", target.getName())));
        } else {
            // Create a copy of the inventory for view-only mode with a valid size
            int inventorySize = ((target.getInventory().getSize() + 8) / 9) * 9;  // Round up to the nearest multiple of 9
            Inventory copyInventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
            copyInventory.setContents(target.getInventory().getContents());
            player.openInventory(copyInventory);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.view-only").replace("{target}", target.getName())));
        }

        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");  // Default language is German
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // Check if the inventory is a view-only inventory
        if (event.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "§e§0§f§l§r"))) {
            // Block item movement if player does not have "simplecore.invsee.change" permission
            if (!player.hasPermission("simplecore.invsee.change")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("invsee.no-permission")));
            }
        }
    }
}