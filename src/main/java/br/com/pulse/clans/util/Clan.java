package br.com.pulse.clans.util;

import br.com.pulse.clans.ClanAPI;

import java.util.*;

public class Clan implements ClanAPI {
    private String name;
    private String tag;
    private UUID leader;
    private final Set<UUID> managers;
    private final Set<UUID> members;
    private final Set<UUID> invites;
    private int gamesWin;
    private int gamesDefeat;
    private String discord;
    private long creationDate;
    private static final Map<String, Clan> allClans = new HashMap<>();
    private final Map<String, String> tournamentResults;
    private final Map<String, String> availableColors;
    private String color;

    public Clan(String name, String tag, UUID leader, String color) {
        this.name = name;
        this.tag = tag;
        this.leader = leader;
        this.color = color;
        this.managers = new HashSet<>();
        this.members = new HashSet<>();
        this.invites = new HashSet<>();
        this.discord = "";
        this.creationDate = System.currentTimeMillis();
        this.tournamentResults = new HashMap<>();
        this.availableColors = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTag() {
        return tag.toUpperCase();
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public Set<UUID> getManagers() {
        return managers;
    }

    @Override
    public Set<UUID> getMembers() {
        return members;
    }

    @Override
    public long getCreationDate() {
        return creationDate;
    }

    @Override
    public void addManager(UUID player) {
        managers.add(player);
    }

    @Override
    public void addMember(UUID player) {
        members.add(player);
    }

    @Override
    public void removeMember(UUID player) {
        members.remove(player);
        managers.remove(player);
    }

    @Override
    public void removeMemberForPromote(UUID player) {
        members.remove(player);
    }

    @Override
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    @Override
    public boolean isManager(UUID player) {
        return managers.contains(player);
    }

    @Override
    public boolean isMember(UUID player) {
        return members.contains(player);
    }

    @Override
    public Set<UUID> getInvites() {
        return invites;
    }

    @Override
    public void addInvite(UUID playerUUID) {
        invites.add(playerUUID);
    }

    @Override
    public void removeInvite(UUID playerUUID) {
        invites.remove(playerUUID);
    }

    @Override
    public boolean hasInvite(UUID playerUUID) {
        return invites.contains(playerUUID);
    }

    @Override
    public String getDiscord() {
        if (discord == null) {
            return "§cNão informado";
        }
        return discord;
    }

    @Override
    public int getGamesWin() {
        return gamesWin;
    }

    @Override
    public int getGamesDefeat() {
        return gamesDefeat;
    }

    public static Map<String, Clan> getClans() {
        return allClans;
    }

    @Override
    public void setDiscord(String discord) {
        this.discord = discord;
    }

    @Override
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public void setLeader(UUID newLeader) {
        this.leader = newLeader;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setGamesWin(int gamesWin) {
        this.gamesWin = gamesWin;
    }

    @Override
    public void setGamesDefeat(int gamesDefeat) {
        this.gamesDefeat = gamesDefeat;
    }

    @Override
    public void removeManager(UUID managerUUID) {
        managers.remove(managerUUID);
    }

    public static ClanAPI getClan(String name) {
        return allClans.get(name);
    }

    public static void addClan(String name, ClanAPI clan) {
        if (clan instanceof Clan) {
            allClans.put(name, (Clan) clan);
        }
    }

    public static void removeClan(String name) {
        allClans.remove(name);
    }

    public void addTournamentResult(String tournamentName, String place) {
        this.tournamentResults.put(tournamentName, place);
    }

    public Map<String, String> getTournamentResults() {
        return this.tournamentResults;
    }

    public void addColorTag(String name, String color) {
        this.availableColors.put(name, color);
    }

    public Map<String, String> getAvailableColors() {
        return this.availableColors;
    }

    public boolean hasColorTag(String color) {
        return this.availableColors.containsValue(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}