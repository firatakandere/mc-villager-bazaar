package github.fakandere.villagerBazaar.models;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.*;

public class Bazaar {
    private UUID playerUniqueId;
    private BazaarType bazaarType;
    private Map<Item, Integer> stocks = new HashMap<>();
    private List<BazaarItem> items = new ArrayList<>();
    private UUID villagerUniqueId;

    public BazaarType getBazaarType() {
        return bazaarType;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
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

    public void setStocks(Map<Item, Integer> stocks) {
        this.stocks = stocks;
    }

    public void addStock(Item item, int amount) throws InvalidInputException {
        if (amount < 0) {
            throw new InvalidInputException("A positive amount is expected.");
        }

        if (stocks.containsKey(item)) {
            stocks.put(item, stocks.get(item) + amount);
        } else {
            stocks.put(item, amount);
        }
    }

    public void removeStock(Item item, int amount) throws InvalidInputException {
        if (amount < 0) {
            throw new InvalidInputException("A positive amount is expected.");
        }

        if (stocks.containsKey(item)) {
            stocks.put(item, stocks.get(item) - amount);
        }
    }

    public void setItems(List<BazaarItem> items) {
        this.items = items;
    }

    public void addItem(BazaarItem item) {
        item.setBazaar(this);
        items.add(item);
    }

    public void setVillagerUniqueId(UUID villagerUniqueId) {
        this.villagerUniqueId = villagerUniqueId;
    }
}
