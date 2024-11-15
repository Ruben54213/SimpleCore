package de.ruben.simplecore.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MOTDListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private String line1;
    private String line2;
    private boolean isActive;

    public MOTDListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        loadMotdFromConfig();

        // Register the event listener only if the module is active
        if (isActive) {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    // Default Config Setup
    private void setupDefaultConfig() {
        if (!config.contains("modules.motd.active")) {
            config.set("modules.motd.active", false);  // Default is inactive
        }
        if (!config.contains("motd.line1")) {
            config.set("motd.line1", "&e&lYOURSERVER.NET&7 | &b&lJoin Us!");
        }
        if (!config.contains("motd.line2")) {
            config.set("motd.line2", "&e&lWelcome &7 to the &a&lServer&7!");
        }
        plugin.saveConfig();
    }

    // Load MOTD from the config
    private void loadMotdFromConfig() {
        this.isActive = config.getBoolean("modules.motd.active", false);
        this.line1 = ChatColor.translateAlternateColorCodes('&', config.getString("motd.line1", "&e&lYOURSERVER.NET&7 | &b&lJoin Us!"));
        this.line2 = ChatColor.translateAlternateColorCodes('&', config.getString("motd.line2", "&e&lWelcome &7 to the &a&lServer&7!"));
    }

    // Event handler to set the MOTD dynamically, only if the module is active
    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (isActive) {
            event.setMotd(line1 + "\n" + line2);
        }
    }
}
