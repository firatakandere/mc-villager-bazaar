package github.fakandere.villagerBazaar.repositories;

import github.fakandere.villagerBazaar.models.Bazaar;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class FileRepository implements IBazaarRepository {

    private final String path = "plugins/VillagerBazaar/Bazaars";

    public FileRepository() {
        new File(path).mkdirs();
    }

    @Override
    public Map<UUID, Bazaar> getBazaarMap() {
        Map<UUID, Bazaar> bazaarMap = new HashMap<>();

        File [] bazaarFiles = (new File(path)).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });

        for (File bazaarFile : bazaarFiles) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(bazaarFile);
            Bazaar bazaar = yml.getObject("BAZAAR", Bazaar.class);
            bazaarMap.put(bazaar.getVillagerUniqueId(), bazaar);
        }

        return bazaarMap;
    }

    @Override
    public void createBazaar(Bazaar bazaar) throws UnexpectedException {
        final File file = new File(getBazaarFilePath(bazaar.getVillagerUniqueId()));
        try {
            file.createNewFile();
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            yml.set("BAZAAR", bazaar);
            yml.save(file);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create file for user registration", ex);
            throw new UnexpectedException("Could not create Bazaar");
        }
    }

    @Override
    public void updateBazaar(Bazaar bazaar) throws UnexpectedException {
        final File file = new File(getBazaarFilePath(bazaar.getVillagerUniqueId()));

        if (!file.exists()) {
            // @todo throw not found exception
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("BAZAAR", bazaar);
        try {
            yml.save(file);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not update Bazaar", ex);
            throw new UnexpectedException("Could not update Bazaar");
        }
    }

    @Override
    public void deleteBazaar(UUID villagerUniqueId) {

    }

    private String getBazaarFilePath(UUID villagerUniqueId) {
        return path + "/" + villagerUniqueId + ".yml";
    }
}
