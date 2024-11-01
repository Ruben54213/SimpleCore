package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class HealCommand implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public HealCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("heal").setExecutor(this);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.de.heal.active")) {
            config.set("modules.heal.active", true);
        }
        if (!config.contains("modules.en.heal.active")) {
            config.set("modules.heal.active", true);
        }
        setDefaultMessages();
        plugin.saveConfig();
    }

    private void setDefaultMessages() {
        String[][] messages = {
                {"messages.de.heal.self", "&7Du hast dich selbst &egeheilt&7."},
                {"messages.en.heal.self", "&7You have &ehealed&7 yourself."},
                {"messages.de.heal.usage", "&cFalsche&7 Benutzung: Benutze &e/heal &7<&eSpieler&7>."},
                {"messages.en.heal.usage", "&cWrong&7 usage: Use &e/heal &7<&eplayer&7>."},
                {"messages.de.heal.other", "&7Du hast &e{player}&7 geheilt."},
                {"messages.en.heal.other", "&7You have healed &e{player}&7."},
                {"messages.de.heal.only-players", "Diesen Befehl dürfen nur Spieler ausführen!"},
                {"messages.en.heal.only-players", "Only players can execute this command!"},
                {"messages.de.heal.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung."},
                {"messages.en.heal.no-permission", "&7You do not have &cpermission&7 for that."},
                {"messages.de.heal.no-player", "&7Dieser &eSpieler&7 wurde &cnicht&7 gefunden!"},
                {"messages.en.heal.no-player", "&7This &eplayer&7 was &cnot&7 found!"}
        };

        for (String[] message : messages) {
            if (!config.contains(message[0])) {
                config.set(message[0], message[1]);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".heal.only-players")));
            return true;
        }

        Player player = (Player) sender;

        if (!config.getBoolean("modules.heal.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        if (args.length == 0) {
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
            sendMessage(player, "self");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessage(player, "no-player");
                return true;
            }
            // Überprüfen, ob der Spieler die Berechtigung hat, andere zu heilen
            if (player.hasPermission("simplecore.heal.other")) {
                target.setHealth(target.getMaxHealth());
                target.setFireTicks(0);
                sendMessage(player, "other", target.getName());
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".heal.no-permission")));
            }
        } else {
            sendMessage(player, "usage");
        }
        return true;
    }

    private void sendMessage(Player player, String messageKey, String... placeholders) {
        String message = getMessage(messageKey);
        if (placeholders.length > 0) {
            message = message.replace("{player}", placeholders[0]);
        }
        player.sendMessage(message);
    }

    private String getMessage(String key) {
        String language = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + language + ".heal." + key, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7.");
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}