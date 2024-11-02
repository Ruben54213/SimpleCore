package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderChestCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public EnderChestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.enderchest.active")) {
            config.set("modules.enderchest.active", true);
        }
        if (!config.contains("messages.de.enderchest.no-permission")) {
            config.set("messages.de.enderchest.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.enderchest.success")) {
            config.set("messages.de.enderchest.success", "&7Deine &eEnderchest &7wurde geöffnet.");
        }
        if (!config.contains("messages.de.enderchest.success-other")) {
            config.set("messages.de.enderchest.success-other", "&7Du hast die &eEnderchest &7von &e{target} &7geöffnet.");
        }
        if (!config.contains("messages.de.enderchest.player-not-found")) {
            config.set("messages.de.enderchest.player-not-found", "&7Der Spieler &e{target} &7wurde nicht gefunden.");
        }
        if (!config.contains("messages.en.enderchest.no-permission")) {
            config.set("messages.en.enderchest.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.enderchest.success")) {
            config.set("messages.en.enderchest.success", "&7Your &eEnder Chest &7has been opened.");
        }
        if (!config.contains("messages.en.enderchest.success-other")) {
            config.set("messages.en.enderchest.success-other", "&7You have opened the &eEnder Chest &7of &e{target}&7.");
        }
        if (!config.contains("messages.en.enderchest.player-not-found")) {
            config.set("messages.en.enderchest.player-not-found", "&7Player &e{target} &7not found.");
        }
        if (!config.contains("messages.de.enderchest.only-players")) {
            config.set("messages.de.enderchest.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.enderchest.only-players")) {
            config.set("messages.en.enderchest.only-players", "Only players can execute this command!");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("enderchest.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.enderchest.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        // Überprüfen, ob der Spieler eine Enderchest von einem anderen öffnen möchte
        if (args.length > 0 && player.hasPermission("simplecore.enderchest.other")) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                player.openInventory(target.getEnderChest());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("enderchest.success-other").replace("{target}", target.getName())));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("enderchest.player-not-found").replace("{target}", args[0])));
            }
            return true;
        }

        // Berechtigungen prüfen
        if (!player.hasPermission("simplecore.enderchest")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("enderchest.no-permission")));
            return true;
        }

        // Öffnet die eigene Enderchest
        player.openInventory(player.getEnderChest());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("enderchest.success")));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}