package de.ruben.simplecore;

import de.ruben.simplecore.Commands.*;
import de.ruben.simplecore.Listeners.JoinLeaveMessages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Konfigurationsdatei erstellen, falls sie noch nicht existiert
        saveDefaultConfig();
        //Command Integration
        getCommand("weather").setExecutor(new WeatherCommand(this));
        getCommand("workbench").setExecutor(new WorkBenchCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("rename").setExecutor(new ItemRenameCommand(this));
        getCommand("msg").setExecutor(new MessageCommand(this));
        getCommand("msg").setTabCompleter(new MessageCommand(this));
        getCommand("r").setExecutor(new MessageCommand(this));
        getCommand("enchant").setExecutor(new EnchantCommand(this));
        getCommand("enchant").setTabCompleter(new EnchantCommand(this));
        getCommand("gamemode").setExecutor(new GamemodeCommand(this));
        getCommand("gamemode").setTabCompleter(new GamemodeCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("chatclear").setExecutor(new ChatClearCommand(this));
        getCommand("lore").setExecutor(new ItemLoreCommand(this));
            getCommand("teleport").setExecutor(new TeleportCommand(this));
            getCommand("teleport").setTabCompleter(new TeleportCommand(this));
            getCommand("tpo").setExecutor(new TeleportCommand(this));
        getCommand("kill").setExecutor(new KillCommand(this));
        getCommand("kill").setTabCompleter(new KillCommand(this));
        getCommand("nearby").setExecutor(new NearbyCommand(this));

        //Listener Integration
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinLeaveMessages(this), this);
        pluginManager.registerEvents(new ChatClearCommand(this), this);
        //Anvil Command
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // Methode zum Neuladen der Konfiguration
    //public void reloadPluginConfig() {
    //    reloadConfig();
    //    getLogger().info("Config.yml wurde neu geladen.");
    //}

    // Beispielmethode, um einen Wert aus der Config zu holen
    // public String getExampleSetting() {
    //    return getConfig().getString("example-setting");
    // }

}
