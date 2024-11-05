package de.ruben.simplecore.Commands;

import de.ruben.simplecore.Utility.WarpManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
        if (!config.contains("messages.de.warp.success.teleport")) {
            config.set("messages.de.warp.success.teleport", "&7Du wurdest zu Warp &e{warp}&7 teleportiert.");
        }
        if (!config.contains("messages.de.warp.success.set")) {
            config.set("messages.de.warp.success.set", "&7Warp &e{warp}&7 wurde gesetzt.");
        }
        if (!config.contains("messages.de.warp.success.set.already-exists")) {
            config.set("messages.de.warp.set.already-exists", "&7Warp &e{warp}&7 existiert bereits.");
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
        if (!config.contains("messages.de.warp.list-hover")) {
            config.set("messages.de.warp.list-hover", "&8[&eTeleportieren&8]");
        }
        if (!config.contains("messages.de.warp.list-none")) {
            config.set("messages.de.warp.list-none", "&7Es sind keine Warps gesetzt.");
        }

        if (!config.contains("messages.en.warp.no-permission")) {
            config.set("messages.en.warp.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.warp.success.teleport")) {
            config.set("messages.en.warp.success.teleport", "&7You have been teleported to Warp &e{warp}&7.");
        }
        if (!config.contains("messages.en.warp.success.set")) {
            config.set("messages.en.warp.success.set", "&7Warp &e{warp}&7 has been set.");
        }
        if (!config.contains("messages.en.warp.success.set.already-exists")) {
            config.set("messages.en.warp.set.already-exists", "&7Warp &e{warp}&7 already exists.");
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
        if (!config.contains("messages.en.warp.list-hover")) {
            config.set("messages.en.warp.list-hover", "&8[&eTeleport&8]");
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

                TextComponent message = new TextComponent(getMessage("warp.list").replace("{count}", String.valueOf(warpCount)).replace("{warps}", ""));

                for (String warp : warpNames) {
                    TextComponent warpComponent = new TextComponent(ChatColor.YELLOW + warp);

                    // Füge einen Hover-Text hinzu
                    warpComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(getRawMessage("warp.list-hover")).create()));
                    warpComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp));

                    message.addExtra(warpComponent);
                    message.addExtra("§8, ");
                }

                if (message.getExtra().size() > 0) {
                    message.getExtra().remove(message.getExtra().size() - 1);
                }

                player.spigot().sendMessage(message);
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

                if(warpName.equalsIgnoreCase("set") || warpName.equalsIgnoreCase("remove")) {
                    player.sendMessage(getMessage("warp.set.already-exists").replace("{warp}", warpName));
                }

                if (warpManager.getWarp(warpName) != null) {
                    player.sendMessage(getMessage("warp.set.already-exists").replace("{warp}", warpName));
                    return true;
                }

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
                    player.sendMessage(getMessage("warp.success.teleport").replace("{warp}", warpName));
                } else {
                    player.sendMessage(getMessage("warp.not-found").replace("{warp}", warpName));
                }
                break;
        }
        return true;
    }

    private String getRawMessage(String path) {
        String lang = config.getString("language", "de");
        return ChatColor.translateAlternateColorCodes('&', config.getString("messages." + lang + "." + path,
                "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
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
            for (String warp : warpNames) {
                if (warp.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(warp);
                }
            }
        } else if (args.length == 2) {
            if ("set".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0])) {
                Set<String> warpNames = warpManager.getWarpNames();
                for (String warp : warpNames) {
                    if (warp.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(warp);
                    }
                }
            }
        }

        return completions;
    }
}
