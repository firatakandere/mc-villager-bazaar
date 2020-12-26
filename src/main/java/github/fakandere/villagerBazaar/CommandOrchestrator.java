package github.fakandere.villagerBazaar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandOrchestrator implements CommandExecutor {

    final private Map<String, BazaarCommand> bazaarCommands = new HashMap<>();

    public void addCommand(String commandName, CommandExecutor executor, String permission) {
        final BazaarCommand bazaarCommand = new BazaarCommand(commandName, executor, permission);
        bazaarCommands.put(commandName, bazaarCommand);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false; // ignore
        }

        if (args.length == 0) {
            // @todo return help
            return false;
        }

        if (!bazaarCommands.containsKey(args[0])) {
            // @todo command not found, return help
            return false;
        }

        final BazaarCommand bazaarCommand = bazaarCommands.get(args[0]);

        if (!commandSender.hasPermission(bazaarCommand.permission)) {
            // @todo return no permission
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
