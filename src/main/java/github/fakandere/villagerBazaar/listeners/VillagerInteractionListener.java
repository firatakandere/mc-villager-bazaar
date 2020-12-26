package github.fakandere.villagerBazaar.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class VillagerInteractionListener implements Listener{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteractEntity(final PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if(e.getRightClicked() instanceof Villager) {
            Villager v = (Villager) e.getRightClicked();
            //if(v.getCustomName().equalsIgnoreCase("YourCustomName")) {
            //Do something
            //For example open an Inventory
            Inventory inv = Bukkit.createInventory(null, 27, "Custom Inventory Title");

            ItemStack item = new ItemStack(Material.APPLE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Custom ItemName");
            item.setItemMeta(meta);

            p.openInventory(inv);
            // }
        }
    }



}
