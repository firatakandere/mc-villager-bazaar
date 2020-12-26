package github.fakandere.villagerBazaar;

import org.bukkit.command.CommandExecutor;

public interface ICommandOrchestrator extends CommandExecutor {
    void addCommand(String commandName, CommandExecutor executor, String permission);
}
