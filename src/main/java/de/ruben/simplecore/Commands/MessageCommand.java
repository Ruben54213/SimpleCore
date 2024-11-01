package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<Player, Player> lastMessageSender = new HashMap<>();
    private boolean isEnabled;

    public MessageCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        this.isEnabled = config.getBoolean("commands.msg.enabled", true);
        if (isEnabled) {
            plugin.getCommand("msg").setExecutor(this);
            plugin.getCommand("msg").setTabCompleter(this);
            plugin.getCommand("r").setExecutor(this);
        }
    }

    private void setupDefaultConfig() {
        // Einstellung zur Aktivierung/Deaktivierung des Commands
        if (!config.contains("modules.message.active")) {
            config.set("commands.msg.enabled", true);
        }

        // Standardwerte für Nachrichten auf Deutsch
        if (!config.contains("messages.de.msg.no-player")) {
            config.set("messages.de.msg.no-player", "&7Der Spieler &e{name}&7 ist &cnicht&7 online.");
        }
        if (!config.contains("messages.de.msg.self-message")) {
            config.set("messages.de.msg.self-message", "&7Du kannst dir &eselbst &ckeine &7Nachricht senden.");
        }
        if (!config.contains("messages.de.msg.no-reply")) {
            config.set("messages.de.msg.no-reply", "&7Es gibt &cniemanden&7, dem du antworten kannst.");
        }
        if (!config.contains("messages.de.msg.usage")) {
            config.set("messages.de.msg.usage", "&cFalsche&7 Verwendung! Verwende &e/msg &7(&eSpieler&7) (&eNachricht&7).");
        }
        if (!config.contains("messages.de.msg.sent")) {
            config.set("messages.de.msg.sent", "&fDu &7» &f{name} &8| &f{message}");
        }
        if (!config.contains("messages.de.msg.received")) {
            config.set("messages.de.msg.received", "&e{name} &7» &eDir &8| &f{message}");
        }

        // Standardwerte für Nachrichten auf Englisch
        if (!config.contains("messages.en.msg.no-player")) {
            config.set("messages.en.msg.no-player", "&7The player &e{name}&7 is &cnot&7 online.");
        }
        if (!config.contains("messages.en.msg.self-message")) {
            config.set("messages.en.msg.self-message", "&7You &ccannot&7 send a message to &eyourself&7.");
        }
        if (!config.contains("messages.en.msg.no-reply")) {
            config.set("messages.en.msg.no-reply", "&7There is &cno one&7 to reply to.");
        }
        if (!config.contains("messages.en.msg.usage")) {
            config.set("messages.en.msg.usage", "&cIncorrect&7 usage! Use &e/msg &7(&eplayer&7) (&emessage&7).");
        }
        if (!config.contains("messages.en.msg.sent")) {
            config.set("messages.en.msg.sent", "&fYou &7» &f{name} &8| &f{message}");
        }
        if (!config.contains("messages.en.msg.received")) {
            config.set("messages.en.msg.received", "&e{name} &7» &eYou &8| &f{message}");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isEnabled) {
            sender.sendMessage(ChatColor.RED + "This command is currently disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".only-players"));
            return true;
        }

        Player player = (Player) sender;

        // /msg Command
        if (command.getName().equalsIgnoreCase("msg")) {
            if (args.length < 2) {
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".msg.usage"));
                return false;
            }

            Player target = plugin.getServer().getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".msg.no-player").replace("{name}", args[0]));
                return true;
            }

            if (target.equals(player)) {
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".msg.self-message"));
                return true;
            }

            String message = buildMessage(args, 1);
            sendPrivateMessage(player, target, message);
            lastMessageSender.put(target, player);
            lastMessageSender.put(player, target);

            return true;
        }

        // /r Command (Antwort an den letzten Sender)
        if (command.getName().equalsIgnoreCase("r")) {
            Player lastSender = lastMessageSender.get(player);
            if (lastSender == null || !lastSender.isOnline()) {
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".msg.no-reply"));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(getConfigMessage("messages." + getConfigMessage("language") + ".msg.usage"));
                return false;
            }

            String message = buildMessage(args, 0);
            sendPrivateMessage(player, lastSender, message);
            lastMessageSender.put(player, lastSender);

            return true;
        }

        return false;
    }

    // Methode zum Senden der privaten Nachricht
    private void sendPrivateMessage(Player sender, Player receiver, String message) {
        String senderMessage = getConfigMessage("messages." + getConfigMessage("language") + ".msg.sent")
                .replace("{name}", receiver.getName())
                .replace("{message}", message);
        sender.sendMessage(senderMessage);

        String receiverMessage = getConfigMessage("messages." + getConfigMessage("language") + ".msg.received")
                .replace("{name}", sender.getName())
                .replace("{message}", message);
        receiver.sendMessage(receiverMessage);
    }

    private String buildMessage(String[] args, int startIndex) {
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) {
                messageBuilder.append(" ");
            }
        }
        return messageBuilder.toString();
    }

    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString(path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null; // Für zukünftige Implementierungen von Tab-Vervollständigungen
    }
}