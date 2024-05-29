package br.com.pulse.clans.listeners;

import br.com.pulse.clans.Main;
import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import com.leafplugins.punish.platform.bukkit.api.events.PunishEvent;
import com.leafplugins.punish.platform.commons.api.LeafPunishAPI;
import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class RankedEvents implements Listener {

    private final Main plugin;
    private final ClanManager clanManager;
    LeafPunishAPI leafPunishAPI = LeafPunishAPI.getApi();
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public RankedEvents(Main plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void GameWinRanked(GameEndEvent e) {
        String group = e.getArena().getGroup();
        ITeam winnerTeam = e.getTeamWinner();
        List<UUID> loserUUIDs = e.getLosers();

        if (group.equalsIgnoreCase("Ranked4v4")) {
            // Verificar se todos os vencedores estão no mesmo clã
            Clan winningClan = null;
            boolean allWinnersInSameClan = true;
            for (Player winner : winnerTeam.getMembers()) {
                Clan playerClan = clanManager.getClanByPlayer(winner.getUniqueId());
                if (winningClan == null) {
                    winningClan = playerClan;
                } else if (playerClan == null || !playerClan.equals(winningClan)) {
                    allWinnersInSameClan = false;
                    break;
                }
            }

            // Verificar se todos os perdedores estão no mesmo clã
            Clan losingClan = null;
            boolean allLosersInSameClan = true;
            for (UUID loserUUID : loserUUIDs) {
                Player loser = Bukkit.getPlayer(loserUUID);
                if (loser != null) {
                    Clan playerClan = clanManager.getClanByPlayer(loser.getUniqueId());
                    if (losingClan == null) {
                        losingClan = playerClan;
                    } else if (playerClan == null || !playerClan.equals(losingClan)) {
                        allLosersInSameClan = false;
                        break;
                    }
                }
            }

            if ((allLosersInSameClan && losingClan != null) && (allWinnersInSameClan && winningClan != null) && (losingClan != winningClan)) {

                clanManager.addGamesWin(winningClan, 1);
                for (Player winner : winnerTeam.getMembers()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        winner.sendMessage("");
                        winner.sendMessage("§aPartida contada como CxC!");
                        winner.sendMessage("§aVítoria contada para o clan.");
                        winner.sendMessage("");
                    }, 10L);
                }

                clanManager.addGamesDefeat(losingClan, 1);
                for (UUID loserUUID : loserUUIDs) {
                    Player loser = Bukkit.getPlayer(loserUUID);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        loser.sendMessage("");
                        loser.sendMessage("§aPartida contada como CxC!");
                        loser.sendMessage("§cDerrota contada para o clan.");
                        loser.sendMessage("");
                    }, 10L);
                }
            }
        }
    }

    @EventHandler
    public void onBan(PunishEvent e) {
        UUID userUUID = e.getPunishment().getPlayer(); //getPlayer é pra pegar o alvo, não o Bukkit.Player
        Player player = Bukkit.getPlayer(userUUID);

        if (player == null || !player.isOnline()) return;

        if (e.getPunishment().isBan()) {
            if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                IArena arena = bedwarsAPI.getArenaUtil().getArenaByPlayer(player);
                List<Player> players = arena.getPlayers();
                for (Player jogadores : players) {

                    jogadores.sendMessage("");
                    jogadores.sendMessage("§c§lUM JOGADOR NESSA SALA FOI BANIDO!");
                    jogadores.sendMessage("§c§lCANCELANDO PARTIDA");
                    jogadores.sendMessage("");

                    Bukkit.getScheduler().runTaskLater(plugin, () -> arena.removePlayer(jogadores, false), 30L);
                }
            }
        }
    }
}