package br.com.pulse.clans;

import br.com.pulse.clans.commands.ClanAdminCommand;
import br.com.pulse.clans.commands.ClanChatCommand;
import br.com.pulse.clans.commands.ClanCommand;
import br.com.pulse.clans.commands.ClanTagDisplay;
import br.com.pulse.clans.competitive.CxCCommand;
import br.com.pulse.clans.competitive.CxCManager;
import br.com.pulse.clans.competitive.GameListener;
import br.com.pulse.clans.listeners.RankedEvents;
import br.com.pulse.clans.util.ClanManager;
import br.com.pulse.clans.util.placeholder.PlaceholderSupport;
import org.bukkit.Bukkit;
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
        CxCManager cxCManager = new CxCManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getScheduler().runTaskLater(this, () ->
                    getLogger().info("Hook para suporte ao PlaceholderAPI!"), 20L);
            new PlaceholderSupport(clanManager, getDataFolder(), clanManager.getDisplayPreferences()).register();
        }

        clanManager.loadClans();

        getCommand("clan").setExecutor(new ClanCommand(clanManager));
        getCommand("cc").setExecutor(new ClanChatCommand(clanManager));
        getCommand("clandisplaytag").setExecutor(new ClanTagDisplay(this, clanManager));
        getCommand("clanadmin").setExecutor(new ClanAdminCommand(clanManager));
        getCommand("cxc").setExecutor(new CxCCommand(clanManager, cxCManager));

        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("LeafPlugins") || Bukkit.getPluginManager().isPluginEnabled("LeafPunish")) {
                Bukkit.getScheduler().runTaskLater(this, () ->
                        getLogger().info("Hook para suporte ao LeafPlugins!"), 20L);
                Bukkit.getPluginManager().registerEvents(new RankedEvents(this, clanManager), this);
            }
        }, 20L);

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