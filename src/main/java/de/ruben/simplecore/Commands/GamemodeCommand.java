package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand implements Listener, CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public GamemodeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("gamemode").setExecutor(this);
        plugin.getCommand("gamemode").setTabCompleter(this);
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.gamemode.active")) {
            config.set("modules.gamemode.active", true);
        }
        setDefaultMessages();
        plugin.saveConfig();
    }

    private void setDefaultMessages() {
        String[][] messages = {
                {"messages.de.gamemode.change", "&7Du hast deinen Spielmodus auf &e{mode}&7 geändert."},
                {"messages.en.gamemode.change", "&7You changed your game mode to &e{mode}&7."},
                {"messages.de.gamemode.usage", "&cFalsche&7 Verwendung: Benutze &e/gamemode <spieler> <modus>&7."},
                {"messages.en.gamemode.usage", "&cWrong&7 usage: Use &e/gamemode <player> <mode>&7."},
                {"messages.de.gamemode.change-other", "&7Du hast den &eSpielmodus&7 von &e{user} &7auf &e{mode}&7 geändert."},
                {"messages.en.gamemode.no-player", "&cWrong&7 usage: Use &e/gamemode <player> <mode>&7."},
                {"messages.de.gamemode.no-player", "&7Dieser &eSpieler&7 wurde &cnicht&7 gefunden!"},
                {"messages.en.gamemode.change-other", "&7You changed {user}'s &egame mode&7 to &e{mode}&7."},
                {"messages.de.gamemode.already-in-mode", "&7Du bist bereits im Spielmodus &e{mode}&7."},
                {"messages.en.gamemode.already-in-mode", "&7You are already in &e{mode}&7 mode."},
                {"messages.de.gamemode.already-in-mode-other", "&7{user} ist bereits im Spielmodus &e{mode}&7."},
                {"messages.en.gamemode.already-in-mode-other", "&7{user} is already in &e{mode}&7 mode."},
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
            sender.sendMessage(getMessage("messages." + config.getString("language") + ".gamemode.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!config.getBoolean("modules.gamemode.active")) {
            player.sendMessage(getMessage("messages." + config.getString("language") + ".modules.inactive"));
            return true;
        }

        // Check for the number of arguments
        if (args.length == 1) {
            return handleSingleArgCommand(player, args[0]);
        } else if (args.length == 2) {
            return handleTwoArgsCommand(player, args);
        } else {
            sendMessage(player, "usage");
            return true;
        }
    }

    private boolean handleSingleArgCommand(Player player, String mode) {
        switch (mode.toLowerCase()) {
            case "creative":
            case "1":
                if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                    sendMessage(player, "already-in-mode", null, "Creative");
                    return true;
                }
                player.setGameMode(org.bukkit.GameMode.CREATIVE);
                sendMessage(player, "change", null, "Creative");
                break;
            case "survival":
            case "0":
                if (player.getGameMode() == org.bukkit.GameMode.SURVIVAL) {
                    sendMessage(player, "already-in-mode", null, "Survival");
                    return true;
                }
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
                sendMessage(player, "change", null, "Survival");
                break;
            case "spectator":
            case "3":
                if (player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
                    sendMessage(player, "already-in-mode", null, "Spectator");
                    return true;
                }
                player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                sendMessage(player, "change", null, "Spectator");
                break;
            case "adventure":
            case "2":
                if (player.getGameMode() == org.bukkit.GameMode.ADVENTURE) {
                    sendMessage(player, "already-in-mode", null, "Adventure");
                    return true;
                }
                player.setGameMode(org.bukkit.GameMode.ADVENTURE);
                sendMessage(player, "change", null, "Adventure");
                break;
            default:
                sendMessage(player, "usage");
                break;
        }
        return true;
    }

    private boolean handleTwoArgsCommand(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + config.getString("language") + ".gamemode.no-player")));
            return true;
        }

        String mode = args[1].toLowerCase();
        switch (mode) {
                case "creative":
                case "1":
                    if (target.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                        sendMessage(sender, "already-in-mode-other", target.getName(), "Creative");
                        return true;
                    }
                    target.setGameMode(org.bukkit.GameMode.CREATIVE);
                    sendMessage(sender, "change-other", target.getName(), "Creative");
                    break;
                case "survival":
                case "0":
                    if (target.getGameMode() == org.bukkit.GameMode.SURVIVAL) {
                        sendMessage(sender, "already-in-mode-other", target.getName(), "Survival");
                        return true;
                    }
                    target.setGameMode(org.bukkit.GameMode.SURVIVAL);
                    sendMessage(sender, "change-other", target.getName(), "Survival");
                    break;
                case "spectator":
                case "3":
                    if (target.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
                        sendMessage(sender, "already-in-mode-other", target.getName(), "Spectator");
                        return true;
                    }
                    target.setGameMode(org.bukkit.GameMode.SPECTATOR);
                    sendMessage(sender, "change-other", target.getName(), "Spectator");
                    break;
                case "adventure":
                case "2":
                    if (target.getGameMode() == org.bukkit.GameMode.ADVENTURE) {
                        sendMessage(sender, "already-in-mode-other", target.getName(), "Adventure");
                        return true;
                    }
                    target.setGameMode(org.bukkit.GameMode.ADVENTURE);
                    sendMessage(sender, "change-other", target.getName(), "Adventure");
                    break;
                default:
                    sendMessage(sender, "usage");
                    break;
            }
            return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            // Autocomplete für Spielernamen
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(onlinePlayer.getName());
                }
            }
            // Liste der möglichen Spielmodus-Optionen
            String[] options = {"creative", "survival", "spectator", "adventure", "1", "0", "3", "2"};
            for (String option : options) {
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(option);
                }
            }
        } else if (args.length == 2) {
            // Autocomplete für Spielmodi, wenn der Spielername angegeben ist
            String[] options = {"creative", "survival", "spectator", "adventure", "1", "0", "3", "2"};
            for (String option : options) {
                if (option.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(option);
                }
            }
        }
        return completions;
    }

    private void sendMessage(Player player, String messageKey, String... placeholders) {
        String message = getMessage(messageKey);

        if (placeholders.length > 0 && placeholders[0] != null) {
            message = message.replace("{user}", placeholders[0]);
        }

        if (placeholders.length > 1 && placeholders[1] != null) {
            message = message.replace("{mode}", placeholders[1]);
        }

        player.sendMessage(message);
    }

    private String getMessage(String key) {
        String language = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + language + ".gamemode." + key, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7.");
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }
}