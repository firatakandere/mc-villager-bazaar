package com.kirpideleri.villagerBazaar;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.kirpideleri.villagerBazaar.repositories.FileRepository;
import com.kirpideleri.villagerBazaar.repositories.IBazaarRepository;
import com.kirpideleri.villagerBazaar.utils.BazaarManager;
import com.kirpideleri.villagerBazaar.utils.IBazaarManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class BinderModule extends AbstractModule {
    private final VillagerBazaarPlugin plugin;

    public BinderModule(VillagerBazaarPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() throws NullPointerException {
        this.bind(VillagerBazaarPlugin.class).toInstance(this.plugin);
        this.bind(ICommandOrchestrator.class).to(CommandOrchestrator.class).in(Scopes.SINGLETON);
        this.bind(IBazaarManager.class).to(BazaarManager.class).in(Scopes.SINGLETON);
        this.bind(IBazaarRepository.class).to(FileRepository.class).in(Scopes.SINGLETON);
        this.bind(Economy.class).toInstance(this.plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider());
        this.bind(Permission.class).toInstance(this.plugin.getServer().getServicesManager().getRegistration(Permission.class).getProvider());
    }
}
