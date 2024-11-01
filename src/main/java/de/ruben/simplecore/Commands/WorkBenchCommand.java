package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorkBenchCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public WorkBenchCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.plugin.getCommand("workbench").setExecutor(this);
        setupDefaultConfig();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("prefix") + getMessage("messages." + getMessage("language") + ".only-players"));
            return true;
        }

        Player player = (Player) sender;
        player.openWorkbench(player.getLocation(), true);
        player.sendMessage(getMessage("prefix") + getMessage("messages." + getMessage("language") + ".workbench-open"));
        return true;
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }

    // Methode zum Überprüfen und Hinzufügen von Standardwerten in der Konfiguration
    private void setupDefaultConfig() {
        if (!config.contains("language")) {
            config.set("language", "en");
        }
        if (!config.contains("messages.en.only-players")) {
            config.set("messages.en.only-players", "Only players can execute this command!");
        }
        if (!config.contains("messages.en.workbench-open")) {
            config.set("messages.en.workbench-open", "&7The &eworkbench&7 has been &asuccessfully&7 opened!");
        }
        if (!config.contains("messages.de.only-players")) {
            config.set("messages.de.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.de.workbench-open")) {
            config.set("messages.de.workbench-open", "&7Die &eWerkbank&7 wurde &aErfolgreich&7 geöffnet!");
        }
        plugin.saveConfig(); // Konfigurationsdatei speichern, falls neue Werte hinzugefügt wurden
    }
}