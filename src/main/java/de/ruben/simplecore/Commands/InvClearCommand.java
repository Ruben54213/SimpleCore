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

import java.util.Arrays;

public class InvClearCommand implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public InvClearCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("invclear").setExecutor(this);
        plugin.getCommand("invclear").setAliases(Arrays.asList("clear"));
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.de.invclear.active")) {
            config.set("modules.invclear.active", true);
        }
        if (!config.contains("modules.en.invclearactive")) {
            config.set("modules.invclear.active", true);
        }
        setDefaultMessages();
        plugin.saveConfig();
    }

    private void setDefaultMessages() {
        String[][] messages = {
                {"messages.de.invclear.self", "&7Dein &eInventar &7wurde geleert."},
                {"messages.en.invclear.self", "&7Your inventory has been cleared."},
                {"messages.de.invclear.usage", "&cFalsche&7 Benutzung: Benutze &e/invclear &7<&eSpieler&7>."},
                {"messages.en.invclear.usage", "&cWrong&7 usage: Use &e/invclear &7<&eplayer&7>."},
                {"messages.de.invclear.other", "&7Das Inventar von &e{player}&7 wurde geleert."},
                {"messages.en.invclear.other", "&7Cleared the inventory of &e{player}&7."},
                {"messages.de.invclear.only-players", "&cDiesen Befehl dürfen nur Spieler ausführen!"},
                {"messages.en.invclear.only-players", "&cOnly players can execute this command!"},
                {"messages.de.invclear.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung."},
                {"messages.en.invclear.no-permission", "&7You do not have &cpermission&7 for that."},
                {"messages.de.invclear.no-player", "&7Dieser &eSpieler&7 wurde &cnicht&7 gefunden!"},
                {"messages.en.invclear.no-player", "&7This &eplayer&7 was &cnot&7 found!"},
        };

        for (String[] message : messages) {
            if (!config.contains(message[0])) {
                config.set(message[0], message[1]);
            }
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".invclear.only-players")));
            return true;
        }

        Player player = (Player) sender;

        if (!config.getBoolean("modules.invclear.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        if (args.length == 0) {
            player.getInventory().clear();
            sendMessage(player, "self");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessage(player, "no-player");
                return true;
            }
            if (player.hasPermission("simplecore.invclear.other")) {
                target.getInventory().clear();
                sendMessage(player, "other", target.getName());
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".invclear.no-permission")));
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
        String message = config.getString("messages." + language + ".invclear." + key, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7.");

        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}
