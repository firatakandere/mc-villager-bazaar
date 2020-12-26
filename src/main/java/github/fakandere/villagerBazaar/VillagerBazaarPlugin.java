package github.fakandere.villagerBazaar;

import com.google.inject.Injector;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

public class VillagerBazaarPlugin extends JavaPlugin {

    @Inject
    CommandOrchestrator commandOrchestrator;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        BinderModule module = new BinderModule(this);
        Injector injector= module.createInjector();
        injector.injectMembers(this);

        getCommand("bazaar").setExecutor(this.commandOrchestrator);
        registerCommands();
        getLogger().info("VillagerBazaar plugin is enabled.");

        getLogger().info(AnvilGUI.class.toString());
    }

    @Override
    public void onDisable() {

        getLogger().info("VillagerBazaar plugin is disabled.");
    }

    private void registerCommands() {
        // this.commandOrchestrator.addCommand("create", new CreateCommand(), "villagerbazaar.create");
    }
}
