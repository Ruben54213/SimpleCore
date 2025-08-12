package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TeleportHereCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public TeleportHereCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();

        PluginCommand cmd = plugin.getCommand("tph");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.tph.active")) config.set("modules.tph.active", true);
        if (!config.contains("prefix")) config.set("prefix", "&bSimple&fCore &8» ");
        if (!config.contains("language")) config.set("language", "de");

        // DE
        setDefault("messages.de.tph.only-players", "&7Diesen Befehl dürfen nur &eSpieler&7 ausführen.");
        setDefault("messages.de.tph.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        setDefault("messages.de.tph.module-disabled", "&7Dieses Modul ist zurzeit &cdeaktiviert&7.");
        setDefault("messages.de.tph.usage", "&aKorrekte&7 Verwendung: &e/tph &7<&eSpieler&7>.");
        setDefault("messages.de.tph.no-player", "&7Der Spieler &e{player}&7 wurde &cnicht&7 gefunden.");
        setDefault("messages.de.tph.success-self", "&7Du hast &a{player}&7 zu dir teleportiert.");
        setDefault("messages.de.tph.target-info", "&7Du wurdest zu &a{sender}&7 teleportiert.");

        // EN
        setDefault("messages.en.tph.only-players", "&7Only &eplayers&7 can execute this command.");
        setDefault("messages.en.tph.no-permission", "&7You do not have &cpermission&7 for that.");
        setDefault("messages.en.tph.module-disabled", "&7This module is currently &cdisabled&7.");
        setDefault("messages.en.tph.usage", "&aCorrect&7 usage: &e/tph &7<&eplayer&7>.");
        setDefault("messages.en.tph.no-player", "&7The player &e{player}&7 was &cnot&7 found.");
        setDefault("messages.en.tph.success-self", "&7You teleported &a{player}&7 to you.");
        setDefault("messages.en.tph.target-info", "&7You were teleported to &a{sender}&7.");

        plugin.saveConfig();
    }

    private void setDefault(String path, String value) {
        if (!config.contains(path)) config.set(path, value);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sendPath(sender, "messages." + getLang() + ".tph.only-players");
            return true;
        }
        Player executor = (Player) sender;

        if (!config.getBoolean("modules.tph.active", true)) {
            sendPath(executor, "messages." + getLang() + ".tph.module-disabled");
            return true;
        }

        if (!executor.hasPermission("simplecore.tph")) {
            sendPath(executor, "messages." + getLang() + ".tph.no-permission");
            return true;
        }

        if (args.length != 1) {
            sendPath(executor, "messages." + getLang() + ".tph.usage");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            String msg = get("messages." + getLang() + ".tph.no-player").replace("{player}", args[0]);
            sendMsg(executor, msg);
            return true;
        }

        Location here = executor.getLocation();
        target.teleport(here);

        String okExec = get("messages." + getLang() + ".tph.success-self").replace("{player}", target.getName());
        sendMsg(executor, okExec);

        String infoTarget = get("messages." + getLang() + ".tph.target-info").replace("{sender}", executor.getName());
        sendMsg(target, infoTarget);

        return true;
    }

    private String getLang() {
        return config.getString("language", "de");
    }

    private String getPrefix() {
        return config.getString("prefix", "&bSimple&fCore &8» ");
    }

    private String get(String path) {
        return config.getString(path, "&7Es ist ein &cFehler&7 aufgetreten (&e" + path + "&7).");
    }

    private String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    // === Helfer für sauberes Senden ===
    private void sendPath(CommandSender to, String path) {
        to.sendMessage(colorize(getPrefix() + get(path)));
    }

    private void sendMsg(CommandSender to, String msg) {
        to.sendMessage(colorize(getPrefix() + msg));
    }

    // Tab-Completion: Spielerliste (nur erstes Argument)
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String start = args[0].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                String n = p.getName();
                if (n.toLowerCase().startsWith(start)) list.add(n);
            }
        }
        return list;
    }
}
