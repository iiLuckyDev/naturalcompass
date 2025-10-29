package fr.constantdevs.naturalcompass.gui;

import fr.constantdevs.naturalcompass.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class PaginatedGUI {

    protected Inventory inventory;
    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int totalPages;

    public void open(org.bukkit.entity.Player player) {
        player.openInventory(inventory);
    }

    protected void addNavigationButtons() {
        inventory.setItem(45, null);
        inventory.setItem(53, null);
        if (page > 0) {
            inventory.setItem(45, ItemManager.PREVIOUS_PAGE);
        }
        if (page < totalPages - 1) {
            inventory.setItem(53, ItemManager.NEXT_PAGE);
        }
    }

    protected void addBorder() {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(i + 45, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        for (int i = 1; i < 5; i++) {
            inventory.setItem(i * 9, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            inventory.setItem(i * 9 + 8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
    }

    protected void addItem(ItemStack item) {
        for (int i = 10; i < 44; i++) {
            if (i % 9 != 0 && (i + 1) % 9 != 0) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, item);
                    break;
                }
            }
        }
    }

    public abstract void displayPage(int page);

    public Inventory getInventory() {
        return inventory;
    }
}