package de.ruben.simplecore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//coded by mats

public class TrashCommand implements CommandExecutor, Listener {

    // Map um zu tracken welche Inventare Trash-Inventare sind
    private final Map<UUID, Inventory> trashInventories = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Bukkit.getServer().getIp().equals("77.90.52.24"))
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("trash")) {
            openTrashInventory(player);
            return true;
        }

        return false;
    }

    /**
     * Öffnet das Trash-Inventar für den Spieler
     */
    private void openTrashInventory(Player player) {
        // 6x9 = 54 Slots Inventar erstellen
        Inventory trashInv = Bukkit.createInventory(null, 54, "§c§lMülleimer §7- §fItems hier löschen");

        // Deko-Items an den Rändern hinzufügen
        addDecorationItems(trashInv);

        // Inventar in der Map speichern
        trashInventories.put(player.getUniqueId(), trashInv);

        // Inventar öffnen
        player.openInventory(trashInv);
        player.sendMessage("§7Lege Items in den Mülleimer. Beim Schließen werden sie §c§lgelöscht§7!");
    }

    /**
     * Fügt Deko-Items an den Rändern des Inventars hinzu
     */
    private void addDecorationItems(Inventory inventory) {
        ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14); // Rotes Glas
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName("§c§lMülleimer");
        meta.setLore(Arrays.asList(
                "§7Lege Items in die mittleren Slots",
                "§7um sie zu löschen!"
        ));
        glassPane.setItemMeta(meta);

        // Obere Reihe
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, glassPane);
        }

        // Untere Reihe
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, glassPane);
        }

        // Linke und rechte Seiten
        for (int i = 1; i < 5; i++) {
            inventory.setItem(i * 9, glassPane);      // Linke Seite
            inventory.setItem(i * 9 + 8, glassPane); // Rechte Seite
        }

        // Spezielle Items hinzufügen
        ItemStack trashIcon = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta trashMeta = trashIcon.getItemMeta();
        trashMeta.setDisplayName("§c§lLöschen");
        trashMeta.setLore(Arrays.asList(
                "§7Alle Items in diesem Inventar",
                "§7werden beim Schließen gelöscht!"
        ));
        trashIcon.setItemMeta(trashMeta);
        inventory.setItem(4, trashIcon); // Mitte oben

        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§e§lInfo");
        infoMeta.setLore(Arrays.asList(
                "§7Lege Items in die freien Slots",
                "§7um sie zu löschen.",
                "§c§lAchtung: §7Dies kann nicht",
                "§7rückgängig gemacht werden!"
        ));
        infoItem.setItemMeta(infoMeta);
        inventory.setItem(49, infoItem); // Mitte unten
    }

    /**
     * Event Handler für das Schließen des Inventars
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();

        // Prüfen ob es sich um ein Trash-Inventar handelt
        if (trashInventories.containsKey(player.getUniqueId()) &&
                trashInventories.get(player.getUniqueId()).equals(closedInventory)) {

            // Items zählen die gelöscht werden
            int deletedItems = 0;

            // Durch alle Slots iterieren (außer Deko-Items)
            for (int slot = 0; slot < closedInventory.getSize(); slot++) {
                if (isTrashSlot(slot)) {
                    ItemStack item = closedInventory.getItem(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        deletedItems += item.getAmount();
                    }
                }
            }

            // Inventar aus der Map entfernen
            trashInventories.remove(player.getUniqueId());

            // Feedback an den Spieler
            if (deletedItems > 0) {
                player.sendMessage("§c§l" + deletedItems + " §cItems wurden gelöscht!");
                player.sendMessage("§7Der Mülleimer wurde geleert.");
            } else {
                player.sendMessage("§7Mülleimer war leer - nichts wurde gelöscht.");
            }
        }
    }

    /**
     * Prüft ob ein Slot ein gültiger Trash-Slot ist (keine Deko-Items)
     */
    private boolean isTrashSlot(int slot) {
        // Obere und untere Reihe sind Deko
        if (slot < 9 || slot >= 45) {
            return false;
        }

        // Linke und rechte Spalte sind Deko
        int column = slot % 9;
        if (column == 0 || column == 8) {
            return false;
        }

        return true;
    }

    /**
     * Cleanup Methode für Plugin-Deaktivierung
     */
    public void cleanup() {
        // Alle offenen Trash-Inventare schließen
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (trashInventories.containsKey(player.getUniqueId())) {
                player.closeInventory();
            }
        }
        trashInventories.clear();
    }
}