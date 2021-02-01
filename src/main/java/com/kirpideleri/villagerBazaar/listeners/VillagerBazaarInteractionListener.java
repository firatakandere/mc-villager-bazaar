package com.kirpideleri.villagerBazaar.listeners;

import com.google.inject.Inject;
import com.kirpideleri.villagerBazaar.VillagerBazaar;
import com.kirpideleri.villagerBazaar.VillagerBazaarPlugin;
import com.kirpideleri.villagerBazaar.models.Bazaar;
import com.kirpideleri.villagerBazaar.utils.IBazaarManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerBazaarInteractionListener implements Listener{

    @Inject
    IBazaarManager bazaarManager;

    @Inject
    VillagerBazaarPlugin plugin;

    @Inject
    Permission perm;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteractEntity(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            Player player = e.getPlayer();
            Villager villager = (Villager) e.getRightClicked();

            Bazaar bazaar = bazaarManager.getBazaar(villager.getUniqueId());

            if (bazaar == null) {
                return;
            }

            e.setCancelled(true);

            VillagerBazaar villagerBazaar = new VillagerBazaar(player, villager, plugin, bazaarManager, bazaar, perm);
            villagerBazaar.open();
        }
    }
}
