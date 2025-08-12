package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class FlyspeedCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public FlyspeedCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();

        PluginCommand cmd = plugin.getCommand("flyspeed");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.flyspeed.active")) config.set("modules.flyspeed.active", true);
        if (!config.contains("prefix")) config.set("prefix", "&bSimple&fCore &8» ");
        if (!config.contains("language")) config.set("language", "de");

        // DE
        setDefault("messages.de.flyspeed.only-players", "&7Diesen Befehl dürfen nur &eSpieler&7 ausführen.");
        setDefault("messages.de.flyspeed.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        setDefault("messages.de.flyspeed.no-permission-other", "&7Dir fehlt die Berechtigung (&eanderen Spieler setzen&7).");
        setDefault("messages.de.flyspeed.module-disabled", "&7Dieses Modul ist zurzeit &cdeaktiviert&7.");
        setDefault("messages.de.flyspeed.usage", "&cFalsche&7 Verwendung: &e/flyspeed &7<&e1-10&7> [&eSpieler&7].");
        setDefault("messages.de.flyspeed.invalid-speed", "&7Bitte gib eine Zahl von &e1 &7bis &e10&7 an.");
        setDefault("messages.de.flyspeed.player-not-found", "&7Der Spieler &e{player}&7 wurde &cnicht&7 gefunden.");
        setDefault("messages.de.flyspeed.success-self", "&7Fluggeschwindigkeit auf &e{speed}&7 gesetzt.");
        setDefault("messages.de.flyspeed.success-other", "&7Fluggeschwindigkeit von &e{player}&7 auf &e{speed}&7 gesetzt.");

        // EN
        setDefault("messages.en.flyspeed.only-players", "&7Only &eplayers&7 can execute this command.");
        setDefault("messages.en.flyspeed.no-permission", "&7You do not have &cpermission&7.");
        setDefault("messages.en.flyspeed.no-permission-other", "&7You lack permission to set &eother players'&7 speed.");
        setDefault("messages.en.flyspeed.module-disabled", "&7This module is currently &cdisabled&7.");
        setDefault("messages.en.flyspeed.usage", "&cWrong&7 usage: &e/flyspeed &7<&e1-10&7> [&eplayer&7].");
        setDefault("messages.en.flyspeed.invalid-speed", "&7Please enter a number between &e1 &7and &e10&7.");
        setDefault("messages.en.flyspeed.player-not-found", "&7Player &e{player}&7 &cnot&7 found.");
        setDefault("messages.en.flyspeed.success-self", "&7Flight speed set to &e{speed}&7.");
        setDefault("messages.en.flyspeed.success-other", "&7Set &e{player}'s&7 flight speed to &e{speed}&7.");

        plugin.saveConfig();
    }

    private void setDefault(String path, String value) {
        if (!config.contains(path)) config.set(path, value);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!config.getBoolean("modules.flyspeed.active", true)) {
            sendPath(sender, "messages." + lang() + ".flyspeed.module-disabled");
            return true;
        }

        // Konsole braucht Zielspieler
        if (!(sender instanceof Player) && args.length < 2) {
            sendPath(sender, "messages." + lang() + ".flyspeed.usage");
            return true;
        }

        if (args.length < 1) {
            sendPath(sender, "messages." + lang() + ".flyspeed.usage");
            return true;
        }

        Integer lvl = parseInt(args[0]);
        if (lvl == null || lvl < 1 || lvl > 10) {
            sendPath(sender, "messages." + lang() + ".flyspeed.invalid-speed");
            return true;
        }

        float spigotSpeed = lvl / 10.0f; // 1..10 -> 0.1..1.0

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sendPath(sender, "messages." + lang() + ".flyspeed.usage");
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("simplecore.flyspeed")) {
                sendPath(p, "messages." + lang() + ".flyspeed.no-permission");
                return true;
            }
            setFlySpeed(p, spigotSpeed);
            String msg = get("messages." + lang() + ".flyspeed.success-self").replace("{speed}", String.valueOf(lvl));
            sendMsg(p, msg);
            return true;
        } else {
            if (!sender.hasPermission("simplecore.flyspeed.other")) {
                sendPath(sender, "messages." + lang() + ".flyspeed.no-permission-other");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String m = get("messages." + lang() + ".flyspeed.player-not-found").replace("{player}", args[1]);
                sendMsg(sender, m);
                return true;
            }
            setFlySpeed(target, spigotSpeed);
            String toSender = get("messages." + lang() + ".flyspeed.success-other")
                    .replace("{player}", target.getName())
                    .replace("{speed}", String.valueOf(lvl));
            sendMsg(sender, toSender);

            String toTarget = get("messages." + lang() + ".flyspeed.success-self")
                    .replace("{speed}", String.valueOf(lvl));
            sendMsg(target, toTarget);
            return true;
        }
    }

    private void setFlySpeed(Player p, float speed) {
        try {
            p.setFlySpeed(speed);
        } catch (IllegalArgumentException ignored) {
            p.setFlySpeed(0.1f);
        }
    }

    // ===== Utils =====
    private String lang() { return config.getString("language", "de"); }
    private String prefix() { return config.getString("prefix", "&bSimple&fCore &8» "); }
    private String color(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
    private String get(String path) { return config.getString(path, "&7Es ist ein &cFehler&7 aufgetreten (&e" + path + "&7)."); }
    private void sendPath(CommandSender to, String path) { to.sendMessage(color(prefix() + get(path))); }
    private void sendMsg(CommandSender to, String msg) { to.sendMessage(color(prefix() + msg)); }
    private Integer parseInt(String s) { try { return Integer.parseInt(s); } catch (Exception e) { return null; } }

    // Tab-Complete: <1-10> [player]
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (int i = 1; i <= 10; i++) list.add(String.valueOf(i));
        } else if (args.length == 2) {
            String start = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(start)) list.add(p.getName());
            }
        }
        return list;
    }
}
