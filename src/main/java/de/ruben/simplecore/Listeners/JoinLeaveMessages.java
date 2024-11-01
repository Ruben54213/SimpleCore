package de.ruben.simplecore.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinLeaveMessages implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public JoinLeaveMessages(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.joinleave.active")) {
            config.set("modules.custom-messages.active", true);
        }
        if (!config.contains("messages.de.joinleave.join")) {
            config.set("messages.join", "&7[&a»&7] &f{name}");
        }
        if (!config.contains("messages.de.joinleave.leave")) {
            config.set("messages.leave", "&7[&c«&7] &f{name}");
        }
        if (!config.contains("messages.en.joinleave.join")) {
            config.set("messages.join", "&7[&a»&7] &f{name}");
        }
        if (!config.contains("messages.en.joinleave.leave")) {
            config.set("messages.leave", "&7[&c«&7] &f{name}");
        }
        plugin.saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!config.getBoolean("modules.joinleave.active")) return;

        String joinMessage = config.getString("messages." + config.getString("language") + ".join", "&7[&a»&7] &f{name}");
        joinMessage = ChatColor.translateAlternateColorCodes('&', joinMessage.replace("{name}", event.getPlayer().getName()));
        event.setJoinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!config.getBoolean("modules.joinleave.active")) return;

        String leaveMessage = config.getString("messages." + config.getString("language") + ".leave", "&7[&c«&7] &f{name}");
        leaveMessage = ChatColor.translateAlternateColorCodes('&', leaveMessage.replace("{name}", event.getPlayer().getName()));
        event.setQuitMessage(leaveMessage);
    }
}
