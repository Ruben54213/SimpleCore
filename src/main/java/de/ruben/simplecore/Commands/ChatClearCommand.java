package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatClearCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ChatClearCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.de.chatclear.active")) {
            config.set("modules.chatclear.active", true);
        }
        if (!config.contains("modules.en.chatclear.active")) {
            config.set("modules.chatclear.active", true);
        }
        if (!config.contains("messages.de.chatclear.info")) {
            config.set("messages.de.chatclear.info", "&7Der Chat wurde von &e{user}&7 geleert.");
        }
        if (!config.contains("messages.en.chatclear.info")) {
            config.set("messages.en.chatclear.info", "&7The chat has been cleared by &e{user}&7.");
        }
        if (!config.contains("messages.de.chatclear.sender-info")) {
            config.set("messages.de.chatclear.sender-info", "&7Du hast nun &aerfolgreich&7 den &eChat&7 gecleart.");
        }
        if (!config.contains("messages.en.chatclear.sender-info")) {
            config.set("messages.en.chatclear.sender-info", "&7You have &asuccessfully&7 cleared the &echat&7.");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("modules.chatclear.active")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        String userName = sender instanceof Player ? sender.getName() : "Console";
        String infoMessage = ChatColor.translateAlternateColorCodes('&', config.getString("broadcastprefix") + config.getString("messages." + config.getString("language") + ".chatclear.info").replace("{user}", userName));

        for (int i = 0; i < 300; i++) {
            Bukkit.broadcastMessage(" ");
        }

        // Broadcasts the info message after chat clearance
        Bukkit.broadcastMessage(infoMessage);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".chatclear.sender-info")));
        return true;
    }
}
