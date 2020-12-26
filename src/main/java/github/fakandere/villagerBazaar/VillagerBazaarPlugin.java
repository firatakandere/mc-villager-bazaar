package github.fakandere.villagerBazaar;

import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public class VillagerBazaarPlugin extends JavaPlugin {

    final private CommandOrchestrator commandOrchestrator = new CommandOrchestrator();

    @Override
    public void onEnable() {
        //getCommand("bazaar").setExecutor(this.commandOrchestrator);
        registerCommands();
        getLogger().info("VillagerBazaar plugin is enabled.");


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
                this.createVillager(p, args);
            }
        }
        return true;
    }

    public boolean createVillager(Player p, String[] args) {
        Villager v = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
        v.setCustomName("KOYLU");
        v.setInvulnerable(true);

        new AnvilGUI.Builder()
                .onComplete((player, text) -> {                             //called when the inventory output slot is clicked
                    v.setCustomName(text.replaceAll("[^a-zA-Z0-9\\s]", ""));
                    return AnvilGUI.Response.close();
                })
                .preventClose()                                             //prevents the inventory from being closed
                .text("Shop?")                      //sets the text the GUI should start with
                .title("What is your shops name")                        //set the title of the GUI (only works in 1.14+)
                .open(p);                                            //opens the GUI for the player provided

        p.sendMessage("Villager created!");
        p.sendMessage(args[0]);
        return true;
    }

}
