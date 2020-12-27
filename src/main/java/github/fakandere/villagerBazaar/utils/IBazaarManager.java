package github.fakandere.villagerBazaar.utils;

import github.fakandere.villagerBazaar.exceptions.InsufficientFundsException;
import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import github.fakandere.villagerBazaar.exceptions.NotFoundException;
import github.fakandere.villagerBazaar.exceptions.TransactionFailureException;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.models.BazaarItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.rmi.UnexpectedException;
import java.util.UUID;

public interface IBazaarManager {
    Bazaar getBazaar(UUID villagerUniqueId);
    void deleteBazaar(UUID villagerUniqueId) throws NotFoundException;
    void createAdminBazaar(UUID villagerUniqueId) throws UnexpectedException;
    void createPlayerBazaar(UUID villagerUniqueId, UUID playerUniqueId) throws UnexpectedException;
    void makePurchase(BazaarItem bazaarItem, Player player, int quantity) throws InsufficientFundsException, TransactionFailureException, UnexpectedException;
    void addStock(Bazaar bazaar, Material material, int amount) throws InvalidInputException, UnexpectedException, NotFoundException;
    void removeStock(Bazaar bazaar, Material material, int amount) throws InvalidInputException, UnexpectedException, NotFoundException;
    void addItem(Bazaar bazaar, Material material, double sellPrice, double buyPrice, int amount) throws  InvalidInputException, UnexpectedException, NotFoundException;
    void removeItem(Bazaar bazaar, BazaarItem item) throws UnexpectedException, NotFoundException;

}
