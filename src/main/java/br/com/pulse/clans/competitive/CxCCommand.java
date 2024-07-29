package br.com.pulse.clans.competitive;

import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import br.com.pulse.clans.util.Messages;
import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CxCCommand implements CommandExecutor {

    private final ClanManager clanManager;
    private final CxCManager cxCManager;
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public CxCCommand(ClanManager clanManager, CxCManager cxCManager) {
        this.clanManager = clanManager;
        this.cxCManager = cxCManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length < 1) {
            sender.sendMessage("§c§lClan x Clan Competitive");
            sender.sendMessage("");
            sender.sendMessage("§5/cxc joinQueue §7- Entra na fila do CxC");
            sender.sendMessage("§5/cxc leaveQueue §7- Sair da fila do CxC");
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (args[0].equalsIgnoreCase("joinQueue")) {

            Clan clan = clanManager.getClanByPlayer(playerUUID);

            if (clan == null) {
                player.sendMessage(Messages.IS_NOT_CLAN);
                return true;
            }

            if (!clan.isManager(playerUUID) && !clan.isLeader(playerUUID)) {
                player.sendMessage(Messages.WITHOUT_RANK);
                return true;
            }

            Party party = bedwarsAPI.getPartyUtil();

            if (!party.hasParty(player)) {
                player.sendMessage(Messages.PARTY_NEED);
                return true;
            }

            if (!party.isOwner(player)) {
                player.sendMessage(Messages.PARTY_NO_RANK);
                return true;
            }

            if (party.getMembers(player).size() != 4) {
                player.sendMessage("§cSua party precisa ter exatamente 4 jogadores!");
                return true;
            }

            List<Player> playersParty = party.getMembers(player);

            if (!clanManager.inSameClan(playersParty)) {
                player.sendMessage("§cTodos da sua party devem estar no mesmo clan!");
                return true;
            }

            cxCManager.queueJoin(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("leaveQueue")) {

            cxCManager.leaveQueue(player);
        }

        return true;
    }
}
