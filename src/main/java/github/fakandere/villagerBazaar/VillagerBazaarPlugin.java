package github.fakandere.villagerBazaar;

import com.google.inject.Injector;
import com.google.inject.Inject;
import github.fakandere.villagerBazaar.commands.CreateAdminCommand;
import github.fakandere.villagerBazaar.commands.CreateCommand;
import github.fakandere.villagerBazaar.listeners.VillagerBazaarInteractionListener;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.models.BazaarItem;

import github.fakandere.villagerBazaar.utils.AnvilGUIHelper;
import org.bukkit.Bukkit;



import org.bukkit.configuration.serialization.ConfigurationSerialization;


import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;


public class VillagerBazaarPlugin extends JavaPlugin {


    @Inject
    VillagerBazaarInteractionListener villagerBazaarInteractionListener;

    @Inject
    ICommandOrchestrator commandOrchestrator;

    @Inject
    CreateCommand createCommand;

    @Inject
    CreateAdminCommand createAdminCommand;

    @Override
    public void onEnable() {
        //Check Vault's existence
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        AnvilGUIHelper.setUp(this);
        BinderModule module = new BinderModule(this);
        Injector injector= module.createInjector();
        injector.injectMembers(this);

        registerConfigurationSerializations();
        registerCommands();
        getLogger().info("VillagerBazaar plugin is enabled.");
        Bukkit.getPluginManager().registerEvents(this.villagerBazaarInteractionListener, this);
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
    }

    @Override
    public void onDisable() {

        getLogger().info("VillagerBazaar plugin is disabled.");
    }

    private void registerCommands() {
        getCommand("bazaar").setExecutor(commandOrchestrator);

        commandOrchestrator.addCommand("create", createCommand, "villagerbazaar.create");
        commandOrchestrator.addCommand("createadmin", createAdminCommand, "villagerbazaar.createadmin");
    }

    private void registerConfigurationSerializations() {
        ConfigurationSerialization.registerClass(Bazaar.class);
        ConfigurationSerialization.registerClass(BazaarItem.class);
    }

}
