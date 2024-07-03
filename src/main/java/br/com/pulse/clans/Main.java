package br.com.pulse.clans;

import br.com.pulse.clans.commands.ClanChatCommand;
import br.com.pulse.clans.commands.ClanCommand;
import br.com.pulse.clans.listeners.RankedEvents;
import br.com.pulse.clans.util.ClanManager;
import br.com.pulse.clans.util.placeholder.PlaceholderSupport;
import com.tomkeuper.bedwars.api.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ClanManager clanManager;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("BedWars2023") == null) {
            getLogger().severe("BedWars2023 não foi encontrado. Desabilitando...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        clanManager = new ClanManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getScheduler().runTaskLater(this, () ->
                    getLogger().info("Hook para suporte ao PlaceholderAPI!"), 20L);
            new PlaceholderSupport(clanManager).register();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("LeafPlugins") || Bukkit.getPluginManager().isPluginEnabled("LeafPunish")) {
            Bukkit.getScheduler().runTaskLater(this, () ->
                    getLogger().info("Hook para suporte ao LeafPlugins!"), 20L);
            Bukkit.getPluginManager().registerEvents(new RankedEvents(this, clanManager), this);
        }

        clanManager.loadClans();

        getCommand("clan").setExecutor(new ClanCommand(clanManager));
        getCommand("cc").setExecutor(new ClanChatCommand(clanManager));

        Bukkit.getScheduler().runTaskLater(this, () ->
                getLogger().info("&aPulseClans habilitado!"), 30L);

    }

    @Override
    public void onDisable() {
        clanManager.saveClans();
    }

    public static Main getPlugins() {
        return getPlugin(Main.class);
    }
}