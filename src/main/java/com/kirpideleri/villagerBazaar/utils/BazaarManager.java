package com.kirpideleri.villagerBazaar.utils;

import com.kirpideleri.villagerBazaar.models.Bazaar;
import com.kirpideleri.villagerBazaar.repositories.IBazaarRepository;
import com.kirpideleri.villagerBazaar.exceptions.InsufficientFundsException;
import com.kirpideleri.villagerBazaar.exceptions.InvalidInputException;
import com.kirpideleri.villagerBazaar.exceptions.NotFoundException;
import com.kirpideleri.villagerBazaar.exceptions.TransactionFailureException;
import com.kirpideleri.villagerBazaar.models.BazaarItem;
import com.kirpideleri.villagerBazaar.models.BazaarType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.rmi.UnexpectedException;
import java.util.Map;
import java.util.UUID;

public class BazaarManager implements IBazaarManager {

    private final Map<UUID, Bazaar> bazaars;

    Economy econ;
    IBazaarRepository bazaarRepository;

    @Inject
    public BazaarManager(Economy econ, IBazaarRepository bazaarRepository) {
        this.econ = econ;
        this.bazaarRepository = bazaarRepository;
        // load models
        bazaars = this.bazaarRepository.getBazaarMap();
    }

    public Bazaar getBazaar(UUID villagerUniqueId) {
        return bazaars.getOrDefault(villagerUniqueId, null);
    }

    public void deleteBazaar(UUID villagerUniqueId) throws NotFoundException {
        bazaarRepository.deleteBazaar(villagerUniqueId);
        // @todo delete villager here or somewhere else?
        // @todo return stocks back to player or drop to the floor?
        bazaars.remove(villagerUniqueId);
    }

    public void createAdminBazaar(UUID villagerUniqueId) throws UnexpectedException {
        Bazaar bazaar = new Bazaar();
        bazaar.setBazaarType(BazaarType.ADMIN);
        bazaar.setVillagerUniqueId(villagerUniqueId);
        bazaarRepository.createBazaar(bazaar);
        bazaars.put(villagerUniqueId, bazaar);
    }

    public void createPlayerBazaar(UUID villagerUniqueId, UUID playerUniqueId) throws UnexpectedException {
        Bazaar bazaar = new Bazaar();
        bazaar.setBazaarType(BazaarType.PLAYER);
        bazaar.setVillagerUniqueId(villagerUniqueId);
        bazaar.setPlayerUniqueId(playerUniqueId);
        bazaarRepository.createBazaar(bazaar);
        bazaars.put(villagerUniqueId, bazaar);
    }

    public void makePurchase(BazaarItem bazaarItem, Player player, int quantity) throws InsufficientFundsException, TransactionFailureException, UnexpectedException {
        Bazaar bazaar = bazaarItem.getBazaar();
        OfflinePlayer bazaarOwner = null;

        if (bazaar.getBazaarType() == BazaarType.PLAYER) {
            bazaarOwner = Bukkit.getServer().getOfflinePlayer(bazaar.getPlayerUniqueId());
        }

        synchronized (bazaar) {
            double totalPrice = bazaarItem.getBuyPrice() * quantity;

            // @todo maybe check if bazaar has enough quantity, idk
            // @todo need to check if buyer has enough spots?

            if (!econ.has(player, totalPrice)) {
                throw new InsufficientFundsException();
            }

            if (!econ.withdrawPlayer(player, totalPrice).transactionSuccess()) {
                throw new TransactionFailureException();
            }

            if (bazaar.getBazaarType() == BazaarType.PLAYER) {
                econ.depositPlayer(bazaarOwner, totalPrice);

//                try {
//                    bazaar.removeStock(bazaarItem.getMaterial(), quantity);
//                    bazaarRepository.updateBazaar(bazaar);
//                } catch (InvalidInputException | UnexpectedException | NotFoundException ex) {
//                    throw new UnexpectedException(ex.getMessage());
//                }
            }
        }
    }

    public void setItem(Bazaar bazaar, int index, ItemStack itemStack, double sellPrice, double buyPrice) throws UnexpectedException, NotFoundException, InvalidInputException {
        synchronized (bazaar) {
            bazaar.setItem(index, new BazaarItem(itemStack, sellPrice, buyPrice));
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void deleteItem(Bazaar bazaar, int index) throws UnexpectedException, NotFoundException {
        synchronized (bazaar) {
            bazaar.removeItem(index);
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void setStock(Bazaar bazaar, int index, ItemStack itemStack) throws UnexpectedException, NotFoundException {
        synchronized (bazaar) {
            bazaar.setStock(index, itemStack);
            bazaarRepository.updateBazaar(bazaar);
        }
    }
}
