package com.kirpideleri.villagerBazaar;

import com.google.inject.Injector;
import com.google.inject.Inject;
import com.kirpideleri.villagerBazaar.commands.CreateCommand;
import com.kirpideleri.villagerBazaar.models.Bazaar;
import com.kirpideleri.villagerBazaar.commands.CreateAdminCommand;
import com.kirpideleri.villagerBazaar.listeners.VillagerBazaarInteractionListener;
import com.kirpideleri.villagerBazaar.models.BazaarInventory;
import com.kirpideleri.villagerBazaar.models.BazaarItem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
        registerConfigurationSerializations();

        //Check Vault's existence
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        BinderModule module = new BinderModule(this);
        Injector injector= module.createInjector();
        injector.injectMembers(this);

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
        commandOrchestrator.addCommand("createadmin", createAdminCommand, "villagerbazaar.admin.create");
    }

    private void registerConfigurationSerializations() {
        ConfigurationSerialization.registerClass(BazaarInventory.class);
        ConfigurationSerialization.registerClass(BazaarItem.class);
        ConfigurationSerialization.registerClass(Bazaar.class);
    }

}
