package com.kirpideleri.villagerBazaar.models;

import com.kirpideleri.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BazaarItem implements ConfigurationSerializable {
    private Bazaar bazaar;
    private double sellPrice;
    private double buyPrice;
    private ItemStack itemStack;

    // Configuration Constants
    private static final String ITEM_STACK = "ITEM_STACK";
    private static final String SELL_PRICE = "SELL_PRICE";
    private static final String BUY_PRICE = "BUY_PRICE";

    public BazaarItem(ItemStack itemStack, double sellPrice, double buyPrice) throws InvalidInputException {
        setItemStack(itemStack);
        setSellPrice(sellPrice);
        setBuyPrice(buyPrice);
        setBazaar(bazaar);
    }

    public BazaarItem(Map<String, Object> serialized) {
        itemStack = (ItemStack) serialized.get(ITEM_STACK);
        sellPrice = (double)serialized.get(SELL_PRICE);
        buyPrice = (double)serialized.get(BUY_PRICE);
    }

    public ItemStack getItemStack() { return itemStack; }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public Bazaar getBazaar() {
        return bazaar;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }


    public void setSellPrice(double sellPrice) throws InvalidInputException {
        if (sellPrice <= 0.0) {
            throw new InvalidInputException("A positive price is required.");
        }

        this.sellPrice = sellPrice;
    }

    public void setBuyPrice(double buyPrice) throws InvalidInputException {
        if (buyPrice <= 0.0) {
            throw new InvalidInputException("A positive price is required.");
        }

        this.buyPrice = buyPrice;
    }

    public void setBazaar(Bazaar bazaar) {
        this.bazaar = bazaar;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> serialized = new HashMap<>();

        serialized.put(ITEM_STACK, itemStack);
        serialized.put(SELL_PRICE, sellPrice);
        serialized.put(BUY_PRICE, buyPrice);

        return serialized;
    }
}
