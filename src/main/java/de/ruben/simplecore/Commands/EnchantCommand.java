package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public EnchantCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.custom-enchant.active")) {
            config.set("modules.custom-enchant.active", true);
        }
        if (!config.contains("messages.de.custom-enchant.success")) {
            config.set("messages.de.custom-enchant.success", "&7Das &eItem&7 wurde &aerfolgreich&7 mit &e{enchantment}&7 auf Level &e{level} &7verzaubert!");
        }
        if (!config.contains("messages.de.custom-enchant.failure")) {
            config.set("messages.de.custom-enchant.failure", "&cFehler&7 Aufgetreten: &7Du &cmusst&7 ein Item in der &eHand &7halten und eine &agültige&7 Verzauberung und &eLevel&7 angeben!");
        }
        if (!config.contains("messages.en.custom-enchant.success")) {
            config.set("messages.en.custom-enchant.success", "&7The &eitem&7 has been &asuccessfully&7 enchanted with &e{enchantment}&7 at level &e{level}&7!");
        }
        if (!config.contains("messages.en.custom-enchant.failure")) {
            config.set("messages.en.custom-enchant.failure", "&7An &cerror &7occurred: &7You &cmust&7 hold an item in your &ehand &7and specify a &avalid&7 enchantment and &elevel&7!");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("modules.custom-enchant.active")) {
            sender.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".modules.inactive"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".custom-enchant.only-player"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();

        if (item == null || args.length < 2) {
            player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".custom-enchant.failure"));
            return true;
        }

        try {
            Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());
            int level = Integer.parseInt(args[1]);

            if (enchantment == null || level < 1 || level > 999) {
                player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".custom-enchant.failure"));
                return true;
            }

            item.addUnsafeEnchantment(enchantment, level);
            String successMessage = getConfigMessage("messages." + getConfigMessage("language") + ".custom-enchant.success")
                    .replace("{enchantment}", enchantment.getName())
                    .replace("{level}", String.valueOf(level));
            player.sendMessage(getConfigMessage("prefix") + successMessage);

        } catch (NumberFormatException e) {
            player.sendMessage(getConfigMessage("prefix") + getConfigMessage("messages." + getConfigMessage("language") + ".custom-enchant.failure"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        // Check if the player is trying to complete the enchantment name
        if (args.length == 1) {
            for (Enchantment enchantment : Enchantment.values()) {
                completions.add(enchantment.getName().toLowerCase());
            }
        }

        // No completions for level numbers
        return completions;
    }

    private String getConfigMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString(path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}