package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LanguageCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public LanguageCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig(); // Set up default config values
    }

    private void setupDefaultConfig() {
        if (!config.contains("messages.de.language.success")) {
            config.set("messages.de.language.success", "&7Die &eSprache&7 wurde nun &aErfolgreich&7 geändert!");
        }
        if (!config.contains("messages.de.language.wrong-language")) {
            config.set("messages.de.language.wrong-language", "&7Diese Sprache wird momentan &cnicht&7 unterstützt!");
        }
        if (!config.contains("messages.de.language.no-language")) {
            config.set("messages.de.language.no-language", "&7Du &cmusst&7 eine Sprache angeben! (&ede&7/&een&7)");
        }

        if (!config.contains("messages.en.language.success")) {
            config.set("messages.en.language.success", "&7The &elanguage&7 has been &asuccessfully&7 changed!");
        }
        if (!config.contains("messages.en.language.wrong-language")) {
            config.set("messages.en.language.wrong-language", "&7This language is currently &cnot&7 supported!");
        }
        if (!config.contains("messages.en.language.no-language")) {
            config.set("messages.en.language.no-language", "&7You &cmust&7 specify a language! (&ede&7/&een&7)");
        }

        // Save the default values to the config
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".language.no-language"));
            return true;
        }

        String language = args[0].toLowerCase();

        if (!language.equals("de") && !language.equals("en")) {
            sender.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".language.wrong-language"));
            return true;
        }

        // Update the language in the config
        config.set("language", language);
        plugin.saveConfig();

        sender.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + language + ".language.success"));

        // Reload the plugin configuration
        plugin.reloadConfig();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        // Check if the player is trying to complete the language argument
        if (args.length == 1) {
            completions.add("de");
            completions.add("en");
        }

        return completions;
    }

    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString(path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}
