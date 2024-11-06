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

public class SpeedCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public SpeedCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.getCommand("speed").setTabCompleter(this);
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.speed.active")) {
            config.set("modules.speed.active", true);
        }

        // Deutsch
        if (!config.contains("messages.de.speed.no-permission")) {
            config.set("messages.de.speed.no-permission", "&7Du hast &ckeine&7 Berechtigung dazu.");
        }
        if (!config.contains("messages.de.speed.usage")) {
            config.set("messages.de.speed.usage", "&cFalsche &7Verwendung: &e/speed <1-10> [walk|fly]");
        }
        if (!config.contains("messages.de.speed.invalid-speed")) {
            config.set("messages.de.speed.invalid-speed", "&cBitte gib eine Geschwindigkeit zwischen &e1 &cund &e10 &c ein.");
        }
        if (!config.contains("messages.de.speed.success")) {
            config.set("messages.de.speed.success", "&7Geschwindigkeit auf &e{speed} &7gesetzt. &7(&e{type}&7) ");
        }
        if (!config.contains("messages.de.speed.only-players")) {
            config.set("messages.de.speed.only-players", "&cNur Spieler können diesen Befehl verwenden.");
        }

        // Englisch
        if (!config.contains("messages.en.speed.no-permission")) {
            config.set("messages.en.speed.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.speed.usage")) {
            config.set("messages.en.speed.usage", "&cWrong &7usage: &e/speed <1-10> [walk|fly]");
        }
        if (!config.contains("messages.en.speed.invalid-speed")) {
            config.set("messages.en.speed.invalid-speed", "&cPlease enter a speed between &e1 &cand &e10.");
        }
        if (!config.contains("messages.en.speed.success")) {
            config.set("messages.en.speed.success", "&7Speed set to &e{speed} &7(&e{type}&7).");
        }
        if (!config.contains("messages.en.speed.only-players")) {
            config.set("messages.en.speed.only-players", "&cOnly players can use this command.");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("modules.speed.active", true)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("modules.inactive")));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("speed.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("simplecore.speed")) {
            player.sendMessage(getMessage("speed.no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(getMessage("speed.usage"));
            return true;
        }

        int speed;
        try {
            speed = Integer.parseInt(args[0]);
            if (speed < 1 || speed > 10) {
                player.sendMessage(getMessage("speed.invalid-speed"));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(getMessage("speed.invalid-speed"));
            return true;
        }

        String type;
        if (args.length > 1) {
            type = args[1].toLowerCase();
        } else {
            type = player.isFlying() ? "fly" : "walk";
        }


        switch (type) {
            case "walk":
                if (!player.hasPermission("simplecore.speed.walk")) {
                    player.sendMessage(getMessage("speed.no-permission"));
                    return true;
                }
                player.setWalkSpeed(speed * 0.1f);
                break;
            case "fly":
                if (!player.hasPermission("simplecore.speed.fly")) {
                    player.sendMessage(getMessage("speed.no-permission"));
                    return true;
                }
                if (player.getAllowFlight()) {
                    player.setFlySpeed(speed * 0.1f);
                } else {
                    player.sendMessage(getMessage("speed.flight-not-allowed"));
                    return true;

                }
                break;
            default:
                player.sendMessage(getMessage("speed.usage"));
                break;
        }

        player.sendMessage(getMessage("speed.success").replace("{speed}", String.valueOf(speed)).replace("{type}", type));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + lang + "." + path,
                "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (int i = 1; i <= 10; i++) {
                completions.add(String.valueOf(i));
            }
        } else if (args.length == 2) {
            completions.add("walk");
            completions.add("fly");
        }

        return completions;
    }
}
