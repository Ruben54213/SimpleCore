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
        initializeDefaultConfig();
    }

    private void initializeDefaultConfig() {
        config.addDefault("language", "de");
        config.addDefault("prefix", "&bSimple&fCore &8» ");

        // Deutsche Nachrichten
        config.addDefault("messages.de.weather.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        config.addDefault("messages.de.weather.usage", "&cFalsche&7 Verwendung! Verwende &e/weather rain&7/&esun&7/&ethunder&7.");
        config.addDefault("messages.de.weather.wrong-usage", "&cFalsche&7 Verwendung! Verwende &e/weather rain&7/&esun&7/&ethunder&7, deine Angabe wurde &cnicht&7 erkannt.");
        config.addDefault("messages.de.weather.weather-thunder", "&7Das Wetter wurde nun auf &eGewitter&7 umgestellt.");
        config.addDefault("messages.de.weather.weather-sun", "&7Das Wetter wurde nun auf &eSonne&7 umgestellt.");
        config.addDefault("messages.de.weather.weather-rain", "&7Das Wetter wurde nun auf &eRegen&7 umgestellt.");

        // Englische Nachrichten
        config.addDefault("messages.en.weather.only-players", "Only players can execute this command!");
        config.addDefault("messages.en.weather.usage", "&cIncorrect&7 usage! Use &e/weather rain&7/&esun&7/&ethunder&7.");
        config.addDefault("messages.en.weather.wrong-usage", "&cIncorrect&7 usage! Use &e/weather rain&7/&esun&7/&ethunder&7, your input was &cnot&7 recognized.");
        config.addDefault("messages.en.weather.weather-thunder", "&7The weather has been set to &ethunder&7.");
        config.addDefault("messages.en.weather.weather-sun", "&7The weather has been set to &esunny&7.");
        config.addDefault("messages.en.weather.weather-rain", "&7The weather has been set to &erainy&7.");

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weather.only-players"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weather.usage"));
            return false;
        }

        Player player = (Player) sender;
        String weatherType = args[0].toLowerCase();

        switch (weatherType) {
            case "sun":
            case "sonne":
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weather.weather-sun"));
                break;

            case "rain":
            case "regen":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weather.weather-rain"));
                break;

            case "thunder":
            case "gewitter":
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weather.weather-thunder"));
                break;

            default:
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".weather.wrong-usage"));
                return false;
        }

        return true;
    }

    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}
