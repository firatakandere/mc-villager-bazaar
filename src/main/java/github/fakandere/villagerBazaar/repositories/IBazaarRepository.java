package github.fakandere.villagerBazaar.repositories;

import github.fakandere.villagerBazaar.models.Bazaar;

import java.util.Map;
import java.util.UUID;

public interface IBazaarRepository {
    Map<UUID, Bazaar> getBazaarMap();
    Bazaar createBazaar(Bazaar bazaar);
    Bazaar updateBazaar(Bazaar bazaar);
    void deleteBazaar(UUID villagerUniqueId);
}
