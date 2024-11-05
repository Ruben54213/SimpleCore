package de.ruben.simplecore.Utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WarpManager {

    private final Plugin plugin;
    private final File warpFile;
    private final FileConfiguration warpConfig;

    public WarpManager(Plugin plugin) {
        this.plugin = plugin;
        this.warpFile = new File(plugin.getDataFolder(), "warps.yml");
        this.warpConfig = YamlConfiguration.loadConfiguration(warpFile);
    }

    public void setWarp(String name, Location location) {
        warpConfig.set(name + ".world", location.getWorld().getName());
        warpConfig.set(name + ".x", location.getX());
        warpConfig.set(name + ".y", location.getY());
        warpConfig.set(name + ".z", location.getZ());
        warpConfig.set(name + ".pitch", location.getPitch());
        warpConfig.set(name + ".yaw", location.getYaw());

        try {
            warpConfig.save(warpFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getWarp(String name) {
        if (!warpConfig.contains(name + ".world")) {
            return null;
        }

        World world = Bukkit.getWorld(warpConfig.getString(name + ".world"));
        if (world == null) {
            return null;
        }

        double x = warpConfig.getDouble(name + ".x");
        double y = warpConfig.getDouble(name + ".y");
        double z = warpConfig.getDouble(name + ".z");
        float pitch = (float) warpConfig.getDouble(name + ".pitch");
        float yaw = (float) warpConfig.getDouble(name + ".yaw");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean deleteWarp(String name)  {
        if (warpConfig.contains(name)) {
            warpConfig.set(name, null);
            try {
                warpConfig.save(warpFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    public Set<String> getWarpNames() {
        return warpConfig.getKeys(false);
    }
}
