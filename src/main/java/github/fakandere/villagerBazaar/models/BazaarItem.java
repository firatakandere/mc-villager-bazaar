package github.fakandere.villagerBazaar.models;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class BazaarItem implements ConfigurationSerializable {
    private Bazaar bazaar;
    private Material material;
    private int amount;
    private double sellPrice;
    private double buyPrice;

    // Configuration Constants
    private static final String MATERIAL = "MATERIAL";
    private static final String AMOUNT = "AMOUNT";
    private static final String SELL_PRICE = "SELL_PRICE";
    private static final String BUY_PRICE = "BUY_PRICE";

    public BazaarItem(Bazaar bazaar, Material material, int amount, double sellPrice, double buyPrice) throws InvalidInputException {
        setBazaar(bazaar);
        setMaterial(material);
        setAmount(amount);
        setSellPrice(sellPrice);
        setBuyPrice(buyPrice);
        setBazaar(bazaar);
    }

    public BazaarItem(Map<String, Object> serialized) {
        material = Material.valueOf((String)serialized.get(MATERIAL));
        amount = (int)serialized.get(AMOUNT);
        sellPrice = (double)serialized.get(SELL_PRICE);
        buyPrice = (double)serialized.get(BUY_PRICE);
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public Bazaar getBazaar() {
        return bazaar;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setAmount(int amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("At least 1 item is required.");
        }

        this.amount = amount;
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

        serialized.put(MATERIAL, material.toString());
        serialized.put(AMOUNT, amount);
        serialized.put(SELL_PRICE, sellPrice);
        serialized.put(BUY_PRICE, buyPrice);

        return serialized;
    }
}
