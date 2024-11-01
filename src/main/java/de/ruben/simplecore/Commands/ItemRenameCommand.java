package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRenameCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ItemRenameCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        // Standardwerte hinzufügen, falls sie fehlen
        if (!config.contains("modules.itemrename.active")) {
            config.set("modules.itemrename.active", true);
        }
        if (!config.contains("messages." + config.getString("language") + ".itemrename.only-players")) {
            config.set("messages." + config.getString("language") + ".itemrename.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages." + config.getString("language") + ".itemrename.no-item")) {
            config.set("messages." + config.getString("language") + ".itemrename.no-item", "&7Du hast &ckein&7 Item in der &eHand&7.");
        }
        if (!config.contains("messages." + config.getString("language") + ".itemrename.success")) {
            config.set("messages." + config.getString("language") + ".itemrename.success", "&7Das &eItem&7 wurde &aErfolgreich&7 umbenannt!");
        }
        if (!config.contains("messages." + config.getString("language") + ".itemrename.usage")) {
            config.set("messages." + config.getString("language") + ".itemrename.usage", "&7Du &cmusst&7 einen Namen angeben!");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigMessage("messages." + config.getString("language") + ".itemrename.only-players"));
            return true;
        }

        // Überprüfen, ob das Modul aktiviert ist
        if (!config.getBoolean("modules.itemrename.active", true)) {
            String message2 = getConfigMessage("messages." + config.getString("language") + ".modules.inactive");
            sender.sendMessage(getConfigMessage("prefix") + message2);
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            String message1 = getConfigMessage("messages." + config.getString("language") + ".itemrename.usage");
            player.sendMessage(getConfigMessage("prefix") + message1);
            return false;
        }

        String newName = String.join(" ", args);
        newName = ChatColor.translateAlternateColorCodes('&', newName); // Farbcodes verarbeiten

        ItemStack itemInHand = player.getInventory().getItemInHand(); // Für ältere Bukkit-Versionen
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + config.getString("language") + ".itemrename.no-item"));
            return true;
        }

        // Item umbenennen
        ItemMeta meta = itemInHand.getItemMeta();
        meta.setDisplayName(newName);
        itemInHand.setItemMeta(meta);
        String message3 = getConfigMessage("messages." + config.getString("language") + ".itemrename.success");
        player.sendMessage(getConfigMessage("prefix") + message3);
        return true;
    }

    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}