package de.ruben.simplecore.Commands;

import de.ruben.simplecore.Utility.VanishManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VanishCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public VanishCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.vanish.active")) {
            config.set("modules.vanish.active", true);
        }
        if (!config.contains("messages.de.vanish.no-permission")) {
            config.set("messages.de.vanish.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.vanish.success.activated")) {
            config.set("messages.de.vanish.success.activated", "&7Vanish wurde &aaktiviert&7.");
        }
        if (!config.contains("messages.de.vanish.success.deactivated")) {
            config.set("messages.de.vanish.success.deactivated", "&7Vanish wurde &cdeaktiviert&7.");
        }
        if (!config.contains("messages.de.vanish.usage")) {
            config.set("messages.de.vanish.usage", "&cNutze &e/vanish&7.");
        }
        if (!config.contains("messages.en.vanish.no-permission")) {
            config.set("messages.en.vanish.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.vanish.success.activated")) {
            config.set("messages.en.vanish.success.activated", "&7Vanish has been &aactivated&7.");
        }
        if (!config.contains("messages.en.vanish.success.deactivated")) {
            config.set("messages.en.vanish.success.deactivated", "&7Vanish has been &cdeactivated&7.");
        }
        if (!config.contains("messages.en.vanish.usage")) {
            config.set("messages.en.vanish.usage", "&cUse &e/vanish&7.");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!config.getBoolean("modules.vanish.active", true)) {
            sender.sendMessage(getMessage("modules.inactive"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("vanish.only-players"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("simplecore.vanish")) {
            player.sendMessage(getMessage("vanish.no-permission"));
            return true;
        }

        if (args.length == 0) {
            VanishManager vm = VanishManager.getMainManager();
            boolean newState = !vm.isVanished(player);
            vm.setVanished(player, newState);
            player.sendMessage(getMessage(newState ? "vanish.success.activated" : "vanish.success.deactivated"));
        } else {
            player.sendMessage(getMessage("vanish.usage"));
        }

        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + lang + "." + path,
                "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}
