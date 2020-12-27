package github.fakandere.villagerBazaar.utils;

import github.fakandere.villagerBazaar.exceptions.InsufficientFundsException;
import github.fakandere.villagerBazaar.exceptions.InvalidInputException;
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

    private Map<UUID, Bazaar> bazaars;
    private IBazaarRepository bazaarRepository;

    @Inject
    Economy econ;

    public BazaarManager() {
        // load models
        bazaars = bazaarRepository.getBazaarMap();
    }

    public Bazaar getBazaar(UUID villagerUniqueId) {
        return bazaars.getOrDefault(villagerUniqueId, null);
    }

    public void deleteBazaar(UUID villagerUniqueId) {
        bazaarRepository.deleteBazaar(villagerUniqueId);
        // @todo delete villager here or somewhere else?
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

    public void makePurchase(BazaarItem bazaarItem, Player player, int quantity) throws InsufficientFundsException, TransactionFailureException, InvalidInputException, UnexpectedException {
        Bazaar bazaar = bazaarItem.getBazaar();
        OfflinePlayer bazaarOwner = null;

        if (bazaar.getBazaarType() == BazaarType.PLAYER) {
            bazaarOwner = Bukkit.getServer().getOfflinePlayer(bazaar.getPlayerUniqueId());
        }

        synchronized (bazaar) {
            double totalPrice = bazaarItem.getBuyPrice() * quantity;

            // @todo maybe check if bazaar has enough quantity, idk

            if (!econ.has(player, totalPrice)) {
                throw new InsufficientFundsException();
            }

            if (!econ.withdrawPlayer(player, totalPrice).transactionSuccess()) {
                throw new TransactionFailureException();
            }

            if (bazaarOwner != null) {
                econ.depositPlayer(bazaarOwner, totalPrice);
            }

            bazaar.removeStock(bazaarItem.getMaterial(), quantity * bazaarItem.getAmount());
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void addStock(Bazaar bazaar, Material material, int amount) throws InvalidInputException, UnexpectedException {
        synchronized (bazaar) {
            bazaar.addStock(material, amount);
            bazaarRepository.updateBazaar(bazaar);
        }
    }

    public void removeStock(Bazaar bazaar, Material material, int amount) throws InvalidInputException, UnexpectedException {
        synchronized (bazaar) {
            bazaar.removeStock(material,amount);
            bazaarRepository.updateBazaar(bazaar);
        }
    }





}
