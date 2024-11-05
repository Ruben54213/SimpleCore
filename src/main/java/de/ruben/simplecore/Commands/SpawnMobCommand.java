package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnMobCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public SpawnMobCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
        plugin.getCommand("spawnmob").setAliases(Arrays.asList("summon"));
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.spawnmob.active")) {
            config.set("modules.spawnmob.active", true);
        }
        if (!config.contains("messages.de.spawnmob.no-permission")) {
            config.set("messages.de.spawnmob.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.spawnmob.success")) {
            config.set("messages.de.spawnmob.success", "&7Du hast erfolgreich &e{amount} {mob}&7 gespawnt.");
        }
        if (!config.contains("messages.de.spawnmob.invalid-mob")) {
            config.set("messages.de.spawnmob.invalid-mob", "&7Der Mob &e{mob}&7 ist ungültig.");
        }
        if (!config.contains("messages.de.spawnmob.only-players")) {
            config.set("messages.de.spawnmob.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.de.spawnmob.usage")) {
            config.set("messages.de.spawnmob.usage", "&cFalsche &7Verwendung: &e/spawnmob <mob> <anzahl>");
        }
        if (!config.contains("messages.de.modules.inactive")) {
            config.set("messages.de.modules.inactive", "&7Das Modul ist derzeit &cdeaktiviert&7.");
        }

        if (!config.contains("messages.en.spawnmob.no-permission")) {
            config.set("messages.en.spawnmob.no-permission", "&7You do not have &cpermission&7 to do that.");
        }
        if (!config.contains("messages.en.spawnmob.success")) {
            config.set("messages.en.spawnmob.success", "&7Successfully spawned &e{amount} {mob}&7.");
        }
        if (!config.contains("messages.en.spawnmob.invalid-mob")) {
            config.set("messages.en.spawnmob.invalid-mob", "&7The mob &e{mob}&7 is invalid.");
        }
        if (!config.contains("messages.en.spawnmob.only-players")) {
            config.set("messages.en.spawnmob.only-players", "Only players can execute this command!");
        }
        if (!config.contains("messages.en.spawnmob.usage")) {
            config.set("messages.en.spawnmob.usage", "&cWrong &7usage: &e/spawnmob <mob> <amount>");
        }
        if (!config.contains("messages.en.modules.inactive")) {
            config.set("messages.en.modules.inactive", "&7The module is currently &cinactive&7.");
        }

        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("modules.spawnmob.active", true)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + getMessage("modules.inactive")));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("spawnmob.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("simplecore.spawnmob")) {
            player.sendMessage(getMessage("spawnmob.no-permission"));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(getMessage("spawnmob.usage"));
            return true;
        }

        String mobName = args[0].toUpperCase();
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(getMessage("spawnmob.usage"));
            return true;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(mobName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(getMessage("spawnmob.invalid-mob").replace("{mob}", mobName.toLowerCase()));
            return true;
        }

        for (int i = 0; i < amount; i++) {
            player.getWorld().spawnEntity(player.getLocation(), entityType);
        }

        player.sendMessage(getMessage("spawnmob.success")
                .replace("{amount}", String.valueOf(amount))
                .replace("{mob}", mobName.toLowerCase()));
        return true;
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "de");
        String prefix = config.getString("prefix", "&bSimple&fCore &8» ");
        String message = config.getString("messages." + lang + "." + path,
                "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: "+ path);
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (EntityType entityType : EntityType.values()) {
                if (entityType.isAlive()) {
                    completions.add(entityType.name().toLowerCase());
                }
            }
            String currentInput = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(currentInput));

        }
        return completions;
    }
}
