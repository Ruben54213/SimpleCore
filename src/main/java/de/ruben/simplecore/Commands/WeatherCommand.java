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
import java.util.List;

public class WeatherCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public WeatherCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        initializeDefaultConfig();
        plugin.getCommand("weather").setExecutor(this);
        plugin.getCommand("weather").setTabCompleter(this);
    }

    private void initializeDefaultConfig() {
        // Überprüfen, ob die Sprache bereits gesetzt ist
        if (!config.contains("language")) {
            config.set("language", "de");
        }

        // Überprüfen, ob der Prefix bereits gesetzt ist
        if (!config.contains("prefix")) {
            config.set("prefix", "&bSimple&fCore &8» ");
        }

        // Überprüfen, ob das Wetter-Modul aktiv ist
        if (!config.contains("modules.weather.active")) {
            config.set("modules.weather.active", true);  // Aktivierungs-Flag für das Modul
        }

        // Nachrichten für das Modul nur setzen, wenn sie nicht existieren
        if (!config.contains("messages.de.weather.module-disabled")) {
            config.set("messages.de.weather.module-disabled", "&7Dieses Modul ist zurzeit &cDeaktiviert&7, aktiviere es in der &eConfig&7.");
        }
        if (!config.contains("messages.de.weather.only-players")) {
            config.set("messages.de.weather.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.de.weather.usage")) {
            config.set("messages.de.weather.usage", "&cFalsche&7 Verwendung! Verwende &e/weather rain&7/&esun&7/&ethunder&7.");
        }
        if (!config.contains("messages.de.weather.wrong-usage")) {
            config.set("messages.de.weather.wrong-usage", "&cFalsche&7 Verwendung! Verwende &e/weather rain&7/&esun&7/&ethunder&7, deine Angabe wurde &cnicht&7 erkannt.");
        }
        if (!config.contains("messages.de.weather.weather-thunder")) {
            config.set("messages.de.weather.weather-thunder", "&7Das Wetter wurde nun auf &eGewitter&7 umgestellt.");
        }
        if (!config.contains("messages.de.weather.weather-sun")) {
            config.set("messages.de.weather.weather-sun", "&7Das Wetter wurde nun auf &eSonne&7 umgestellt.");
        }
        if (!config.contains("messages.de.weather.weather-rain")) {
            config.set("messages.de.weather.weather-rain", "&7Das Wetter wurde nun auf &eRegen&7 umgestellt.");
        }

        // Speichert die Konfiguration, wenn Änderungen vorgenommen wurden
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Prüfen, ob das Modul aktiviert ist
        if (!config.getBoolean("modules.weather.active", true)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".modules.inactive")));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigMessage(getConfigMessage("prefix") + "messages." + getConfigMessage("language") + ".weather.only-players"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weather.usage"));
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
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".weather.wrong-usage"));
                return false;
        }

        return true;
    }

    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        String language = config.getString("language", "de");
        if (args.length == 1) {
            if (language.equals("de")) {
                completions.add("sonne");
                completions.add("regen");
                completions.add("gewitter");

                completions.add("sun");
                completions.add("rain");
                completions.add("thunder");
            } else {
                completions.add("sun");
                completions.add("rain");
                completions.add("thunder");
            }
        }
        return completions;
    }
}
