package de.ruben.simplecore.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilCommand implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public AnvilCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setupDefaultConfig() {
        // Default values for configuration
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
                {"messages.de.anvil.title", "&fAmboss"},
                {"messages.en.anvil.title", "&fAnvil"},
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (config.getBoolean("modules.anvil.active") &&
                (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR ||
                        event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) &&
                event.getPlayer().hasPermission("anvil.open")) {

            openAnvil(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        if (!args[0].equalsIgnoreCase("/anvil")) return; // Early exit if not our command

        Player sender = (Player) event.getPlayer();
        if (args.length == 1) {
            openAnvil(sender);
        } else if (args.length == 2) {
            handleOpenOtherAnvil(sender, args[1]);
        } else {
            sendUsageMessage(sender);
        }
        event.setCancelled(true); // Prevent normal command execution
    }

    private void handleOpenOtherAnvil(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            sendMessage(sender, "not-online", targetName);
            return;
        }
        if (!sender.hasPermission("simplecore.anvil.other")) {
            sendMessage(sender, "no-permission");
            return;
        }
        openAnvil(target);
        sendMessage(sender, "open-other", target.getName());
    }

    private void sendUsageMessage(Player player) {
        sendMessage(player, "usage");
    }

    private void sendMessage(Player player, String messageKey, String... placeholders) {
        String message = config.getString("messages." + config.getString("language") + ".anvil." + messageKey);
        for (String placeholder : placeholders) {
            message = message.replace("{name}", placeholder);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + message));
    }

    private void openAnvil(Player player) {
        Inventory anvil = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".anvil.title")));
        player.openInventory(anvil);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".anvil.open")));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}