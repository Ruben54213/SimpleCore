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

public class KillCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public KillCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        // Standardnachrichten für den Kill-Befehl setzen
        if (!config.contains("messages.de.kill.success")) {
            config.set("messages.de.kill.success", "&7Du hast &e{player}&a erfolgreich&7 getötet.");
        }
        if (!config.contains("messages.de.kill.all-success")) {
            config.set("messages.de.kill.all-success", "&7Alle Spieler wurden &aerfolgreich&7 getötet.");
        }
        if (!config.contains("messages.en.kill.only-players")) {
            config.set("messages.en.kill.only-players", "Only players can execute this command!");
        }
        if (!config.contains("messages.de.kill.only-players")) {
            config.set("messages.de.kill.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.kill.usage")) {
            config.set("messages.en.kill.usage", "&cWrong&7 usage: Use &e/kill &7(&eall&7/@a&7) or &e/kill &7(&ePlayer&7).");
        }
        if (!config.contains("messages.de.kill.usage")) {
            config.set("messages.de.kill.usage", "&cFalsche&7 Benutzung: Benutze &e/kill &7(&eall&7/@a&7) oder &e/kill &7(&eSpieler&7).");
        }
        if (!config.contains("messages.en.kill.success")) {
            config.set("messages.en.kill.success", "&7You have killed &e{player}&a successfully&7.");
        }
        if (!config.contains("messages.en.kill.all-success")) {
            config.set("messages.en.kill.all-success", "&7All players have been &asuccessfully&7 killed.");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("kill.only-players")));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("prefix") + getMessage("kill.usage")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "all":
            case "@a":
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.setHealth(0); // Spieler töten
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("prefix") + getMessage("kill.all-success")));
                break;
            default:
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    target.setHealth(0); // Zielspieler töten
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("prefix") + getMessage("kill.success").replace("{player}", target.getName())));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("prefix") + getMessage("kill.no-player").replace("{player}", args[0])));
                }
                break;
        }
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("all");
            completions.add("@a");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(onlinePlayer.getName());
                }
            }
        }
        return completions;
    }
}