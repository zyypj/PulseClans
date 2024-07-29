package br.com.pulse.clans.competitive;

import br.com.pulse.clans.Main;
import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.GameState;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.events.player.PlayerLeaveArenaEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class GameListener implements Listener {

    private final Main plugin;
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public GameListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveArenaEvent e) {
        IArena arena = e.getArena();
        String group = arena.getGroup();

        if (group.equalsIgnoreCase("CxC4v4")) {
            List<Player> players = arena.getPlayers();

            if (bedwarsAPI.getArenaUtil().isPlaying(e.getPlayer())) {
                for (Player player : players) {
                    player.sendMessage("");
                    player.sendMessage("§cAlgum jogador saiu da partida...");
                    player.sendMessage("§cPartida cancelada!");
                    player.sendMessage("");

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        arena.removePlayer(player, true);
                        arena.setStatus(GameState.restarting);
                    }, 30L);
                }
                return;
            }

            for (Player player : players) {
                player.sendMessage("");
                player.sendMessage("§cAlgum jogador saiu da partida...");
                player.sendMessage("§cPartida cancelada!");
                player.sendMessage("");

                Bukkit.getScheduler().runTaskLater(plugin, () -> player.kickPlayer("§cPartida cancelada. Entre novamente!"), 30L);
            }
        }
    }
}
