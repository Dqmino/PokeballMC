package codes.domino.pokeballmc.command;

import codes.domino.pokeballmc.item.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveBallCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("giveball")) return true;
        if (!commandSender.hasPermission("pokeballmc.giveball")) {
            commandSender.sendMessage(ChatColor.RED + "You cannot run this command without the permission node 'pokeballmc.giveball'.");
        }
        switch (args.length) {
            case 0:
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "Please specify a player to give the pokeball to, or run this command with your minecraft client.");
                    return true;
                }
                ItemManager.getInstance().giveItem((Player) commandSender);
                commandSender.sendMessage(ChatColor.GREEN + "Sent!");
                break;
            case 1:
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    commandSender.sendMessage(ChatColor.RED + "Invalid player name.");
                    return true;
                }
                ItemManager.getInstance().giveItem(player);
                commandSender.sendMessage(ChatColor.GREEN + "Sent!");
                break;
        }
        return true;
    }
}
