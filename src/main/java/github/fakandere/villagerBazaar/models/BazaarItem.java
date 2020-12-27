package github.fakandere.villagerBazaar.models;

import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class BazaarItem implements ConfigurationSerializable {
    private Bazaar bazaar;
    private Material material;
    private double sellPrice;
    private double buyPrice;

    // Configuration Constants
    private static final String MATERIAL = "MATERIAL";
    private static final String SELL_PRICE = "SELL_PRICE";
    private static final String BUY_PRICE = "BUY_PRICE";

    public BazaarItem(Material material, double sellPrice, double buyPrice) throws InvalidInputException {
        setMaterial(material);
        setSellPrice(sellPrice);
        setBuyPrice(buyPrice);
        setBazaar(bazaar);
    }

    public BazaarItem(Map<String, Object> serialized) {
        material = Material.valueOf((String)serialized.get(MATERIAL));
        sellPrice = (double)serialized.get(SELL_PRICE);
        buyPrice = (double)serialized.get(BUY_PRICE);
    }

    public Material getMaterial() {
        return material;
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
        serialized.put(SELL_PRICE, sellPrice);
        serialized.put(BUY_PRICE, buyPrice);

        return serialized;
    }
}
