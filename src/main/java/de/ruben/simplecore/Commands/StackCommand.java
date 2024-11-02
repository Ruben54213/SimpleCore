package de.ruben.simplecore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class StackCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public StackCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        if (!config.contains("modules.stack.active")) {
            config.set("modules.stack.active", true);
        }
        if (!config.contains("messages.de.stack.no-permission")) {
            config.set("messages.de.stack.no-permission", "&7Dafür hast du &ckeine&7 Berechtigung.");
        }
        if (!config.contains("messages.de.stack.success")) {
            config.set("messages.de.stack.success", "&7Alle gleichen Items wurden &egestapelt&7.");
        }
        if (!config.contains("messages.de.stack.only-players")) {
            config.set("messages.de.stack.only-players", "Diesen Befehl dürfen nur Spieler ausführen!");
        }
        if (!config.contains("messages.en.stack.no-permission")) {
            config.set("messages.en.stack.no-permission", "&7You do &cnot&7 have permission to do &ethat&7.");
        }
        if (!config.contains("messages.en.stack.success")) {
            config.set("messages.en.stack.success", "&7All identical items have been &estacked&7.");
        }
        if (!config.contains("messages.en.stack.only-players")) {
            config.set("messages.en.stack.only-players", "Only players can execute this command!");
        }
        plugin.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("stack.only-players")));
            return true;
        }

        Player player = (Player) sender;

        // Überprüfen, ob das Modul aktiv ist
        if (!config.getBoolean("modules.stack.active")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("stack.inactive")));
            return true;
        }

        // Überprüfen, ob der Spieler Berechtigung für den Befehl hat
        if (!player.hasPermission("simplecore.stack")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("stack.no-permission")));
            return true;
        }

        stackItems(player);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage("stack.success")));
        return true;
    }

    private void stackItems(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            // Überprüfen, ob das Item nicht null ist
            if (item != null) {
                // Nur weiter machen, wenn das Item nicht die maximale Anzahl hat
                while (item.getAmount() < item.getMaxStackSize()) {
                    for (int j = i + 1; j < contents.length; j++) {
                        ItemStack otherItem = contents[j];

                        // Überprüfen, ob das andere Item nicht null ist und ob es gleich ist
                        if (otherItem != null && item.isSimilar(otherItem)) {
                            // Berechnung der Gesamtmenge
                            int totalAmount = item.getAmount() + otherItem.getAmount();
                            if (totalAmount <= item.getMaxStackSize()) {
                                // Setze die neue Menge und setze das andere Item auf null
                                item.setAmount(totalAmount);
                                contents[j] = null; // Entferne das Item, das gestapelt wurde
                            } else {
                                // Setze die maximale Menge des Items
                                item.setAmount(item.getMaxStackSize());
                                // Setze die verbleibende Menge des anderen Items
                                otherItem.setAmount(totalAmount - item.getMaxStackSize());
                            }
                            // Breche die innere Schleife ab, wenn wir erfolgreich gestapelt haben
                            break;
                        }
                    }
                    // Wenn kein passendes Item gefunden wurde, breche die Schleife ab
                    if (item.getAmount() >= item.getMaxStackSize()) {
                        break;
                    }
                }
            }
        }

        // Setze die geänderten Inhalte wieder ins Inventar
        player.getInventory().setContents(contents);
    }

    private String getMessage(String path) {
        String lang = config.getString("language", "en");
        return ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + config.getString("messages." + lang + "." + path, "&bSimple&fCore &8» &7Es ist ein &cFehler&7 aufgetreten, bitte melde dich im &eSupport&7. &cGesuchter Path&7: " + path));
    }
}