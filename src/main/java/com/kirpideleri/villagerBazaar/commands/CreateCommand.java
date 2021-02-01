package com.kirpideleri.villagerBazaar.commands;

import com.kirpideleri.villagerBazaar.utils.IBazaarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.rmi.UnexpectedException;
import java.util.UUID;

public class CreateCommand extends AbstractCreateCommand {
    IBazaarManager bazaarManager;

    @Inject
    public CreateCommand(IBazaarManager bazaarManager) {
        this.bazaarManager = bazaarManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player p = (Player) commandSender;
        UUID villagerID = this.createVillager(p);
        try {
            bazaarManager.createPlayerBazaar(villagerID, p.getUniqueId());
        } catch (UnexpectedException e) {
            p.sendMessage("An unexpected error occurred during bazaar creation, please contact with server admin.");
        }
        p.sendMessage(villagerID.toString());
        return true;
    }
}
