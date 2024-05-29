package br.com.pulse.clans.util;

import br.com.pulse.clans.Main;
import br.com.pulse.clans.listeners.PlayerJoin;
import br.com.pulse.clans.listeners.RankedEvents;
import com.tomkeuper.bedwars.api.addon.Addon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BedWars2023 extends Addon {

    private final Main plugin;
    private final ClanManager clanManager;

    public BedWars2023(Main plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @Override
    public String getAuthor() {
        return "tadeu";
    }

    @Override
    public Plugin getPlugin() {
        return Main.getPlugins();
    }

    @Override
    public String getVersion() {
        return getPlugin().getDescription().getVersion();
    }

    @Override
    public String getName() {
        return getPlugin().getDescription().getName();
    }

    @Override
    public String getDescription() {
        return getPlugin().getDescription().getDescription();
    }

    @Override
    public void load() {
        loadListeners();
        loadCommands();
    }

    @Override
    public void unload() {
        Bukkit.getPluginManager().disablePlugin(getPlugin());
    }

    public void loadListeners() {
        Bukkit.getConsoleSender().sendMessage("&eLoading Listeners...");
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(plugin, clanManager), plugin);
        Bukkit.getPluginManager().registerEvents(new RankedEvents(plugin, clanManager), plugin);
        Bukkit.getConsoleSender().sendMessage("&aListeners loaded!");
    }

    public void loadCommands() {
        Bukkit.getConsoleSender().sendMessage("&eLoading Commands...");
        Bukkit.getConsoleSender().sendMessage("&aCommands loaded!");
    }
}
