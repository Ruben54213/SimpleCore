package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FlyCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public FlyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.fly.active")) {
            config.set("modules.fly.active", true);
        }
        if (!config.contains("messages.de.fly.no-permission")) {
            config.set("messages.de.fly.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.fly.no-player")) {
            config.set("messages.de.fly.no-player", "&7Der angegebene Spieler existiert&c nicht&7.");
        }
        if (!config.contains("messages.de.fly.enabled")) {
            config.set("messages.de.fly.enabled", "&7Der &eFlugmodus&7 wurde für &e{target}&7 aktiviert.");
        }
        if (!config.contains("messages.de.fly.disabled")) {
            config.set("messages.de.fly.disabled", "&7Der &eFlugmodus&7 wurde für &e{target}&7 deaktiviert.");
        }
        if (!config.contains("messages.en.fly.no-permission")) {
            config.set("messages.en.fly.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.fly.no-player")) {
            config.set("messages.en.fly.no-player", "&7The specified player does &cnot&7 exist.");
        }
        if (!config.contains("messages.en.fly.enabled")) {
            config.set("messages.en.fly.enabled", "&eFlight mode&7 enabled for &e{target}&7.");
        }
        if (!config.contains("messages.en.fly.disabled")) {
            config.set("messages.en.fly.disabled", "&eFlight mode&7 disabled for &e{target}&7.");
        }
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("modules.fly.active")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("fly.inactive")));
            return true;
        }

        if (!sender.hasPermission("simplecore.fly")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("fly.no-permission")));
            return true;
        }

        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("fly.no-player")));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("fly.no-player")));
                return true;
            }
            target = (Player) sender;
        }

        boolean newFlyState = !target.getAllowFlight();
        target.setAllowFlight(newFlyState);
        target.setFlying(newFlyState);

        String messageKey = newFlyState ? "fly.enabled" : "fly.disabled";
        String message = getMessage(messageKey).replace("{target}", target.getName());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de"); // Setze die Standardsprache auf Deutsch
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}