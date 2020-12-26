package github.fakandere.villagerBazaar;

import org.bukkit.plugin.java.JavaPlugin;

public class VillagerBazaarPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("VillagerBazaar plugin is enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("VillagetBazaar plugin is disabled.");
    }
}
