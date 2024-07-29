package br.com.pulse.clans.listeners;

import br.com.pulse.clans.Main;
import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import com.github.syncwrld.prankedbw.bw4sbot.api.Ranked4SApi;
import com.github.syncwrld.prankedbw.bw4sbot.manager.GameManager;
import com.github.syncwrld.prankedbw.bw4sbot.model.game.Match;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RankedEvents implements Listener {

    private final Main plugin;
    private final ClanManager clanManager;
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
    Ranked4SApi botAPI = Bukkit.getServicesManager().getRegistration(Ranked4SApi.class).getProvider();

    public RankedEvents(Main plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void GameWinRanked(GameEndEvent e) {
        String group = e.getArena().getGroup();
        ITeam winnerTeam = e.getTeamWinner();
        List<UUID> loserUUIDs = e.getLosers();

        if (group.equalsIgnoreCase("CxC4v4")) {
            // Verificar se todos os vencedores estão no mesmo clã
            List<Player> winners = winnerTeam.getMembers();
            boolean allWinnersInSameClan = clanManager.inSameClan(winners);

            // Verificar se todos os perdedores estão no mesmo clã
            List<Player> losers = new ArrayList<>();
            for (UUID loserUUID : loserUUIDs) {
                Player loser = Bukkit.getPlayer(loserUUID);
                if (loser != null) {
                    losers.add(loser);
                }
            }
            boolean allLosersInSameClan = clanManager.inSameClan(losers);

            // Obter o clã vencedor e perdedor
            Clan winningClan = null;
            Clan losingClan = null;
            if (allWinnersInSameClan) {
                winningClan = clanManager.getClanByPlayer(winners.get(0).getUniqueId());
            }
            if (allLosersInSameClan) {
                losingClan = clanManager.getClanByPlayer(losers.get(0).getUniqueId());
            }

            if ((allLosersInSameClan && losingClan != null) && (allWinnersInSameClan && winningClan != null) && (losingClan != winningClan)) {

                clanManager.addGamesWin(winningClan, 1);
                for (Player winner : winners) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        winner.sendMessage("");
                        winner.sendMessage("§aPartida contada como CxC!");
                        winner.sendMessage("§aVitória contada para o clan.");
                        winner.sendMessage("");
                    }, 10L);
                }

                clanManager.addGamesDefeat(losingClan, 1);
                for (Player loser : losers) {
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
                if (arena.getGroup().equalsIgnoreCase("Ranked1v1") ||
                        arena.getGroup().equalsIgnoreCase("Ranked2v2CM") ||
                        arena.getGroup().equalsIgnoreCase("RankedSolo") ||
                        arena.getGroup().equalsIgnoreCase("CxC4v4")) {
                    for (Player jogadores : players) {

                        jogadores.sendMessage("");
                        jogadores.sendMessage("§c§lUM JOGADOR NESSA SALA FOI BANIDO!");
                        jogadores.sendMessage("§c§lCANCELANDO PARTIDA...");
                        jogadores.sendMessage("");

                        Bukkit.getScheduler().runTaskLater(plugin, () -> arena.removePlayer(jogadores, false), 30L);
                    }
                }
                if (arena.getGroup().equalsIgnoreCase("Ranked4s")) {
                    Match match = botAPI.findMatch(player);

                    if (match == null) {
                        return;
                    }

                    for (Player jogadores : players) {

                        jogadores.sendMessage("");
                        jogadores.sendMessage("§c§lUM JOGADOR NESSA SALA FOI BANIDO!");
                        jogadores.sendMessage("§c§lCANCELANDO PARTIDA...");
                        jogadores.sendMessage("");

                        Bukkit.getScheduler().runTaskLater(plugin, () -> arena.removePlayer(jogadores, false), 30L);

                    }

                }
            }
        }
    }
}