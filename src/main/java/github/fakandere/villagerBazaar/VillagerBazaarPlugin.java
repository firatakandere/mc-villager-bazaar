package github.fakandere.villagerBazaar;

import org.bukkit.plugin.java.JavaPlugin;

public class VillagerBazaarPlugin extends JavaPlugin {

    final private CommandOrchestrator commandOrchestrator = new CommandOrchestrator();

    @Override
    public void onEnable() {
        getCommand("bazaar").setExecutor(this.commandOrchestrator);
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
}
