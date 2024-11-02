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

import java.util.*;

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<UUID, UUID> teleportRequests = new HashMap<>();

    public TpaCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.teleport.active")) {
            config.set("modules.teleport.active", true);
        }

        // Deutsch
        if (!config.contains("messages.de.teleport.tpa.request-sent")) {
            config.set("messages.de.teleport.tpa.request-sent", "&7Du hast eine &eTeleportanfrage&7 an &e{player}&7 gesendet.");
        }
        if (!config.contains("messages.de.teleport.tpa.request-received")) {
            config.set("messages.de.teleport.tpa.request-received", "&e{player} &7möchte sich zu dir teleportieren. Verwende &e/tpaaccept&7, um die Anfrage &aanzunehmen&7.");
        }
        if (!config.contains("messages.de.teleport.tpa.accepted")) {
            config.set("messages.de.teleport.tpa.accepted", "&7Deine &eTeleportanfrage&7 wurde von &e{player}&7 akzeptiert.");
        }
        if (!config.contains("messages.de.teleport.no-request")) {
            config.set("messages.de.teleport.no-request", "&7Du hast &ckeine &eTeleportanfrage&7.");
        }
        if (!config.contains("messages.de.teleport.no-player")) {
            config.set("messages.de.teleport.no-player", "&7Der Spieler &e{player}&7 wurde &cnicht&7 gefunden.");
        }

        // Englisch
        if (!config.contains("messages.en.teleport.tpa.request-sent")) {
            config.set("messages.en.teleport.tpa.request-sent", "&7You have sent a &eteleport request&7 to &e{player}&7.");
        }
        if (!config.contains("messages.en.teleport.tpa.request-received")) {
            config.set("messages.en.teleport.tpa.request-received", "&e{player} &7wants to teleport to you. Use &e/tpaaccept&7 to &aaccept&7 the request.");
        }
        if (!config.contains("messages.en.teleport.tpa.accepted")) {
            config.set("messages.en.teleport.tpa.accepted", "&7Your &eteleport request&7 was accepted by &e{player}&7.");
        }
        if (!config.contains("messages.en.teleport.no-request")) {
            config.set("messages.en.teleport.no-request", "&7You have &cno &eteleport request&7.");
        }
        if (!config.contains("messages.en.teleport.no-player")) {
            config.set("messages.en.teleport.no-player", "&7The player &e{player}&7 was &cnot&7 found.");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("only-players")));
            return true;
        }

        Player player = (Player) sender;

        if (!config.getBoolean("modules.teleport.active")) {
            player.sendMessage(getMessage("modules.inactive"));
            return true;
        }

        switch (label.toLowerCase()) {
            case "tpa":
                return handleTpa(player, args);
            case "tpaaccept":
                return handleTpaAccept(player);
            case "tpahere":
                return handleTpaHere(player, args);
            default:
                return false;
        }
    }

    private boolean handleTpa(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(getMessage("teleport.tpa.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target != null && !target.equals(player)) {
            teleportRequests.put(target.getUniqueId(), player.getUniqueId());
            target.sendMessage(getMessage("teleport.tpa.request-received").replace("{player}", player.getName()));
            player.sendMessage(getMessage("teleport.tpa.request-sent").replace("{player}", target.getName()));
        } else {
            player.sendMessage(getMessage("teleport.no-player").replace("{player}", args[0]));
        }
        return true;
    }

    private boolean handleTpaAccept(Player player) {
        UUID requesterUUID = teleportRequests.remove(player.getUniqueId());
        if (requesterUUID != null) {
            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null) {
                requester.teleport(player.getLocation());
                requester.sendMessage(getMessage("teleport.tpa.accepted").replace("{player}", player.getName()));
                player.sendMessage(getMessage("teleport.success"));
            } else {
                player.sendMessage(getMessage("teleport.no-player"));
            }
        } else {
            player.sendMessage(getMessage("teleport.no-request"));
        }
        return true;
    }

    private boolean handleTpaHere(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(getMessage("teleport.tpahere.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target != null && !target.equals(player)) {
            teleportRequests.put(player.getUniqueId(), target.getUniqueId());
            target.sendMessage(getMessage("teleport.tpa.request-received").replace("{player}", player.getName()));
            player.sendMessage(getMessage("teleport.tpahere.request-sent").replace("{player}", target.getName()));
        } else {
            player.sendMessage(getMessage("teleport.no-player").replace("{player}", args[0]));
        }
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        String message = config.getString("messages." + lang + "." + path, "");
        if (message.isEmpty()) {
            // Debugging-Nachricht, wenn keine Nachricht gefunden wird
            Bukkit.getLogger().warning("Message not found for path: " + path);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(onlinePlayer.getName());
                }
            }
        }
        return completions;
    }
}