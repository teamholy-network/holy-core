package de.teamholy.core.bukkit.utils;

import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/* copyright by Yassino */
@Getter
public class Inventory implements Listener {

    private org.bukkit.inventory.Inventory inventory;
    private Map<Integer, Consumer<InventoryClickEvent>> items;
    private Consumer<InventoryCloseEvent> onClose;
    private boolean cancelClick = true;

    public Inventory(String title, int size) {
        inventory = Bukkit.createInventory(null, size, title);
        BukkitCore.getInstance().getServer().getPluginManager().registerEvents(this, BukkitCore.getInstance());
        items = new HashMap<>();
    }

    public Inventory(String title, int size, boolean cancelClick) {
        inventory = Bukkit.createInventory(null, size, title);
        BukkitCore.getInstance().getServer().getPluginManager().registerEvents(this, BukkitCore.getInstance());
        items = new HashMap<>();
        this.cancelClick = cancelClick;
    }

    public void setOnClose(Consumer<InventoryCloseEvent> onClose) {
        this.onClose = onClose;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            if (onClose != null) {
                onClose.accept(event);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inventory) && event.getRawSlot() < event.getInventory().getSize()) {
            event.setCancelled(cancelClick);
            for (Integer integer : items.keySet()) {
                if (integer == event.getSlot()) {
                    items.get(integer).accept(event);
                }
            }
        }
    }

    public void setItem(ItemStack itemStack, int slot, Consumer<InventoryClickEvent> onClick) {
        inventory.setItem(slot, itemStack);
        items.put(slot, onClick);
    }

    public void setItem(ItemStack itemStack, int slot) {
        inventory.setItem(slot, itemStack);
    }


}
