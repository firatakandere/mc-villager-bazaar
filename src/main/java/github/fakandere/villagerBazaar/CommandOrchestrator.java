package github.fakandere.villagerBazaar;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CommandOrchestrator implements ICommandOrchestrator {

    @Inject
    public CommandOrchestrator(final Permission perm) {
        this.perm = perm;
    }

    final private Map<String, BazaarCommand> bazaarCommands = new HashMap<>();
    final private Permission perm;

    public void addCommand(String commandName, CommandExecutor executor, String permission) {
        final BazaarCommand bazaarCommand = new BazaarCommand(commandName, executor, permission);
        bazaarCommands.put(commandName, bazaarCommand);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false; // ignore
        }

        Player player = (Player) commandSender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Invalid usage.");
            return false;
        }

        if (!bazaarCommands.containsKey(args[0])) {
            player.sendMessage(ChatColor.RED  + "Command not found.");
            return false;
        }

        final BazaarCommand bazaarCommand = bazaarCommands.get(args[0]);

        if (!perm.has(commandSender, bazaarCommand.permission)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to run this command.");
            return false;
        }

        return bazaarCommand.commandExecutor.onCommand(commandSender, command, label, args);
    }

    class BazaarCommand {
        String commandName;
        CommandExecutor commandExecutor;
        String permission;
        public BazaarCommand(String commandName, CommandExecutor commandExecutor, String permission) {
            this.commandName = commandName;
            this.commandExecutor = commandExecutor;
            this.permission = permission;
        }
    }
}
