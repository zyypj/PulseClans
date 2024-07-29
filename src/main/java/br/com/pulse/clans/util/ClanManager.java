package br.com.pulse.clans.util;

import br.com.pulse.clans.ClanManagerAPI;
import br.com.pulse.clans.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClanManager implements ClanManagerAPI {
    private final Map<String, Clan> clans;
    private final File file;
    private final FileConfiguration config;
    private final Main plugin;
    private final Map<UUID, Boolean> displayPreferences;

    public ClanManager(Main plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
        this.file = new File(plugin.getDataFolder(), "clans.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.displayPreferences = new HashMap<>();
        loadDisplayPreferences();
        loadClans();
    }

    public void loadClans() {
        if (!config.contains("clans")) {
            return;
        }

        for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
            String tag = config.getString("clans." + clanName + ".tag");
            String color = config.getString("clans." + clanName + ".color");
            UUID leader = UUID.fromString(config.getString("clans." + clanName + ".leader"));
            long creationDate = config.getLong("clans." + clanName + ".creationDate");

            Clan clan = new Clan(clanName, tag, leader, color);
            clan.setCreationDate(creationDate);
            clan.setColor(color);

            config.getStringList("clans." + clanName + ".managers")
                    .stream().map(UUID::fromString).forEach(clan::addManager);

            config.getStringList("clans." + clanName + ".members")
                    .stream().map(UUID::fromString).forEach(clan::addMember);

            config.getStringList("clans." + clanName + ".invites")
                    .stream().map(UUID::fromString).forEach(clan::addInvite);

            String discord = config.getString("clans." + clanName + ".discord");
            clan.setDiscord(discord);

            int gamesWin = config.getInt("clans." + clanName + ".gamesWin");
            int gamesDefeat = config.getInt("clans." + clanName + ".gamesDefeat");
            clan.setGamesWin(gamesWin);
            clan.setGamesDefeat(gamesDefeat);

            if (config.contains("clans." + clanName + ".tournaments")) {
                for (String tournament : config.getConfigurationSection("clans." + clanName + ".tournaments").getKeys(false)) {
                    String place = config.getString("clans." + clanName + ".tournaments." + tournament);
                    clan.addTournamentResult(tournament, place);
                }
            }

            if (config.contains("clans." + clanName + ".availableColors")) {
                ConfigurationSection colorsSection = config.getConfigurationSection("clans." + clanName + ".availableColors");
                for (String colorName : colorsSection.getKeys(false)) {
                    String colorCode = colorsSection.getString(colorName);
                    clan.addColorTag(colorName, colorCode);
                }
            }

            clans.put(clanName, clan);
        }
    }

    public void saveClans() {
        for (String clanName : clans.keySet()) {
            Clan clan = clans.get(clanName);

            config.set("clans." + clanName + ".tag", clan.getTag());
            config.set("clans." + clanName + ".color", clan.getColor());
            config.set("clans." + clanName + ".leader", clan.getLeader().toString());
            config.set("clans." + clanName + ".creationDate", clan.getCreationDate());

            List<String> managers = clan.getManagers().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            config.set("clans." + clanName + ".managers", managers);

            List<String> members = clan.getMembers().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            config.set("clans." + clanName + ".members", members);

            List<String> invites = clan.getInvites().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            config.set("clans." + clanName + ".invites", invites);

            if (clan.getDiscord() != null) {
                config.set("clans." + clanName + ".discord", clan.getDiscord());
            }

            config.set("clans." + clanName + ".gamesWin", clan.getGamesWin());
            config.set("clans." + clanName + ".gamesDefeat", clan.getGamesDefeat());

            Map<String, String> colors = clan.getAvailableColors();
            for (Map.Entry<String, String> entry : colors.entrySet()) {
                config.set("clans." + clanName + ".availableColors." + entry.getKey(), entry.getValue());
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createClan(Player player, String name, String tag, String color) {
        if (getClanByPlayer(player.getUniqueId()) != null) {
            player.sendMessage("§cVocê já está em um clan.");
            return;
        }

        if (clans.containsKey(name)) {
            player.sendMessage("§cJá existe um clan com esse nome.");
            return;
        }

        Clan clan = new Clan(name, tag.toUpperCase(), player.getUniqueId(), color);
        clans.put(name, clan);
        clan.setCreationDate(System.currentTimeMillis());
        clan.setDiscord(null);
        saveClans();
        player.sendMessage("§aClan §7[" + clan.getTag() + "] " + clan.getName() + " §acriado com sucesso!");
    }

    public void deleteClan(Clan clan) {
        String clanName = clan.getName();
        clans.remove(clanName);

        // Remove clan from configuration
        config.set("clans." + clanName, null);

        // Save changes to the configuration file
        saveClans();

        // Notify all members of the clan
        for (UUID memberId : clan.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage("§cSeu clan foi excluido.");
            }
        }

        for (UUID managerId : clan.getManagers()) {
            Player manager = Bukkit.getPlayer(managerId);
            if (manager != null) {
                manager.sendMessage("§cSeu clan foi excluido.");
            }
        }
    }

    public void sendInvite(Player inviter, Player invitedPlayer, Clan clan) {
        UUID invitedUUID = invitedPlayer.getUniqueId();

        if (clan.isMember(invitedUUID)) {
            inviter.sendMessage(ChatColor.RED + invitedPlayer.getName() + " já está em um clan.");
            return;
        }

        if (clan.hasInvite(invitedUUID)) {
            inviter.sendMessage(ChatColor.RED + invitedPlayer.getName() + " já recebeu um convite para este clan.");
            return;
        }

        if (getClanByPlayer(invitedUUID) != null) {
            inviter.sendMessage("§cEsse jogador já está em um clan!");
            return;
        }

        clan.addInvite(invitedUUID);
        saveClans();

        invitedPlayer.sendMessage("§eVocê foi convidado para o clan " + clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + clan.getName() + "§e.");
        TextComponent acceptText = new TextComponent("§a§lCLIQUE AQUI §epara aceitar");
        acceptText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan aceitar " + clan.getTag()));
        acceptText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aAceitar o convite").create()));

        TextComponent denyText = new TextComponent(" §c§lCLIQUE AQUI §epara negar");
        denyText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan negar " + clan.getTag()));
        denyText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cNegar o convite").create()));

        invitedPlayer.spigot().sendMessage(acceptText, denyText);
    }

    public void acceptInvite(Player player, Clan clan) {

        if (hasClan(player)) {
            player.sendMessage("§cVocê já está em um clan!");
            return;
        }

        if (clan.hasInvite(player.getUniqueId())) {
            clan.addMember(player.getUniqueId());
            clan.removeInvite(player.getUniqueId());
            saveClans();

            player.sendMessage("§aVocê entrou no clan §7[" + clan.getTag() + "] " + clan.getName() + "§a.");
            broadcastMessage(clan.getTag(), clan.getColor().replace('&', '§') + "[" +
                    clan.getTag() + "]" + " §7" + player.getName() + " entrou no clan");
        } else {
            player.sendMessage("§cVocê não tem pedidos pendentes desse clan.");
        }
    }

    public void denyInvite(Player player, Clan clan) {
        if (clan.hasInvite(player.getUniqueId())) {
            clan.removeInvite(player.getUniqueId());
            saveClans();

            player.sendMessage("§aVocê negou o convite.");
        } else {
            player.sendMessage("§cVocê não tem pedidos pendentes desse clan.");
        }
    }

    public void broadcastMessage(String clanTag, String message) {
        Clan clan = getClanByTag(clanTag);
        if (clan == null) {
            Bukkit.getLogger().warning("Clan com tag '" + clanTag + "' não encontrado.");
            return;
        }

        Set<UUID> recipients = new HashSet<>(clan.getManagers());
        recipients.add(clan.getLeader());
        recipients.addAll(clan.getMembers());

        for (UUID uuid : recipients) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }

    public void addGamesWin(Clan clan, int number) {
        int gamesWin = clan.getGamesWin();
        int newGamesWin = gamesWin + number;
        clan.setGamesWin(newGamesWin);
        saveClans();
    }

    public void addGamesDefeat(Clan clan, int number) {
        int gamesDefeat = clan.getGamesDefeat();
        int newGamesDefeat = gamesDefeat + number;
        clan.setGamesDefeat(newGamesDefeat);
        saveClans();
    }

    public void setTournamentResult(String clanIdentifier, String tournamentName, String place) {
        Clan clan = getClanByNameOrTag(clanIdentifier);
        if (clan != null) {
            clan.addTournamentResult(tournamentName, place);
            saveClans();
        }
    }

    public String getCreationFormattedDate(Clan clan) {
        long creationDateInMillis = clan.getCreationDate();
        Date creationDate = new Date(creationDateInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(creationDate);
    }

    public Clan getClanByPlayer(UUID playerUUID) {
        for (Clan clan : clans.values()) {
            if (clan.isMember(playerUUID) || clan.isManager(playerUUID) || clan.isLeader(playerUUID)) {
                return clan;
            }
        }
        return null;
    }

    public Clan getClanByName(String name) {
        for (Clan clan : clans.values()) {
            if (clan.getName().equalsIgnoreCase(name)) {
                return clan;
            }
        }
        return null;
    }

    public Set<String> getInvitesToPlayer(UUID playerUUID) {
        Set<String> clanInvites = new HashSet<>();
        for (Clan clan : clans.values()) {
            if (clan.getInvites().contains(playerUUID)) {
                clanInvites.add(clan.getTag());
            }
        }
        return clanInvites;
    }

    public Clan getClanByManagerAndLeader(UUID playerUUID) {
        for (Clan clan : clans.values()) {
            if (clan.getLeader().equals(playerUUID) || clan.getManagers().contains(playerUUID)) {
                return clan;
            }
        }
        return null;
    }

    public Clan getClanByTag(String tag) {
        for (Clan clan : clans.values()) {
            if (clan.getTag().equalsIgnoreCase(tag)) {
                return clan;
            }
        }
        return null;
    }

    public Clan getClanByNameOrTag(String nameOrTag) {
        for (Clan clan : clans.values()) {
            if (clan.getName().equalsIgnoreCase(nameOrTag) || clan.getTag().equalsIgnoreCase(nameOrTag)) {
                return clan;
            }
        }
        return null;
    }

    public Set<Clan> getClansWithPendingInvites(String playerName, Map<String, Clan> clans) {
        Set<Clan> clansWithPendingInvites = new HashSet<>();
        Player playerP = Bukkit.getPlayer(playerName);
        if (playerP == null) {
            return clansWithPendingInvites;
        }

        UUID playerUUID = playerP.getUniqueId();
        for (Clan clan : clans.values()) {
            if (clan.hasInvite(playerUUID)) {
                clansWithPendingInvites.add(clan);
            }
        }

        return clansWithPendingInvites;
    }

    public Set<Player> getOnlineMembers(Clan clan) {
        Set<Player> onlineMembers = new HashSet<>();
        for (UUID memberId : clan.getMembers()) {
            Player player = Bukkit.getPlayer(memberId);
            if (player != null && player.isOnline()) {
                onlineMembers.add(player);
            }
        }

        for (UUID managerId : clan.getManagers()) {
            Player player = Bukkit.getPlayer(managerId);
            if (player != null && player.isOnline()) {
                onlineMembers.add(player);
            }
        }

        UUID playerUUID = clan.getLeader();
        Player player = Bukkit.getPlayer(playerUUID);
        onlineMembers.add(player);

        return onlineMembers;
    }

    public int compareTournamentPlaces(String place1, String place2) {
        return getPlaceRank(place1) - getPlaceRank(place2);
    }

    public int getPlaceRank(String place) {
        return switch (place.toLowerCase()) {
            case "1º lugar" -> 1;
            case "2º lugar" -> 2;
            case "3º lugar" -> 3;
            case "semi final" -> 4;
            case "quartas de final" -> 5;
            case "fase de grupos" -> 6;
            case "expulso" -> 7;
            default -> Integer.MAX_VALUE;
        };
    }

    public String getPlaceColor(String place) {
        return switch (place.toLowerCase()) {
            case "1º lugar" -> "§6";
            case "2º lugar" -> "§7";
            case "3º lugar" -> "§3";
            case "semi final" -> "§5";
            case "quartas de final" -> "§9";
            case "fase de grupos" -> "§8";
            case "expulso" -> "§4§l";
            default -> "§f"; // Default white color for unknown positions
        };
    }

    public Map<UUID, Boolean> getDisplayPreferences() {
        return displayPreferences;
    }

    public boolean hasClan(Player player) {

        Clan clan = getClanByPlayer(player.getUniqueId());

        return clan != null;
    }

    private void loadDisplayPreferences() {
        File file = new File(plugin.getDataFolder(), "displays.yml");
        if (!file.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("clans")) {
            return;
        }
        for (String uuidString : config.getConfigurationSection("clans").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                boolean display = config.getBoolean("clans." + uuidString);
                displayPreferences.put(uuid, display);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveDisplayPreferences() {
        File file = new File(plugin.getDataFolder(), "displays.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<UUID, Boolean> entry : displayPreferences.entrySet()) {
            config.set("clans." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean inSameClan(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return false;
        }

        // Obtenha o UUID do primeiro jogador e o clã dele
        UUID firstPlayerUUID = players.get(0).getUniqueId();
        Clan firstPlayerClan = getClanByPlayer(firstPlayerUUID);
        if (firstPlayerClan == null) {
            return false;
        }

        // Verifique se todos os outros jogadores estão no mesmo clã
        for (Player player : players) {
            UUID playerUUID = player.getUniqueId();
            Clan playerClan = getClanByPlayer(playerUUID);
            if (playerClan == null || !Objects.equals(playerClan, firstPlayerClan)) {
                return false;
            }
        }

        return true;
    }

}
