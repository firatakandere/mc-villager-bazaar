package github.fakandere.villagerBazaar.repositories;

import github.fakandere.villagerBazaar.models.Bazaar;

import java.util.UUID;

public interface IBazaarRepository {
    boolean addBazaar(Bazaar bazaar);
    boolean updateBazaar(Bazaar bazaar);
    boolean deleteBazaar(UUID villagerUniqueId);
}
