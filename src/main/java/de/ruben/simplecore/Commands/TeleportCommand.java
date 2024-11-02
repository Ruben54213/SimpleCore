package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<UUID, UUID> teleportRequests = new HashMap<>();
    private final Map<UUID, UUID> teleportHereRequests = new HashMap<>();

    public TeleportCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.teleport.active")) {
            config.set("modules.teleport.active", true);
        }
        // Deutsch
        if (!config.contains("messages.de.teleport.success")) {
            config.set("messages.de.teleport.success", "&7Du wurdest &aerfolgreich&7 teleportiert.");
        }
        if (!config.contains("messages.de.teleport.no-permission")) {
            config.set("messages.de.teleport.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.teleport.no-player")) {
            config.set("messages.de.teleport.no-player", "&7Der Spieler &e{player}&7 wurde &cnicht&7 gefunden.");
        }
        if (!config.contains("messages.de.teleport.invalid-coordinates")) {
            config.set("messages.de.teleport.invalid-coordinates", "&7Du hast &cUngültige Koordinaten &7angegeben.");
        }
        if (!config.contains("messages.de.teleport.no-request")) {
            config.set("messages.de.teleport.no-request", "&7Du hast &ckeine &eTeleportanfrage&7.");
        }
        if (!config.contains("messages.de.teleport.override.usage")) {
            config.set("messages.de.teleport.override.usage", "&aKorrekte&7 Verwendung: &e/tpo &7<&eSpieler&7>.");
        }

        // Englisch
        if (!config.contains("messages.en.teleport.success")) {
            config.set("messages.en.teleport.success", "&7You have been &asuccessfully&7 teleported.");
        }
        if (!config.contains("messages.en.teleport.no-permission")) {
            config.set("messages.en.teleport.no-permission", "&7You do not have &cpermission&7 for this.");
        }
        if (!config.contains("messages.en.teleport.no-player")) {
            config.set("messages.en.teleport.no-player", "&7The player &e{player}&7 was &cnot&7 found.");
        }
        if (!config.contains("messages.en.teleport.invalid-coordinates")) {
            config.set("messages.en.teleport.invalid-coordinates", "&7You have entered &cinvalid coordinates&7.");
        }
        if (!config.contains("messages.en.teleport.no-request")) {
            config.set("messages.en.teleport.no-request", "&7You have &cno &eteleport request&7.");
        }
        if (!config.contains("messages.en.teleport.override.usage")) {
            config.set("messages.en.teleport.override.usage", "&aCorrect&7 usage: &e/tpo &7<&eplayer&7>.");
        }

        if (!config.contains("messages.de.teleport.only-players")) {
            config.set("messages.en.teleport.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.teleport.only-players")) {
            config.set("messages.en.teleport.only-players", "Only players can execute this command!");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("teleport.only-players")));
            return true;
        }
        Player player = (Player) sender;
        if (!config.getBoolean("modules.teleport.active")) {
            player.sendMessage(getMessage("modules.inactive"));
            return true;
        }

        switch (label.toLowerCase()) {
            case "teleport":
            case "tp":
                if (args.length == 1) {
                    handleTeleport(player, args[0]);
                } else if (args.length == 3) {
                    handleCoordinatesTeleport(player, args);
                } else {
                    player.sendMessage(config.getString("prefix") + getMessage("teleport.override.usage"));
                }
                break;
            case "tpa":
                if (player.hasPermission("simplecore.tpa")) {
                    handleTpa(player, args);
                } else {
                    player.sendMessage(config.getString("prefix") + getMessage("teleport.no-permission"));
                }
                break;
            case "tpaaccept":
                if (player.hasPermission("simplecore.tpaaccept")) {
                    handleTpaAccept(player);
                } else {
                    player.sendMessage(config.getString("prefix") + getMessage("teleport.no-permission"));
                }
                break;
            case "tpahere":
                if (player.hasPermission("simplecore.tpahere")) {
                    handleTpahere(player, args);
                } else {
                    player.sendMessage(config.getString("prefix") + getMessage("teleport.no-permission"));
                }
                break;
            case "tpo":
                if (player.hasPermission("simplecore.tpo")) {
                    handleTeleportOverride(player, args);
                } else {
                    player.sendMessage(config.getString("prefix") + getMessage("teleport.no-permission"));
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private void handleTeleport(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            player.teleport(target);
            player.sendMessage(config.getString("prefix") + getMessage("teleport.success"));
        } else {
            player.sendMessage(config.getString("prefix") + getMessage("teleport.no-player").replace("{player}", targetName));
        }
    }

    private void handleCoordinatesTeleport(Player player, String[] args) {
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            Location location = new Location(player.getWorld(), x, y, z);
            player.teleport(location);
            player.sendMessage(config.getString("prefix") + getMessage("teleport.success"));
        } catch (NumberFormatException e) {
            player.sendMessage(config.getString("prefix") + getMessage("teleport.invalid-coordinates"));
        }
    }

    private void handleTpa(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(config.getString("prefix") + getMessage("teleport.tpa.usage"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null && !target.equals(player)) {
            teleportRequests.put(target.getUniqueId(), player.getUniqueId());
            target.sendMessage(config.getString("prefix") + getMessage("teleport.tpa.request-received").replace("{player}", player.getName()));
            player.sendMessage(config.getString("prefix") + getMessage("teleport.tpa.request-sent").replace("{player}", target.getName()));
        } else {
            player.sendMessage(config.getString("prefix") + ("teleport.no-player").replace("{player}", args[0]));
        }
    }

    private void handleTpaAccept(Player player) {
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
    }

    private void handleTpahere(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(getMessage("teleport.tpahere.usage"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null && !target.equals(player)) {
            teleportHereRequests.put(target.getUniqueId(), player.getUniqueId());
            target.sendMessage(getMessage("teleport.tpa.request-received").replace("{player}", player.getName()));
            player.sendMessage(getMessage("teleport.tpa.request-sent").replace("{player}", target.getName()));
        } else {
            player.sendMessage(getMessage("teleport.no-player").replace("{player}", args[0]));
        }
    }

    private void handleTeleportOverride(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(getMessage("teleport.override.usage"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            player.teleport(target);
            player.sendMessage(getMessage("teleport.success"));
        } else {
            player.sendMessage(getMessage("teleport.no-player").replace("{player}", args[0]));
        }
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages." + lang + "." + path, ""));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
