package github.fakandere.villagerBazaar.models;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Bazaar implements ConfigurationSerializable {
    private UUID playerUniqueId;
    private BazaarType bazaarType;
    private Map<Material, Integer> stocks = new HashMap<>();
    private List<BazaarItem> items = new ArrayList<>();
    private UUID villagerUniqueId;

    // Configuration Constants
    final static private String VILLAGER_UNIQUE_ID = "VILLAGER_UNIQUE_ID";
    final static private String PLAYER_UNIQUE_ID = "PLAYER_UNIQUE_ID";
    final static private String BAZAAR_TYPE = "BAZAAR_TYPE";
    final static private String STOCKS = "STOCKS";
    final static private String ITEMS = "ITEMS";

    public Bazaar() {}

    public Bazaar(Map<String, Object> serialized) { // for deserialization
        setVillagerUniqueId(UUID.fromString((String)serialized.get(VILLAGER_UNIQUE_ID)));
        setBazaarType(BazaarType.valueOf((String)serialized.get(BAZAAR_TYPE)));
        if (serialized.containsKey(PLAYER_UNIQUE_ID)) {
            setPlayerUniqueId(UUID.fromString((String)serialized.get(PLAYER_UNIQUE_ID)));
        }

        for (Map.Entry<String, Integer> stockEntry : ((Map<String,Integer>)serialized.get(STOCKS)).entrySet()) {
            try {
                addStock(Material.valueOf(stockEntry.getKey()), stockEntry.getValue());
            } catch (InvalidInputException e) {
                e.printStackTrace();
                // @todo ignore?
            }
        }

        for(BazaarItem item: ((List<BazaarItem>)serialized.get(ITEMS))) {
            addItem(item);
        }
    }

    public BazaarType getBazaarType() {
        return bazaarType;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public UUID getVillagerUniqueId() {
        return villagerUniqueId;
    }

    public Map<Material, Integer> getStocks() {
        return stocks;
    }

    public List<BazaarItem> getItems() {
        return items;
    }

    public void setPlayerUniqueId(UUID playerUniqueId) {
        if (bazaarType == BazaarType.ADMIN) {
            Bukkit.getLogger().warning("Admin bazaar cannot have an owner.");
            return;
        }

        this.playerUniqueId = playerUniqueId;
    }

    public void setBazaarType(BazaarType bazaarType)  {
        if (bazaarType == BazaarType.ADMIN) {
            this.playerUniqueId = null;
        }

        this.bazaarType = bazaarType;
    }

    public void setStocks(Map<Material, Integer> stocks) {
        this.stocks = stocks;
    }

    public void addStock(Material material, int amount) throws InvalidInputException {
        if (bazaarType == BazaarType.ADMIN) {
            return; // Ignore
        }

        if (amount < 0) {
            throw new InvalidInputException("A positive amount is expected.");
        }

        if (stocks.containsKey(material)) {
            stocks.put(material, stocks.get(material) + amount);
        } else {
            stocks.put(material, amount);
        }
    }

    public void removeStock(Material material, int amount) throws InvalidInputException {
        if (bazaarType == BazaarType.ADMIN) {
            return; // Ignore
        }

        if (amount < 0) {
            throw new InvalidInputException("A positive amount is expected.");
        }

        if (stocks.containsKey(material)) {
            stocks.put(material, stocks.get(material) - amount);
        }
    }

    public void addItem(BazaarItem item) {
        item.setBazaar(this);
        items.add(item);
    }

    public void removeItem(BazaarItem item) {
        items.remove(item);
    }

    public void setVillagerUniqueId(UUID villagerUniqueId) {
        this.villagerUniqueId = villagerUniqueId;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Integer> serializedStocks = new HashMap<String, Integer>();
        for (Map.Entry<Material, Integer> entry : stocks.entrySet()) {
            serializedStocks.put(entry.getKey().toString(), entry.getValue());
        }

        Map<String, Object> serialized = new HashMap<>();

        serialized.put(VILLAGER_UNIQUE_ID, villagerUniqueId.toString());

        if (bazaarType == BazaarType.PLAYER) {
            serialized.put(PLAYER_UNIQUE_ID, playerUniqueId.toString());
        }

        serialized.put(BAZAAR_TYPE, bazaarType.toString());
        serialized.put(STOCKS, serializedStocks);
        serialized.put(ITEMS,  items);


        return serialized;
    }
}
