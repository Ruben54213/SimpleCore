package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public class BreakCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public BreakCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.removeblock.active")) {
            config.set("modules.removeblock.active", true);
        }
        if (!config.contains("messages.de.removeblock.no-permission")) {
            config.set("messages.de.removeblock.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.removeblock.no-target")) {
            config.set("messages.de.removeblock.no-target", "&7Du schaust auf &ckeinen&7 &eBlock&7.");
        }
        if (!config.contains("messages.de.removeblock.success")) {
            config.set("messages.de.removeblock.success", "&7Der Block wurde &aerfolgreich&7 entfernt.");
        }
        if (!config.contains("messages.en.removeblock.no-permission")) {
            config.set("messages.en.removeblock.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.removeblock.no-target")) {
            config.set("messages.en.removeblock.no-target", "&7You are &cnot&7 looking at any &eblock&7.");
        }
        if (!config.contains("messages.en.removeblock.success")) {
            config.set("messages.en.removeblock.success", "&7The block has been &asuccessfully&7 removed.");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("removeblock.no-target")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.removeblock.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("removeblock.inactive")));
            return true;
        }

        // Überprüfen, ob der Spieler Berechtigung für den Befehl hat
        if (!player.hasPermission("simplecore.removeblock")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("removeblock.no-permission")));
            return true;
        }

        // Bestimme den Block, den der Spieler anschaut
        Block targetBlock = getTargetBlock(player, 5);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("removeblock.no-target")));
            return true;
        }

        // Entferne den Block
        targetBlock.setType(Material.AIR);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("removeblock.success")));
        return true;
    }

    private Block getTargetBlock(Player player, int maxDistance) {
        BlockIterator iterator = new BlockIterator(player, maxDistance);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (block.getType() != Material.AIR) {
                return block;
            }
        }
        return null;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");  // Standardsprache auf Deutsch
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Ein &cFehler&7 ist aufgetreten."));
    }
}
