package github.fakandere.villagerBazaar.models;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.entity.Item;

public class BazaarItem {
    private Bazaar bazaar;
    private Item item;
    private int amount;
    private float sellPrice;
    private float buyPrice;

    public BazaarItem(Bazaar bazaar, Item item, int amount, float sellPrice, float buyPrice) throws InvalidInputException {
        setBazaar(bazaar);
        setItem(item);
        setAmount(amount);
        setSellPrice(sellPrice);
        setBuyPrice(buyPrice);
        setBazaar(bazaar);
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public Bazaar getBazaar() {
        return bazaar;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setAmount(int amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("At least 1 item is required.");
        }

        this.amount = amount;
    }

    public void setSellPrice(float sellPrice) throws InvalidInputException {
        if (sellPrice <= 0.0) {
            throw new InvalidInputException("A positive price is required.");
        }

        this.sellPrice = sellPrice;
    }

    public void setBuyPrice(float buyPrice) throws InvalidInputException {
        if (buyPrice <= 0.0) {
            throw new InvalidInputException("A positive price is required.");
        }

        this.buyPrice = buyPrice;
    }

    public void setBazaar(Bazaar bazaar) {
        this.bazaar = bazaar;
    }
}
