package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PushOut extends JavaPlugin {

    private static PushOut instance;

    @Override
    public void onEnable() {
        instance = this;

        // Enregistrement des événements
        Bukkit.getPluginManager().registerEvents(new EventsListener(), this);

        // Enregistrement des commandes
        getCommand("pushout").setExecutor(new CommandsManager());

        getLogger().info("PushOut a été activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("PushOut a été désactivé.");
    }

    public static PushOut getInstance() {
        return instance;
    }
}
