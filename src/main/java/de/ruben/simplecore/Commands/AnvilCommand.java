package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilCommand implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public AnvilCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("anvil").setExecutor(this);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.anvil.active")) {
            config.set("modules.anvil.active", true);
        }
        setDefaultMessages();
        plugin.saveConfig();
    }

    private void setDefaultMessages() {
        String[][] messages = {
                {"messages.de.anvil.open", "&7Dein &eAmboss&7 wird nun &ageöffnet&7.."},
                {"messages.en.anvil.open", "&7Your &eAnvil&7 is now &aopening&7.."},
                {"messages.de.anvil.title", "Amboss"},
                {"messages.en.anvil.title", "Anvil"},
                {"messages.de.anvil.open-other", "&7Du hast das &eAmboss&7 für &e{name}&7 geöffnet."},
                {"messages.en.anvil.open-other", "&7You opened the &eAnvil&7 for &e{name}&7."},
                {"messages.de.anvil.not-online", "&7Der Spieler &e{name}&7 ist &cnicht&7 online."},
                {"messages.en.anvil.not-online", "&7The player &e{name}&7 is &cnot&7 online."},
                {"messages.de.anvil.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung."},
                {"messages.en.anvil.no-permission", "&7You do not have &cpermission&7 for that."},
                {"messages.de.anvil.usage", "&cFalsche&7 Verwendung: Benutze &e/anvil &7(&eSpieler&7)."},
                {"messages.en.anvil.usage", "&cWrong&7 usage: Use &e/anvil &7(&ePlayer&7)."}
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
            sender.sendMessage(config.getString("messages." + config.getString("language") + ".anvil.only-players"));
            return true;
        }

        Player player = (Player) sender;
        if (!config.getBoolean("modules.anvil.active")) {
            player.sendMessage(getMessage("modules.anvil.disabled"));
            return true;
        }

        if (args.length == 0) {
            openAnvil(player);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                if (player.hasPermission("simplecore.anvil.other")) {
                    openAnvil(target);
                    sendMessage(player, "open-other", target.getName());
                } else {
                    sendMessage(player, "no-permission");
                }
            } else {
                sendMessage(player, "not-online", args[0]);
            }
        } else {
            sendMessage(player, "usage");
        }
        return true;
    }

    private void openAnvil(Player player) {
        String title = config.getString("messages." + config.getString("language") + ".anvil.title");

        // Check for server version and create the appropriate inventory
        Inventory anvil;
        if (player.getServer().getVersion().contains("1.8")) {
            // Create a custom anvil-like inventory for 1.8
            anvil = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', title));

            // Placeholder for the left input
            ItemStack leftInput = new ItemStack(Material.ANVIL);
            ItemMeta leftInputMeta = leftInput.getItemMeta();
            if (leftInputMeta != null) {
                leftInputMeta.setDisplayName(ChatColor.GOLD + "Input Item");
                leftInput.setItemMeta(leftInputMeta);
            }
            anvil.setItem(0, leftInput); // Left input slot

            // Placeholder for the right input
            ItemStack rightInput = new ItemStack(Material.ANVIL);
            ItemMeta rightInputMeta = rightInput.getItemMeta();
            if (rightInputMeta != null) {
                rightInputMeta.setDisplayName(ChatColor.GOLD + "Rename Item");
                rightInput.setItemMeta(rightInputMeta);
            }
            anvil.setItem(1, rightInput); // Right input slot

            // Placeholder for the output
            ItemStack output = new ItemStack(Material.ANVIL);
            ItemMeta outputMeta = output.getItemMeta();
            if (outputMeta != null) {
                outputMeta.setDisplayName(ChatColor.GOLD + "Output Item");
                output.setItemMeta(outputMeta);
            }
            anvil.setItem(2, output); // Output slot
        } else {
            // For 1.9+ versions, use the built-in anvil inventory
            anvil = Bukkit.createInventory(null, InventoryType.ANVIL, ChatColor.translateAlternateColorCodes('&', title));
        }

        // Open the appropriate anvil inventory for the player
        player.openInventory(anvil);
        sendMessage(player, "open");
    }

    private void sendMessage(Player player, String messageKey, String... placeholders) {
        String message = getMessage(messageKey);
        if (placeholders.length > 0) {
            message = message.replace("{name}", placeholders[0]);
        }
        player.sendMessage(message);
    }

    private String getMessage(String key) {
        String language = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + language + ".anvil." + key, "&bSimple&fCore &8» &7Ein &cFehler&7 ist aufgetreten.");

        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}