package github.fakandere.villagerBazaar.listeners;

import com.google.inject.Inject;
import github.fakandere.villagerBazaar.VillagerBazaar;
import github.fakandere.villagerBazaar.VillagerBazaarPlugin;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.prompts.BStringPrompt;
import github.fakandere.villagerBazaar.prompts.PromptFactory;
import github.fakandere.villagerBazaar.utils.IBazaarManager;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteractEntity(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            Player p = e.getPlayer();
            Villager v = (Villager) e.getRightClicked();


            new PromptFactory(plugin)
                    .player(p)
                    .addPrompt(new BStringPrompt("Please enter your firstname"), "firstname")
                    .addPrompt(new BStringPrompt("Please enter your middle name"), "middlename")
                    .addPrompt(new BStringPrompt("Please enter your last name"), "lastname")
                    .onComplete(map -> {
                        Bukkit.getLogger().info("Your full name is: " + map.get("firstname") + " " + map.get("middlename") + " " + map.get("lastname"));
                    })
                    .build()
                    .begin();


            return;
//            Bazaar bazaar = bazaarManager.getBazaar(v.getUniqueId());
//
//            if (bazaar == null) {
//                return;
//            }
//
//            e.setCancelled(true);
//
//            VillagerBazaar vb = new VillagerBazaar(p, v, e, bazaar,bazaarManager);
//            vb.startBazaar();
        }
    }
}
