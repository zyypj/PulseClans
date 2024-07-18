package br.com.pulse.clans.commands;

import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanChatCommand implements CommandExecutor {

    private final ClanManager clanManager;

    public ClanChatCommand(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
        if (clan == null) {
            player.sendMessage("§cVocê não está em nenhum clan.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUse: /cc <mensagem>");
            return true;
        }

        String message = String.join(" ", args);
        String formattedMessage = clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] §7" +
        PlaceholderAPI.setPlaceholders(player, "%leaftags_tag_prefix%")
                + player.getName() + " » §f" + message;

        if (clanManager.getOnlineMembers(clan).isEmpty()) {
            player.sendMessage(formattedMessage);
            return true;
        }

        for (Player member : clanManager.getOnlineMembers(clan)) {
            member.sendMessage(formattedMessage);
        }

        return true;
    }
}