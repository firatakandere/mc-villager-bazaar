package github.fakandere.villagerBazaar.repositories;

import github.fakandere.villagerBazaar.exceptions.NotFoundException;
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

    private final Integer configRevision = 1;

    private final String CONFIG_PATH = "BAZAAR";
    private final String CONFIG_REV_PATH = "CONFIG_REV";

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
            Bazaar bazaar = yml.getObject(CONFIG_PATH, Bazaar.class);
            bazaarMap.put(bazaar.getVillagerUniqueId(), bazaar);
        }

        return bazaarMap;
    }

    @Override
    public void createBazaar(Bazaar bazaar) throws UnexpectedException {
        final File file = new File(getBazaarFilePath(bazaar.getVillagerUniqueId()));
        try {
            file.createNewFile();
            YamlConfiguration yml = new YamlConfiguration();
            yml.set(CONFIG_PATH, bazaar);
            yml.set(CONFIG_REV_PATH, configRevision);
            yml.save(file);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create file for user registration", ex);
            throw new UnexpectedException("Could not create Bazaar");
        }
    }

    @Override
    public void updateBazaar(Bazaar bazaar) throws UnexpectedException, NotFoundException {
        final File file = new File(getBazaarFilePath(bazaar.getVillagerUniqueId()));

        if (!file.exists()) {
            throw new NotFoundException();
        }

        YamlConfiguration yml = new YamlConfiguration();
        yml.set(CONFIG_PATH, bazaar);
        yml.set(CONFIG_REV_PATH, configRevision);
        try {
            yml.save(file);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not update Bazaar", ex);
            throw new UnexpectedException("Could not update Bazaar");
        }
    }

    @Override
    public boolean deleteBazaar(UUID villagerUniqueId) throws NotFoundException {
        final File file = new File(getBazaarFilePath(villagerUniqueId));

        if (!file.exists()) {
            throw new NotFoundException();
        }

        return file.delete();
    }

    private String getBazaarFilePath(UUID villagerUniqueId) {
        return path + "/" + villagerUniqueId + ".yml";
    }
}
