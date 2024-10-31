package de.ruben.simplecore;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Konfigurationsdatei erstellen, falls sie noch nicht existiert
        saveDefaultConfig();
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
