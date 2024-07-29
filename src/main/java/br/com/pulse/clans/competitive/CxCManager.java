package br.com.pulse.clans.competitive;

import br.com.pulse.clans.Main;
import com.tomkeuper.bedwars.api.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class CxCManager {

    private final Main plugin;
    private final Map<String, List<Player>> gameQueue;
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public CxCManager(Main plugin) {
        this.plugin = plugin;
        gameQueue = new HashMap<>();
    }

    public void queueJoin(Player partyLeader) {
        String gameType = "CxC4v4";
        List<Player> queue = gameQueue.computeIfAbsent(gameType, k -> new ArrayList<>());
        queue.add(partyLeader);
        partyLeader.sendMessage("");
        partyLeader.sendMessage("§7Você entrou numa fila de");
        partyLeader.sendMessage("§5Clan x Clan Competitive");
        partyLeader.sendMessage("");
        checkQueue();
    }

    public void leaveQueue(Player player) {
        for (List<Player> queue : gameQueue.values()) {
            if (queue.contains(player)) {
                queue.remove(player);
                player.sendMessage("");
                player.sendMessage("§7Você saiu da fila.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                return;
            }
        }
        player.sendMessage("§cVocê não está na fila.");
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
    }

    private void checkQueue() {
        String gameType = "CxC4v4";
        List<Player> queue = gameQueue.get(gameType);
        if (queue != null && queue.size() >= 2) {
            List<Player> players = new ArrayList<>(queue.subList(0, 2));
            queue.removeAll(players);
            startGame(players, gameType);
        }
    }

    private void startGame(List<Player> players, String gameType) {
        for (Player player : players) {
            player.sendMessage("§7Partida Encontrada: §5" + gameType);
            player.sendMessage("§7Conectando...");
            player.sendMessage("");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                bedwarsAPI.getArenaUtil().joinRandomFromGroup(player, gameType);
                if (bedwarsAPI.getArenaUtil().isPlaying(player)) {
                    player.sendMessage("");
                    player.sendMessage("§7Você entrou em uma partida ranqueada!");
                    player.sendMessage("§7Modo: §5" + gameType);
                    player.sendMessage("");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    Bukkit.getScheduler().runTaskLater(plugin, () ->
                            bedwarsAPI.getArenaUtil().getArenaByPlayer(player).getStartingTask().setCountdown(5), 20L);
                } else {
                    player.sendMessage("");
                    player.sendMessage("§cNão foi possível entrar na partida!");
                    player.sendMessage("§cRelogue e tente novamente.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                }
            }, 40L);
        }
    }
}
