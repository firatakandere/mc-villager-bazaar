package github.fakandere.villagerBazaar;

import com.google.inject.Injector;
import com.google.inject.Inject;
import github.fakandere.villagerBazaar.listeners.VillagerBazaarInteractionListener;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.models.BazaarItem;
import github.fakandere.villagerBazaar.repositories.IBazaarRepository;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

import java.util.ArrayList;
import java.util.UUID;

public class VillagerBazaarPlugin extends JavaPlugin {

    public ArrayList<UUID> shopList = new ArrayList<>();

    @Inject
    VillagerBazaarInteractionListener villagerInteractionListener;

    @Inject
    ICommandOrchestrator commandOrchestrator;

    @Inject
    IBazaarRepository bazaarRepository;

    @Override
    public void onEnable() {
        //Check Vault's existence
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        BinderModule module = new BinderModule(this);
        Injector injector= module.createInjector();
        injector.injectMembers(this);

        getCommand("bazaar").setExecutor(this.commandOrchestrator);
        registerConfigurationSerializations();
        registerCommands();
        getLogger().info("VillagerBazaar plugin is enabled.");
        Bukkit.getPluginManager().registerEvents(this.villagerInteractionListener, this);

        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
    }

    @Override
    public void onDisable() {

        getLogger().info("VillagerBazaar plugin is disabled.");
    }

    private void registerCommands() {
        // this.commandOrchestrator.addCommand("create", new CreateCommand(), "villagerbazaar.create");
    }

    private void registerConfigurationSerializations() {
        ConfigurationSerialization.registerClass(Bazaar.class);
        ConfigurationSerialization.registerClass(BazaarItem.class);
    }




}
