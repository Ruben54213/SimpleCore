package de.ruben.simplecore.Commands;

import de.ruben.simplecore.Utility.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final WarpManager warpManager;

    public WarpCommand(JavaPlugin plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.warpManager = warpManager;
        setupDefaultConfig();
        plugin.getCommand("warp").setAliases(Arrays.asList("warps"));
        plugin.getCommand("warp").setTabCompleter(this);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.warpp.active")) {
            config.set("modules.warpp.active", true);
        }
        if (!config.contains("messages.de.warp.no-permission")) {
            config.set("messages.de.warp.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.warp.success.set")) {
            config.set("messages.de.warp.success.set", "&7Warp &e{warp}&7 wurde gesetzt.");
        }
        if (!config.contains("messages.de.warp.success.remove")) {
            config.set("messages.de.warp.success.remove", "&7Warp &e{warp}&7 wurde entfernt.");
        }
        if (!config.contains("messages.de.warp.not-found")) {
            config.set("messages.de.warp.not-found", "&7Warp &e{warp}&7 wurde nicht gefunden.");
        }
        if (!config.contains("messages.de.warp.usage")) {
            config.set("messages.de.warp.usage", "&cFalsche &7Verwendung: &e/warp <name>");
        }
        if (!config.contains("messages.de.warp.list")) {
            config.set("messages.de.warp.list", "&7Warps &8(&e{count}&8) &8» &e{warps}");
        }
        if (!config.contains("messages.de.warp.list-none")) {
            config.set("messages.de.warp.list-none", "&7Es sind keine Warps gesetzt.");
        }

        if (!config.contains("messages.en.warp.no-permission")) {
            config.set("messages.en.warp.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.warp.success.set")) {
            config.set("messages.en.warp.success.set", "&7Warp &e{warp}&7 has been set.");
        }
        if (!config.contains("messages.en.warp.success.remove")) {
            config.set("messages.en.warp.success.remove", "&7Warp &e{warp}&7 has been removed.");
        }
        if (!config.contains("messages.en.warp.not-found")) {
            config.set("messages.en.warp.not-found", "&7Warp &e{warp}&7 not found.");
        }
        if (!config.contains("messages.en.warp.usage")) {
            config.set("messages.en.warp.usage", "&cWrong &7usage: &e/warp <name>");
        }
        if (!config.contains("messages.en.warp.list")) {
            config.set("messages.en.warp.list", "&7Warps &8(&e{count}&8) &8» &e{warps}");
        }
        if (!config.contains("messages.en.warp.list-none")) {
            config.set("messages.en.warp.list-none", "&7No warps are set.");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("modules.warp.active", true)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("modules.inactive")));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("warp.only-players"));
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("warps")) {
            Set<String> warpNames = warpManager.getWarpNames();
            if (warpNames.isEmpty()) {
                player.sendMessage(getMessage("warp.list-none"));
            } else {
                int warpCount = warpNames.size();
                StringBuilder warpList = new StringBuilder();

                for (String warp : warpNames) {
                    warpList.append(ChatColor.YELLOW).append(warp).append(", ");
                }

                if (warpList.length() > 0) {
                    warpList.setLength(warpList.length() - 2);
                }
                String message = getMessage("warp.list")
                        .replace("{count}", String.valueOf(warpCount))
                        .replace("{warps}", warpList.toString());
                player.sendMessage(message);
            }
            return true;
        }


        if (args.length == 0) {
            player.sendMessage(getMessage("warp.usage"));
            return true;
        }

        String action = args[0];
        String warpName;

        switch (action.toLowerCase()) {
            case "set":
                if (!player.hasPermission("simplecore.warp.edit")) {
                    player.sendMessage(getMessage("warp.no-permission"));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(getMessage("warp.usage"));
                    return true;
                }

                warpName = args[1];
                warpManager.setWarp(warpName, player.getLocation());
                player.sendMessage(getMessage("warp.success.set").replace("{warp}", warpName));
                break;

            case "remove":
                if (!player.hasPermission("simplecore.warp.edit")) {
                    player.sendMessage(getMessage("warp.no-permission"));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(getMessage("warp.usage"));
                    return true;
                }

                warpName = args[1];
                if (warpManager.deleteWarp(warpName)) {
                    player.sendMessage(getMessage("warp.success.remove").replace("{warp}", warpName));
                } else {
                    player.sendMessage(getMessage("warp.not-found").replace("{warp}", warpName));
                }
                break;

            default:
                warpName = args[0];
                Location warpLocation = warpManager.getWarp(warpName);
                if (warpLocation != null) {
                    player.teleport(warpLocation);
                    player.sendMessage(getMessage("warp.success.set").replace("{warp}", warpName));
                } else {
                    player.sendMessage(getMessage("warp.not-found").replace("{warp}", warpName));
                }
                break;
        }
        return true;
    }
    private String getMessage(String path) {
        String lang = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + lang + "." + path,
                "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("simplecore.warp.edit")) {
                completions.add("set");
                completions.add("remove");
            }
            Set<String> warpNames = warpManager.getWarpNames();
            completions.addAll(warpNames);
        }
        return completions;
    }
}
