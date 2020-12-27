package github.fakandere.villagerBazaar.listeners;

import com.google.inject.Inject;
import github.fakandere.villagerBazaar.VillagerBazaar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;



public class VillagerBazaarInteractionListener implements Listener{

    @Inject
    VillagerBazaar vb;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteractEntity(final PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if(e.getRightClicked() instanceof Villager) {
            Villager v = (Villager) e.getRightClicked();
            vb.setVillager(v);
            vb.setPlayer(p);
            vb.setEvent(e);
            vb.startBazaar();
        }
    }



}
