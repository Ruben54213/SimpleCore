package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MoreCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public MoreCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.more.active")) {
            config.set("modules.more.active", true);
        }
        if (!config.contains("messages.de.more.no-permission")) {
            config.set("messages.de.more.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.more.success")) {
            config.set("messages.de.more.success", "&7Das Item wurde auf einen &e64er Stack &7gesetzt.");
        }
        if (!config.contains("messages.de.more.no-item")) {
            config.set("messages.de.more.no-item", "&7Du hast &ckein &7Item in der &eHand&7.");
        }
        if (!config.contains("messages.de.more.only-players")) {
            config.set("messages.de.more.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.more.no-permission")) {
            config.set("messages.en.more.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.more.success")) {
            config.set("messages.en.more.success", "&7The item has been set to a &e64 stack&7.");
        }
        if (!config.contains("messages.en.more.no-item")) {
            config.set("messages.en.more.no-item", "&7You have &cno&7 item in your &ehand&7.");
        }
        if (!config.contains("messages.en.more.only-players")) {
            config.set("messages.en.more.only-players", "Only players can execute this command!");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("more.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.more.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("more.inactive")));
            return true;
        }

        // Überprüfen, ob der Spieler Berechtigung für den Befehl hat
        if (!player.hasPermission("simplecore.more")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("more.no-permission")));
            return true;
        }

        // Überprüfen, ob der Spieler ein Item in der Hand hat
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("more.no-item")));
            return true;
        }

        // Setze das Item in der Hand auf einen 64er Stack
        itemInHand.setAmount(64);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("more.success")));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}
