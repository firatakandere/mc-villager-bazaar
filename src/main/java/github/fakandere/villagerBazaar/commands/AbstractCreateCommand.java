package github.fakandere.villagerBazaar.commands;

import com.google.inject.Inject;
import github.fakandere.villagerBazaar.VillagerBazaarPlugin;
import github.fakandere.villagerBazaar.prompts.BStringPrompt;
import github.fakandere.villagerBazaar.prompts.PromptFactory;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.UUID;

public abstract class AbstractCreateCommand implements CommandExecutor {

    @Inject
    VillagerBazaarPlugin plugin;

    protected UUID createVillager(Player p) {
        Location targetLocation = p.getTargetBlock(null, 10).getLocation();
        targetLocation.add(0.5, 1, 0.5);
        //@todo: Is this block appropriate is it lava or water or anything sketchy ?
        //throw VillagerShopInvalidPlacementException
        Villager v = (Villager) p.getWorld().spawnEntity(targetLocation, EntityType.VILLAGER);
        //Set properties
        v.setCustomName("Villager of " + p.getName());
        v.setInvulnerable(true);
        v.setAI(false);
        v.setVillagerLevel(5);
        v.setVillagerType(Villager.Type.PLAINS);
        v.setProfession(Villager.Profession.NONE);

//        new PromptFactory(plugin)
//                .player(p)
//                .addPrompt(new BStringPrompt("Please type your shop's name to the chat, type `cancel` to cancel"), "villagername")
//                .onComplete(map -> {
//                    v.setCustomName(map.get("villagername").toString());
//                })
//                .withTimeout(30)
//                .withCancellationToken("cancel")
//                .build()
//                .begin();

        return v.getUniqueId();
    }
}
