package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class LightningCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public LightningCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.lightning.active")) {
            config.set("modules.lightning.active", true);
        }
        if (!config.contains("messages.de.lightning.no-permission")) {
            config.set("messages.de.lightning.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.lightning.no-player")) {
            config.set("messages.de.lightning.no-player", "&7Der angegebene Spieler existiert&c nicht&7.");
        }
        if (!config.contains("messages.de.lightning.success")) {
            config.set("messages.de.lightning.success", "&7Ein &eBlitz&7 wurde bei &e{target}&7 gespawnt.");
        }
        if (!config.contains("messages.en.lightning.no-permission")) {
            config.set("messages.en.lightning.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.lightning.no-player")) {
            config.set("messages.en.lightning.no-player", "&7The specified player does &cnot&7 exist.");
        }
        if (!config.contains("messages.en.lightning.success")) {
            config.set("messages.en.lightning.success", "&7A &elightning bolt&7 has been spawned at &e{target}&7.");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("lightning.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Check if the module is active
        if (!config.getBoolean("modules.lightning.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("lightning.inactive")));
            return true;
        }

        // Check if the player has permission
        if (!player.hasPermission("simplecore.lightning")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("lightning.no-permission")));
            return true;
        }

        // Check if the player name is provided
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("lightning.no-player")));
            return true;
        }

        // Find the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("lightning.no-player")));
            return true;
        }

        // Spawn lightning at the target player's location
        Location targetLocation = target.getLocation();
        target.getWorld().strikeLightning(targetLocation);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("lightning.success").replace("{target}", target.getName())));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");  // Default to German
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix", "") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}