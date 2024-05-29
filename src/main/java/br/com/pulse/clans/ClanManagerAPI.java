package br.com.pulse.clans;

import br.com.pulse.clans.util.Clan;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ClanManagerAPI {

    void createClan(Player player, String name, String tag);

    void deleteClan(Clan clan);

    void sendInvite(Player inviter, Player invitedPlayer, Clan clan);

    void acceptInvite(Player player, Clan clan);

    void denyInvite(Player player, Clan clan);

    void broadcastMessage(String clanTag, String message);

    void addGamesWin(Clan clan, int number);

    void addGamesDefeat(Clan clan, int number);

    String getCreationFormattedDate(Clan clan);

    Clan getClanByPlayer(UUID playerUUID);

    Clan getClanByName(String name);

    Set<String> getInvitesToPlayer(UUID playerUUID);

    Clan getClanByManagerAndLeader(UUID playerUUID);

    Clan getClanByTag(String tag);

    Clan getClanByNameOrTag(String nameOrTag);

    Set<Clan> getClansWithPendingInvites(String playerName, Map<String, Clan> clans);

    Set<Player> getOnlineMembers(Clan clan);

}
