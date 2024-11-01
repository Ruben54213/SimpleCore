package de.ruben.simplecore;

import de.ruben.simplecore.Commands.BroadcastCommand;
import de.ruben.simplecore.Commands.ItemRenameCommand;
import de.ruben.simplecore.Commands.WeatherCommand;
import de.ruben.simplecore.Commands.WorkBenchCommand;
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

        //Listener Integration
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
