package me.pushout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can open the configuration menu.");
            return true;
        }
        Player player = (Player) sender;
        player.openInventory(new ConfigMenu().getInventory());
        return true;
    }
}
