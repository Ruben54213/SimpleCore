package de.ruben.simplecore.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SignChangeListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public SignChangeListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.colorsign.active")) {
            config.set("modules.colorsign.active", true);
        }
        plugin.saveConfig();
    }


    @EventHandler
    public void onChange(SignChangeEvent event) {
        if (!config.getBoolean("modules.colorsign.active")) return;

        if(event.getPlayer().hasPermission("simplecore.colorsign")) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
        }
    }
}
