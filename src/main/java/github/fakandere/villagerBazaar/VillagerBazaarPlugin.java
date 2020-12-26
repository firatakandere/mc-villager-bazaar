package github.fakandere.villagerBazaar;

import com.google.inject.Inject;
import github.fakandere.villagerBazaar.listeners.VillagerInteractionListener;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public class VillagerBazaarPlugin extends JavaPlugin {

    public ArrayList<UUID> shopList = new ArrayList<>();

    @Inject
    VillagerInteractionListener villagerInteractionListener;

    final private CommandOrchestrator commandOrchestrator = new CommandOrchestrator();

    @Override
    public void onEnable() {
        //getCommand("bazaar").setExecutor(this.commandOrchestrator);
        registerCommands();
        getLogger().info("VillagerBazaar plugin is enabled.");
        Bukkit.getPluginManager().registerEvents(this.villagerInteractionListener, this);

    }

    @Override
    public void onDisable() {

        getLogger().info("VillagerBazaar plugin is disabled.");
    }

    private void registerCommands() {
        // this.commandOrchestrator.addCommand("create", new CreateCommand(), "villagerbazaar.create");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bazaar")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                UUID villagerID = this.createVillager(p);
                shopList.add(villagerID);
                p.sendMessage(villagerID.toString());
            }
        }
        return true;
    }

    public UUID createVillager(Player p) {
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
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                    v.setCustomName(text.replaceAll("[^a-zA-Z0-9\\s]", ""));
                    return AnvilGUI.Response.close();
                })
                .preventClose()                                             //prevents the inventory from being closed
                .text("Shop?")                      //sets the text the GUI should start with
                .title("What is your shop's name")                        //set the title of the GUI (only works in 1.14+)
                .plugin(this)
                .open(p);                                            //opens the GUI for the player provided


        return v.getUniqueId();

    }


}
