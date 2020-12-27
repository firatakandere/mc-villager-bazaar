package github.fakandere.villagerBazaar.repositories;

import github.fakandere.villagerBazaar.exceptions.NotFoundException;
import github.fakandere.villagerBazaar.models.Bazaar;

import java.rmi.UnexpectedException;
import java.util.Map;
import java.util.UUID;

public interface IBazaarRepository {
    Map<UUID, Bazaar> getBazaarMap();
    void createBazaar(Bazaar bazaar) throws UnexpectedException;
    void updateBazaar(Bazaar bazaar) throws UnexpectedException, NotFoundException;
    boolean deleteBazaar(UUID villagerUniqueId) throws NotFoundException;
}
