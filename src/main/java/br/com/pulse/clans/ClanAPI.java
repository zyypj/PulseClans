package br.com.pulse.clans;

import java.util.Set;
import java.util.UUID;

public interface ClanAPI {

    String getName();

    String getTag();

    UUID getLeader();

    Set<UUID> getManagers();

    Set<UUID> getMembers();

    long getCreationDate();

    void addManager(UUID player);

    void addMember(UUID player);

    void removeMember(UUID player);

    void removeMemberForPromote(UUID player);

    boolean isLeader(UUID player);

    boolean isManager(UUID player);

    boolean isMember(UUID player);

    Set<UUID> getInvites();

    void addInvite(UUID playerUUID);

    void removeInvite(UUID playerUUID);

    boolean hasInvite(UUID playerUUID);

    String getColor();

    String getDiscord();

    int getGamesWin();

    int getGamesDefeat();

    void setDiscord(String discord);

    void setCreationDate(long creationDate);

    void setColor(String color);

    void setLeader(UUID newLeader);

    void setTag(String tag);

    void setName(String name);

    void setGamesWin(int gamesWin);

    void setGamesDefeat(int gamesDefeat);

    void removeManager(UUID managerUUID);

    static ClanAPI getClan(String name) {
        return null;
    }

    static void addClan(String name, ClanAPI clan) {
    }

    static void removeClan(String name) {
    }
}
