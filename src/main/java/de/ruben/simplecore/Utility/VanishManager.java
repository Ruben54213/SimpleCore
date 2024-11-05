package de.ruben.simplecore.Utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager implements Listener {

    private static VanishManager manager = new VanishManager();
    private Set<UUID> vanished = new HashSet<>();

    public void setVanished(Player player, boolean vanishedStatus) {
        UUID playerUUID = player.getUniqueId();
        if (vanishedStatus) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission("simplecore.vanish")) {
                    onlinePlayer.hidePlayer(player);
                }
            }
            vanished.add(playerUUID);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(player);
            }
            vanished.remove(playerUUID);
        }
    }

    public int vanishedPlayers() {
        return this.vanished.size();
    }

    public Set<UUID> getVanished() {
        return this.vanished;
    }

    public boolean isVanished(Player p) {
        return this.vanished.contains(p.getUniqueId()); // Check using UUID
    }

    public static VanishManager getMainManager() {
        return manager;
    }
}
