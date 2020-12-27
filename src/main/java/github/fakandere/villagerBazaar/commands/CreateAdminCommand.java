package github.fakandere.villagerBazaar.commands;

import github.fakandere.villagerBazaar.VillagerBazaarPlugin;
import github.fakandere.villagerBazaar.utils.AnvilGUIHelper;
import github.fakandere.villagerBazaar.utils.IBazaarManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import javax.inject.Inject;
import java.rmi.UnexpectedException;
import java.util.UUID;

public class CreateAdminCommand implements CommandExecutor {
    VillagerBazaarPlugin plugin;
    IBazaarManager bazaarManager;

    @Inject
    public CreateAdminCommand(VillagerBazaarPlugin plugin, IBazaarManager bazaarManager) {
        this.plugin = plugin;
        this.bazaarManager = bazaarManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player p = (Player) commandSender;
        UUID villagerID = this.createVillager(p);
        try {
            bazaarManager.createAdminBazaar(villagerID);
        } catch (UnexpectedException e) {
            p.sendMessage("An unexpected error occurred during bazaar creation, please contact with server admin.");
        }
        p.sendMessage(villagerID.toString());
        return true;
    }

    private UUID createVillager(Player p) {
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

        AnvilGUIHelper.prompt(p, "What is your shop's name", "Shop?", (text) -> v.setCustomName(text.replaceAll("[^a-zA-Z0-9\\s]", "")));

        return v.getUniqueId();
    }
}
