package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
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

public class NearbyCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public NearbyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.nearby-players.active")) {
            config.set("modules.nearby-players.active", true);
        }
        if (!config.contains("messages.de.nearby.nearby-players")) {
            config.set("messages.de.nearby-players", "&7Folgende &cSpieler&7 befinden sich in der &eNähe&7:");
        }
        if (!config.contains("messages.en.nearby.nearby-players")) {
            config.set("messages.en.nearby-players", "&7The following &cplayers&7 are in the &earea&7:");
        }
        if (!config.contains("messages.de.nearby.no-players")) {
            config.set("messages.de.no-players", "&7Es befinden sich &ckeine&7 Spieler in deiner &eNähe&7.");
        }
        if (!config.contains("messages.en.nearby.no-players")) {
            config.set("messages.en.no-players", "&7There are &cno&7 players in your &earea&7.");
        }
        if (!config.contains("messages.de.nearby.only-players")) {
            config.set("messages.de.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.nearby.only-players")) {
            config.set("messages.en.only-players", "Only players can execute this command!");
        }
        if (!config.contains("messages.de.nearby.distance-word")) {
            config.set("messages.de.distance-word", "&7, &cDistance&7:");
        }
        if (!config.contains("messages.en.nearby.distance-word")) {
            config.set("messages.en.distance-word", "&7, &cDistance&7:");
        }
        if (!config.contains("messages.de.nearby.block-word")) {
            config.set("messages.de.block-word", "&eBlöcke&7");
        }
        if (!config.contains("messages.en.nearby.block-word")) {
            config.set("messages.en.block-word", "&eBlocks&7");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".nearby.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.nearby-players.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        List<String> nearbyPlayers = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                double distance = player.getLocation().distance(onlinePlayer.getLocation());
                if (distance <= 200) {
                    nearbyPlayers.add("&7" + onlinePlayer.getName() + config.getString("messages." + config.getString("language") + ".nearby.distance-word") + (int) distance + config.getString("messages." + config.getString("language") + ".nearby.block-word"));
                }
            }
        }

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".nearby.no-players")));
        } else {
            String playersList = String.join(" | ", nearbyPlayers);
            String message = getMessage("nearby-players").replace("{players}", playersList);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
        return true;
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
