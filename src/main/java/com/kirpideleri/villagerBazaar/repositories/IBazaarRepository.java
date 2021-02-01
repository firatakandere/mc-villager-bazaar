package com.kirpideleri.villagerBazaar.repositories;

import com.kirpideleri.villagerBazaar.models.Bazaar;
import com.kirpideleri.villagerBazaar.exceptions.NotFoundException;

import java.rmi.UnexpectedException;
import java.util.Map;
import java.util.UUID;

public interface IBazaarRepository {
    Map<UUID, Bazaar> getBazaarMap();
    void createBazaar(Bazaar bazaar) throws UnexpectedException;
    void updateBazaar(Bazaar bazaar) throws UnexpectedException, NotFoundException;
    boolean deleteBazaar(UUID villagerUniqueId) throws NotFoundException;
}
