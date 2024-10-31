package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WeatherCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public WeatherCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weater.only-players"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weater.usage"));
            return false;
        }

        Player player = (Player) sender;
        String weatherType = args[0].toLowerCase();

        switch (weatherType) {
            case "sun":
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weater.weather-sun"));
                break;

            case "sonne":
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weater.weather-sun"));
                break;

            case "rain":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weater.weather-rain"));
                break;

            case "regen":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weater.weather-rain"));
                break;

            case "thunder":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weater.weather-thunder"));
                break;

            case "gewitter":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weater.weather-thunder"));
                break;

            default:
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weater.wrong-usage"));
                return false;
        }

        return true;
    }

    // Methode zum Abrufen von Nachrichten aus der Config-Datei
    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7:" + path));
    }
}
