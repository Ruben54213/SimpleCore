package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorkBenchCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public WorkBenchCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("workbench").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("messages." + getMessage("language") + ".only-players"));
            return true;
        }

        Player player = (Player) sender;
        player.openWorkbench(player.getLocation(), true);
        player.sendMessage(getMessage("messages." + getMessage("language") + ".workbench-open"));
        return true;
    }

    private String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path, "&7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}
