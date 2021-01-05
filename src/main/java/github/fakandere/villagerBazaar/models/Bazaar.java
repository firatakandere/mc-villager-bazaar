package github.fakandere.villagerBazaar.models;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

public class Bazaar implements ConfigurationSerializable {
    private UUID playerUniqueId;
    private BazaarType bazaarType;
    private BazaarInventory stocks = new BazaarInventory();
    private final List<BazaarItem> items = new ArrayList<>(7);
    private UUID villagerUniqueId;

    // Configuration Constants
    final static private String VILLAGER_UNIQUE_ID = "VILLAGER_UNIQUE_ID";
    final static private String PLAYER_UNIQUE_ID = "PLAYER_UNIQUE_ID";
    final static private String BAZAAR_TYPE = "BAZAAR_TYPE";
    final static private String STOCKS = "STOCKS";
    final static private String ITEMS = "ITEMS";

    public Bazaar() {
        IntStream.range(0, 7).forEach(i -> items.add(null));
    }

    public Bazaar(Map<String, Object> serialized) { // for deserialization
        IntStream.range(0, 7).forEach(i -> items.add(null));

        setVillagerUniqueId(UUID.fromString((String)serialized.get(VILLAGER_UNIQUE_ID)));
        setBazaarType(BazaarType.valueOf((String)serialized.get(BAZAAR_TYPE)));
        if (serialized.containsKey(PLAYER_UNIQUE_ID)) {
            setPlayerUniqueId(UUID.fromString((String)serialized.get(PLAYER_UNIQUE_ID)));
        }

        setStocks((BazaarInventory) serialized.get(STOCKS));

        int index = 0;
        for(BazaarItem item: ((List<BazaarItem>)serialized.get(ITEMS))) {
            setItem(index++, item);
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

    public BazaarInventory getStocks() {
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

    public void setStocks(BazaarInventory stocks) {
        this.stocks = stocks;
    }

    public void setStock(int index, ItemStack itemStack) {
        this.stocks.set(index, itemStack);
    }

    public void setItem(int index, BazaarItem item) {
        if (item != null) {
            item.setBazaar(this);
        }
        items.set(index, item);
    }

    public void removeItem(BazaarItem item) {
        items.remove(item);
    }

    public void setVillagerUniqueId(UUID villagerUniqueId) {
        this.villagerUniqueId = villagerUniqueId;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> serialized = new HashMap<>();

        serialized.put(VILLAGER_UNIQUE_ID, villagerUniqueId.toString());

        if (bazaarType == BazaarType.PLAYER) {
            serialized.put(PLAYER_UNIQUE_ID, playerUniqueId.toString());
        }

        serialized.put(BAZAAR_TYPE, bazaarType.toString());
        serialized.put(ITEMS,  items);

        serialized.put(STOCKS, stocks);

        return serialized;
    }
}
