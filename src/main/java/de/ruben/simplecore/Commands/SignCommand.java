package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public SignCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.sign.active")) {
            config.set("modules.sign.active", true);
        }
        if (!config.contains("messages.de.sign.no-permission")) {
            config.set("messages.de.sign.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.sign.no-item")) {
            config.set("messages.de.sign.no-item", "&7Du hast &ckein&7 Item in der Hand.");
        }
        if (!config.contains("messages.de.sign.success")) {
            config.set("messages.de.sign.success", "&7Das Item wurde erfolgreich signiert.");
        }
        if (!config.contains("messages.de.sign.only-players")) {
            config.set("messages.de.sign.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.de.sign.lore.sign")) {
            config.set("messages.de.sign.lore.sign", "&7Das Item wurde von &e{player}&7 am &e{date}&7 signiert.");
        }
        if (!config.contains("messages.en.sign.lore.sign")) {
            config.set("messages.en.sign.lore.sign", "&7The item was signed by &e{player}&7 on &e{date}&7.");
        }
        if (!config.contains("messages.en.sign.no-permission")) {
            config.set("messages.en.sign.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.sign.no-item")) {
            config.set("messages.en.sign.no-item", "&7You have &cno&7 item in your hand.");
        }
        if (!config.contains("messages.en.sign.success")) {
            config.set("messages.en.sign.success", "&7The item has been successfully signed.");
        }
        if (!config.contains("messages.en.sign.only-players")) {
            config.set("messages.en.sign.only-players", "Only players can execute this command!");
        }
        if (!config.contains("sign.lore-template")) {
            config.set("sign.lore-template", "&7Signed by &e{player}&7 on &e{date}&7");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("sign.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.sign.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("sign.inactive")));
            return true;
        }

        // Überprüfen, ob der Spieler Berechtigung für den Befehl hat
        if (!player.hasPermission("simplecore.sign")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("sign.no-permission")));
            return true;
        }

        // Überprüfen, ob der Spieler ein Item in der Hand hat
        ItemStack itemInHand = player.getInventory().getItemInHand();
        if (itemInHand == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("sign.no-item")));
            return true;
        }

        // Setze die Lore auf das Item
        ItemMeta meta = itemInHand.getItemMeta();
        List<String> lore = new ArrayList<>();

        // Datum auslesen und formatieren
        String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        // Grundlegende Signatur aus der Konfiguration
        String template = config.getString("messages." + config.getString("language", "de") + ".sign.lore.sign");
        String signText = template.replace("{player}", player.getName()).replace("{date}", date);
        lore.add(ChatColor.translateAlternateColorCodes('&', signText));

        // Optional: Nachricht des Spielers hinzufügen
        if (args.length > 0) {
            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', message.toString().trim()));
        }

        // Setze die neue Lore und speichere das Item
        meta.setLore(lore);
        itemInHand.setItemMeta(meta);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("sign.success")));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");  // Setze die Standardsprache auf Deutsch
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}
