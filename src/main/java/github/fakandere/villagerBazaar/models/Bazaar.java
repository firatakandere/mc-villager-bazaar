package github.fakandere.villagerBazaar.models;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.*;

public class Bazaar {
    private Player owner; // or maybe uuid for simplicity
    private BazaarType bazaarType;
    private Map<Item, Integer> stocks = new HashMap<>();
    private List<BazaarItem> items = new ArrayList<>();
    private UUID villagerUniqueId;

    public void setOwner(Player owner) {
        if (bazaarType == BazaarType.ADMIN) {
            Bukkit.getLogger().warning("Admin bazaar cannot have an owner.");
            return;
        }

        this.owner = owner;
    }

    public void setBazaarType(BazaarType bazaarType)  {
        if (bazaarType == BazaarType.ADMIN) {
            this.owner = null;
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

    public void setItems(List<BazaarItem> items) {
        this.items = items;
    }

    public void addItem(BazaarItem item) {
        items.add(item);
    }

    public void setVillagerUniqueId(UUID villagerUniqueId) {
        this.villagerUniqueId = villagerUniqueId;
    }
}
