package de.ruben.simplecore.Commands;

import de.ruben.simplecore.SimpleCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class BroadcastCommand implements CommandExecutor {
    private final FileConfiguration config;
    private final SimpleCore plugin;

    public BroadcastCommand(SimpleCore plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        // Prüfen und setzen von Standardwerten, falls sie fehlen
        boolean configUpdated = false;

        if (!config.contains("broadcastprefix")) {
            config.set("broadcastprefix", "&b&lSimple&f&lCore &8» &f");
            configUpdated = true;
        }

        if (!config.contains("modules.broadcast.active")) {
            config.set("modules.broadcast.active", true);
            configUpdated = true;
        }

        if (!config.contains("modules.broadcast.addlines")) {
            config.set("modules.broadcast.addlines", true);
            configUpdated = true;
        }

        if (!config.contains("messages." + config.getString("language") + ".broadcast.usage")) {
            config.set("messages." + config.getString("language") + ".broadcast.usage", "&7Du &cmusst&7 eine Nachricht angeben!");
            configUpdated = true;
        }

        if (!config.contains("modules.broadcast.sound")) {
            config.set("modules.broadcast.sound", "ENTITY_PLAYER_LEVELUP");
            configUpdated = true;
        }

        if (!config.contains("modules.broadcast.sound_volume")) {
            config.set("modules.broadcast.sound_volume", 1.0);
            configUpdated = true;
        }

        if (!config.contains("modules.broadcast.sound_pitch")) {
            config.set("modules.broadcast.sound_pitch", 1.0);
            configUpdated = true;
        }

        // Konfiguration speichern, falls sie geändert wurde
        if (configUpdated) {
            plugin.saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Überprüfen, ob der Broadcast-Befehl aktiviert ist
        if (!config.getBoolean("modules.broadcast.active", true)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        if (args.length == 0) {
            String usageMessage = config.getString("messages." + config.getString("language") + ".broadcast.usage");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + usageMessage));
            return false;
        }

        // Nachricht aus den Argumenten zusammenfügen
        String message = String.join(" ", args);
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Prefix aus der Konfiguration holen
        String prefix = config.getString("broadcastprefix", "&b&lSimple&f&lCore &8» &f");

        // Prüfen, ob leere Zeilen über und unter der Nachricht hinzugefügt werden sollen
        boolean addLines = config.getBoolean("modules.broadcast.addlines", true);
        if (addLines) {
            Bukkit.broadcastMessage(""); // Leere Zeile oben
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + message);
        if (addLines) {
            Bukkit.broadcastMessage(""); // Leere Zeile unten
        }

        // Sound-Konfiguration auslesen
        String soundName = config.getString("modules.broadcast.sound");
        float volume = (float) config.getDouble("modules.broadcast.sound_volume", 1.0);
        float pitch = (float) config.getDouble("modules.broadcast.sound_pitch", 1.0);

        // Sound abspielen, falls dieser existiert
        try {
            Sound sound = Sound.valueOf(soundName);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Config-Error with Broadcast-Sounds: " + soundName);
            sender.sendMessage(config.getString("messages." + config.getString("language") + ".broadcast.sound-error"));
        }

        return true;
    }
}