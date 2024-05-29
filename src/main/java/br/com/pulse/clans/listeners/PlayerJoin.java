package br.com.pulse.clans.listeners;

import br.com.pulse.clans.Main;
import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.Set;

public class PlayerJoin implements Listener {

    private final Main plugin;
    private final ClanManager clanManager;

    public PlayerJoin(Main plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Map<String, Clan> allClans = Clan.getClans();
        Set<Clan> clansWithPendingInvites = clanManager.getClansWithPendingInvites(player.getName(), allClans);
        if (!clansWithPendingInvites.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage("§a§lVocê tem pedidos de clans pendentes!");
                player.sendMessage("§aUse §l/clan pedidos §apara ver todos.");
            },100L);
        }
    }
}
