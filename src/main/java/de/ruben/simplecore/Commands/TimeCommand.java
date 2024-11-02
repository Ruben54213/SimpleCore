package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public TimeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.time.active")) {
            config.set("modules.time.active", true);
        }
        if (!config.contains("messages.de.time.no-permission")) {
            config.set("messages.de.time.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.time.success")) {
            config.set("messages.de.time.success", "&7Die Zeit wurde auf &e{time}&7 gesetzt.");
        }
        if (!config.contains("messages.en.time.no-permission")) {
            config.set("messages.en.time.no-permission", "&7You do not have &cpermission&7 for this.");
        }
        if (!config.contains("messages.en.time.success")) {
            config.set("messages.en.time.success", "&7The time has been set to &e{time}&7.");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages." + config.getString("language") + ".time.only-players")));
            return true;
        }

        Player player = (Player) sender;

        if (!config.getBoolean("modules.time.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        String timeType = label.equalsIgnoreCase("time") && args.length > 0 ? args[0].toLowerCase() : label.toLowerCase();
        long time;

        switch (timeType) {
            case "day":
                time = 1000;
                break;
            case "night":
                time = 13000;
                break;
            case "midnight":
                time = 18000;
                break;
            case "noon":
                time = 6000;
                break;
            default:
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".time.usage")));
                return true;
        }

        player.getWorld().setTime(time);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("time.success").replace("{time}", timeType)));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, ""));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Auto-Completion only for /time command
        if (command.getName().equalsIgnoreCase("time") && args.length == 1) {
            completions.addAll(Arrays.asList("day", "night", "midnight", "noon"));
        }

        return completions;
    }
}