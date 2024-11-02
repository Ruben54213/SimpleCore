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

import java.util.ArrayList;
import java.util.List;

public class ItemLoreCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ItemLoreCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.lore.active")) {
            config.set("modules.lore.active", true);
        }
        setDefaultMessages();
        plugin.saveConfig();
    }

    private void setDefaultMessages() {
        String[][] messages = {
                {"messages.de.lore.usage", "&cFalsche&7 Benutzung: &e/lore add &7<&etext&7> oder &e/lore set &7<&ezeile&7> <&etext&7>."},
                {"messages.en.lore.usage", "&cWrong&7 usage: &e/lore add &7<&etext&7> or &e/lore set &7<&eline&7> <&etext&7>."},
                {"messages.de.lore.not-held", "&7Du hältst &ckein&7 Item in der &eHand&7."},
                {"messages.en.lore.not-held", "&7You are &cnot&7 holding an &eitem&7."},
                {"messages.de.lore.added", "&7Lore wurde &aerfolgreich&7 hinzugefügt: &e{text}"},
                {"messages.en.lore.added", "&7Lore was &asuccessfully&7 added: &e{text}"},
                {"messages.de.lore.set", "&7Lore auf Zeile &e{line}&7 gesetzt: &e{text}"},
                {"messages.en.lore.set", "&7Lore set at line &e{line}&7: &e{text}"},
                {"messages.de.lore.only-players", "Diesen Befehl dürfen nur Spieler ausführen!"},
                {"messages.en.lore.only-players", "Only players can execute this command!"},
                {"messages.de.lore.invalid-line", "&cFehler&7 aufgetreten: &cUngültige&7 Zeilennummer."},
                {"messages.en.lore.invalid-line", "&cError&7 occurred: &cInvalid&7 line number."}
        };

        for (String[] message : messages) {
            if (!config.contains(message[0])) {
                config.set(message[0], message[1]);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.only-players")));
            return true;
        }

        if (!config.getBoolean("modules.lore.active")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".modules.inactive")));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.not-held")));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.not-held")));
            return true;
        }

        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        if (args.length < 2 || !(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.usage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            String text = String.join(" ", args).substring(args[0].length() + 1);
            lore.add(ChatColor.translateAlternateColorCodes('&', text));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.added").replace("{text}", text)));

        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.usage")));
                return true;
            }

            try {
                int line = Integer.parseInt(args[1]) - 1;
                if (line < 0 || line >= lore.size()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.invalid-line")));
                    return true;
                }

                String text = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
                lore.set(line, ChatColor.translateAlternateColorCodes('&', text));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.set").replace("{line}", String.valueOf(line + 1)).replace("{text}", text)));

            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("messages." + config.getString("language") + ".lore.invalid-line")));
                return true;
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return true;
    }

    private String getMessage(String key) {
        String language = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString(key, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7.");
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}
