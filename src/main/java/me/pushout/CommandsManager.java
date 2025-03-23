package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsManager implements CommandExecutor {

    private final GameManager gameManager = new GameManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pushout")) {
            if (args.length == 0) {
                sender.sendMessage("§cUtilisation : /pushout <start|stop>");
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cSeuls les joueurs peuvent démarrer une partie.");
                    return true;
                }

                if (GameManager.getInstance().isGameRunning()) {
                    sender.sendMessage("§cUne partie est déjà en cours !");
                    return true;
                }

                GameManager.getInstance().startGame();
                Bukkit.broadcastMessage("§aLa partie PushOut commence !");
                return true;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                if (!GameManager.getInstance().isGameRunning()) {
                    sender.sendMessage("§cAucune partie n'est en cours.");
                    return true;
                }

                GameManager.getInstance().stopGame();
                Bukkit.broadcastMessage("§cLa partie PushOut a été arrêtée !");
                return true;
            }
        }
        return false;
    }
}
