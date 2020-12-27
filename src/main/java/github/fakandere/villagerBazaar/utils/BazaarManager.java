package github.fakandere.villagerBazaar.utils;

import github.fakandere.villagerBazaar.exceptions.InsufficientFundsException;
import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
import github.fakandere.villagerBazaar.exceptions.NotFoundException;
import github.fakandere.villagerBazaar.exceptions.TransactionFailureException;
import github.fakandere.villagerBazaar.models.Bazaar;
import github.fakandere.villagerBazaar.models.BazaarItem;
import github.fakandere.villagerBazaar.models.BazaarType;
import github.fakandere.villagerBazaar.repositories.IBazaarRepository;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

                try {
                    bazaar.removeStock(bazaarItem.getMaterial(), quantity * bazaarItem.getAmount());
                    bazaarRepository.updateBazaar(bazaar);
                } catch (InvalidInputException | UnexpectedException | NotFoundException ex) {
                    throw new UnexpectedException(ex.getMessage());
                }
            }
        }
    }

    public void addStock(Bazaar bazaar, Material material, int amount) throws InvalidInputException, UnexpectedException, NotFoundException {
        // @todo where to update user inventory?

        synchronized (bazaar) {
            bazaar.addStock(material, amount);
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void removeStock(Bazaar bazaar, Material material, int amount) throws InvalidInputException, UnexpectedException, NotFoundException {
        // @todo where to update user inventory?
        // @todo check if it exceeds current stock?

        synchronized (bazaar) {
            bazaar.removeStock(material,amount);
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void addItem(Bazaar bazaar, Material material, int amount, double sellPrice, double buyPrice) throws InvalidInputException, UnexpectedException, NotFoundException {
        synchronized (bazaar) {
            bazaar.addItem(new BazaarItem(material, amount, sellPrice, buyPrice));
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void removeItem(Bazaar bazaar, BazaarItem item) throws UnexpectedException, NotFoundException {
        synchronized (bazaar) {
            bazaar.removeItem(item);
            bazaarRepository.updateBazaar(bazaar);
        }
    }
}
