package github.fakandere.villagerBazaar.utils;

import github.fakandere.villagerBazaar.exceptions.InsufficientFundsException;
import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import github.fakandere.villagerBazaar.exceptions.NotFoundException;
import github.fakandere.villagerBazaar.exceptions.TransactionFailureException;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.models.BazaarItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.rmi.UnexpectedException;
import java.util.UUID;

public interface IBazaarManager {
    Bazaar getBazaar(UUID villagerUniqueId);
    void deleteBazaar(UUID villagerUniqueId) throws NotFoundException;
    void createAdminBazaar(UUID villagerUniqueId) throws UnexpectedException;
    void createPlayerBazaar(UUID villagerUniqueId, UUID playerUniqueId) throws UnexpectedException;
    void makePurchase(BazaarItem bazaarItem, Player player, int quantity) throws InsufficientFundsException, TransactionFailureException, UnexpectedException;
    void setItem(Bazaar bazaar, int index, ItemStack itemStack, double sellPrice, double buyPrice) throws UnexpectedException, NotFoundException, InvalidInputException;
    void setStock(Bazaar bazaar, int index, ItemStack itemStack) throws UnexpectedException, NotFoundException;

}
