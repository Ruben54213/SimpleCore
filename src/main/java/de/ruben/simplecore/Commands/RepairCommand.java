package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepairCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public RepairCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        // Die vorhandenen Konfigurationen bleiben hier unverändert
        if (!config.contains("modules.repair.active")) {
            config.set("modules.repair.active", true);
        }
        if (!config.contains("messages.de.repair.no-permission")) {
            config.set("messages.de.repair.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.repair.success")) {
            config.set("messages.de.repair.success", "&7Dein &eItem &7wurde &aerfolgreich&7 repariert.");
        }
        if (!config.contains("messages.de.repair.success-all")) {
            config.set("messages.de.repair.success-all", "&7Alle &eItems &7in deinem Inventar wurden &arepariert&7.");
        }
        if (!config.contains("messages.de.repair.no-item")) {
            config.set("messages.de.repair.no-item", "&7Du hast &ckein &7Item in der &eHand&7, das &arepariert&7 werden kann.");
        }
        if (!config.contains("messages.de.repair.only-players")) {
            config.set("messages.de.repair.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.repair.no-permission")) {
            config.set("messages.en.repair.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.repair.success")) {
            config.set("messages.en.repair.success", "&7Your &eitem &7has been &asuccessfully&7 &arepaired&7.");
        }
        if (!config.contains("messages.en.repair.success-all")) {
            config.set("messages.en.repair.success-all", "&7All &eitems &7in your inventory have been &arepaired&7.");
        }
        if (!config.contains("messages.en.repair.no-item")) {
            config.set("messages.en.repair.no-item", "&7You have &cno&7 item in your &ehand&7 to repair.");
        }
        if (!config.contains("messages.en.repair.only-players")) {
            config.set("messages.en.repair.only-players", "Only players can execute this command!");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.repair.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.inactive")));
            return true;
        }

        // Überprüfen, ob der Spieler Berechtigung für den Befehl hat
        if (!player.hasPermission("simplecore.repair")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.no-permission")));
            return true;
        }

        // Überprüfen, ob "/repair all" verwendet wird und die Berechtigung dafür vorhanden ist
        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            if (!player.hasPermission("simplecore.repair.all")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.no-permission")));
                return true;
            }
            // Repariere alle Items im Inventar
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR && item.getType().getMaxDurability() > 0) {
                    item.setDurability((short) 0);
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.success-all")));
            return true;
        }

        // Repariere das Item in der Hand
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR || itemInHand.getType().getMaxDurability() <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.no-item")));
            return true;
        }
        itemInHand.setDurability((short) 0);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("repair.success")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("all");
        }
        return new ArrayList<>();
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}
