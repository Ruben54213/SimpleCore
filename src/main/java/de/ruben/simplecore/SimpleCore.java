package de.ruben.simplecore;

import de.ruben.simplecore.Commands.*;
import de.ruben.simplecore.Listeners.JoinLeaveMessages;
import de.ruben.simplecore.Listeners.SignChangeListener;
import de.ruben.simplecore.Utility.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCore extends JavaPlugin {

    @Override
    public void onEnable() {
        WarpManager warpManager = new WarpManager(this);
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
        getCommand("invclear").setExecutor(new InvClearCommand(this));
        getCommand("day").setExecutor(new TimeCommand(this));
        getCommand("night").setExecutor(new TimeCommand(this));
        getCommand("midnight").setExecutor(new TimeCommand(this));
        getCommand("noon").setExecutor(new TimeCommand(this));
        getCommand("time").setExecutor(new TimeCommand(this));
            getCommand("time").setTabCompleter(new TimeCommand(this));
        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        getCommand("repair").setExecutor(new RepairCommand(this));
            getCommand("repair").setTabCompleter(new RepairCommand(this));
        getCommand("more").setExecutor(new MoreCommand(this));
        getCommand("stack").setExecutor(new StackCommand(this));
        getCommand("sign").setExecutor(new SignCommand(this));
        getCommand("removeblock").setExecutor(new BreakCommand(this));
        getCommand("lightning").setExecutor(new LightningCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("godmode").setExecutor(new GodModeCommand(this));
        getCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
            getCommand("spawnmob").setTabCompleter(new SpawnMobCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this, warpManager));
            getCommand("warp").setTabCompleter(new WarpCommand(this, warpManager));

        //Listener Integration
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinLeaveMessages(this), this);
        pluginManager.registerEvents(new InvseeCommand(this), this);
        pluginManager.registerEvents(new SignChangeListener(this), this);
        pluginManager.registerEvents(new GodModeCommand(this), this);
        //Anvil Command
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /*
    public void reloadPluginConfig() {
        reloadConfig();
        getLogger().info("Config.yml wurde neu geladen.");
    }
    */


    /* Beispielmethode, um einen Wert aus der Config zu holen
     public String getExampleSetting() {
        return getConfig().getString("example-setting");
     } */

}
