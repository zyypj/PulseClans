package br.com.pulse.clans.commands;

import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;

import com.tomkeuper.bedwars.api.BedWars;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class ClanCommand implements CommandExecutor {

    private final ClanManager clanManager;
    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    public ClanCommand(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage("§5§lPulseMC §7Clans");
            player.sendMessage("");
            player.sendMessage("§5/clan info §8§o<nome/tag> §7- Ver informações de algum clan");
            player.sendMessage("§5/clan membros §8§o<nome/tag> §7- Ver membros de algum clan"); //
            player.sendMessage("§5/clan criar §8§o<nome/tag> §7- Criar um novo clan"); //
            player.sendMessage("§5/clan excluir §7- Deletar seu clan"); //
            player.sendMessage("§5/clan convidar §8§o<jogador> §7- Convidar um jogador ao seu clan"); //
            player.sendMessage("§5/clan expulsar §8§o<jogador> §7- Expulsar um jogador do seu clan"); //
            player.sendMessage("§5/clan convites §7- Ver convites de clans pendentes"); //
            player.sendMessage("§5/clan aceitar §8§o<nome/tag> §7- Aceitar o convite pendente de algum clan"); //
            player.sendMessage("§5/clan rejeitar §8§o<nome/tag> §7- Rejeitar o convite pendente de algum clan"); //
            player.sendMessage("§5/clan sair §7- Sair do seu atual clan"); //
            player.sendMessage("§5/clan transferir §8§o<jogador> §7- Transferir a liderança do clan para outro jogador"); //
            player.sendMessage("§5/clan promover §8§o<jogador> §7- Promover um jogador à gerência"); //
            player.sendMessage("§5/clan rebaixar §8§o<jogador> §7- Tornar um gerente membro"); //
            player.sendMessage("§5/clan trocar §8§o<nome/tag> <novoNome/novaTag> §7- Trocar informações do clan"); //
            player.sendMessage("§5/cc §8§o<mensagem> §7- Mande uma mensagem para o clan");
            player.sendMessage("§5/clan stats §8§o<nome/tag> §7- Veja as estátisticas ranqueadas de um clan");
            if (player.hasPermission("clan.colortag")) {
                player.sendMessage("§5/clan colortag §8§o<nome> <cor> §7- Mude a cor da tag de algum Clan"); //
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("membros") || args[0].equalsIgnoreCase("members")) {

            if (args.length == 1) {
                Clan clan = clanManager.getClanByPlayer(player.getUniqueId());

                if (clan == null) {
                    player.sendMessage("§cUse /clan " + args[0].toLowerCase() + " <nome/tag>");
                    return true;
                }

                player.sendMessage("§7Membros do Clan §l" + clan.getName());
                player.sendMessage("");
                Player leaderP = Bukkit.getPlayer(clan.getLeader());
                player.sendMessage(PlaceholderAPI.setPlaceholders(leaderP, "%leaftags_tag_prefix%")
                        + leaderP.getName() + " - §4Líder");

                for (UUID manager : clan.getManagers()) {
                    Player managerP = Bukkit.getPlayer(manager);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(managerP, "%leaftags_tag_prefix%")
                            + managerP.getName() + " - §cGerente");
                }

                for (UUID member : clan.getMembers()) {
                    Player memberP = Bukkit.getPlayer(member);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(memberP, "%leaftags_tag_prefix%")
                            + memberP.getName() + " - §7Membro");
                }

                return true;
            }

            if (args.length == 2) {
                Clan clan = clanManager.getClanByNameOrTag(args[1]);

                if (clan == null) {
                    player.sendMessage("§cO clan especificado não foi encontrado.");
                    return true;
                }

                player.sendMessage("§7Membros do Clan §l" + clan.getName());
                player.sendMessage("");
                Player leaderP = Bukkit.getPlayer(clan.getLeader());
                player.sendMessage(PlaceholderAPI.setPlaceholders(leaderP, "%leaftags_tag_prefix%")
                        + leaderP.getName() + " - §6Líder");

                for (UUID manager : clan.getManagers()) {
                    Player managerP = Bukkit.getPlayer(manager);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(managerP, "%leaftags_tag_prefix%")
                            + managerP.getName() + " - §cGerente");
                }

                for (UUID member : clan.getMembers()) {
                    Player memberP = Bukkit.getPlayer(member);
                    player.sendMessage(PlaceholderAPI.setPlaceholders(memberP, "%leaftags_tag_prefix%")
                            + memberP.getName() + " - §7Membro");
                }

                return true;
            }

            player.sendMessage("§cUse /clan " + args[0].toLowerCase() + " <nome/tag>");
            return true;
        }

        //clan criar <nome> <tag>
        if (args[0].equalsIgnoreCase("criar") || args[0].equalsIgnoreCase("create")) {

            if (args.length != 3) {
                player.sendMessage("§cUse: /clan " + args[0] + " <nome> <tag>");
                return true;
            }

            UUID playerUUID = player.getUniqueId();

            Clan clan = clanManager.getClanByPlayer(playerUUID);

            if (clan != null) {
                player.sendMessage("§cVocê já está em um clan!");
                return true;
            }

            String clanName = args[1];
            String clanTag = args[2];

            if (!(player.hasPermission("bw.vip") && bedwarsAPI.getStatsUtil().getPlayerWins(player.getUniqueId()) < 100)) {
                player.sendMessage("§cPara criar um clan, você precisa ter mais de");
                player.sendMessage("§c§l100WINS §cno Bed Wars ou comprar §e§lVIP§c!");
                return true;
            }

            if (!(clanName.length() >= 3 && clanName.length() <= 22)) {
                player.sendMessage("§cO nome do clan deve ter entre 3 e 22 caracteres.");
                return true;
            }

            if (!(clanTag.length() >= 3 && clanTag.length() <= 10)) {
                player.sendMessage("§cA tag do clan deve ter entre 3 e 10 caracteres.");
                return true;
            }

            if (!clanName.matches("[a-zA-Z1-9]+")) {
                player.sendMessage("§cO nome do clan só pode conter letras e números.");
                return true;
            }

            if (!clanTag.matches("[a-zA-Z1-9]+")) {
                player.sendMessage("§cA tag do clan só pode conter letras e números.");
                return true;
            }

            clanManager.createClan(player, clanName, clanTag);
            return true;
        }

        //clan excluir
        if (args[0].equalsIgnoreCase("excluir") || args[0].equalsIgnoreCase("delete")) {

            if (args.length > 1) {
                player.sendMessage("§cUse /clan " + args[0] + ".");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());

            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId())) {
                player.sendMessage("§cVocê não é líder de nenhum clan.");
                return true;
            }

            clanManager.deleteClan(clan);
            player.sendMessage("§aClan excluido com sucesso.");
            return true;
        }

        //clan convidar <jogador>
        if (args[0].equalsIgnoreCase("convidar") || args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <jogador>");
                return true;
            }

            String invitedPlayerName = args[1];
            Player invitedPlayer = Bukkit.getPlayer(invitedPlayerName);

            if (invitedPlayer == null || !invitedPlayer.isOnline()) {
                player.sendMessage("§cJogador não encontrado.");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId()) && !clan.isManager(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode fazer isso.");
                return true;
            }

            clanManager.sendInvite(player, invitedPlayer, clan);
            return true;
        }

        //clan aceitar <clan>
        if (args[0].equalsIgnoreCase("aceitar") || args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <clan>");
                return true;
            }

            Clan clan = clanManager.getClanByName(args[1]);
            if (clan == null) {
                player.sendMessage("§cClan não encontrado.");
                return true;
            }

            clanManager.acceptInvite(player, clan);
            clanManager.broadcastMessage(clan.getTag(), clan.getColor().replace('&', '§') + "[" +
                    clan.getTag() + "]" + " §7" + player.getName() + " entrou no clan");
            return true;
        }

        //clan negar <clan>
        if (args[0].equalsIgnoreCase("negar") || args[0].equalsIgnoreCase("deny")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <clan>");
                return true;
            }

            Clan clan = clanManager.getClanByName(args[1]);
            if (clan == null) {
                player.sendMessage("§cClan não encontrado.");
                return true;
            }

            clanManager.denyInvite(player, clan);
            return true;
        }

        if (args[0].equalsIgnoreCase("pedidos") || args[0].equalsIgnoreCase("requests")) {
            if (args.length != 1) {
                player.sendMessage("§cUse: /clan " + args[0]);
                return true;
            }

            if (clanManager.getClanByPlayer(player.getUniqueId()) != null) {
                player.sendMessage("§cVocê já está em um clan.");
                return true;
            }

            Set<String> clanInvites = clanManager.getInvitesToPlayer(player.getUniqueId());
            if (!clanInvites.isEmpty()) {
                player.sendMessage("§7Convites de Clans:");
                for (String clanTag : clanInvites) {
                    player.sendMessage("§7- " + clanTag);
                }
            } else {
                player.sendMessage("§cVocê não tem nenhum pedido pendente.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("colortag")) {
            if (!player.hasPermission("clan.colortag")) {
                player.sendMessage("§cComando não encontrado ou você não tem permissão.");
                return true;
            }

            if (args.length != 3) {
                player.sendMessage("§cUse: /clan " + args[0] +" <nome/tag> <cor>");
                return true;
            }

            String clanName = args[1];
            String color = args[2];

            // Verifica se a cor é válida (ex: &a, &b, etc.)
            if (!color.matches("&[0-9a-fA-F]")) {
                player.sendMessage("§cCor inválida. Use uma cor válida, como &a, &b, etc.");
                return true;
            }

            Clan clan = clanManager.getClanByName(clanName);
            if (clan == null) {
                player.sendMessage("§cClan não encontrado.");
                return true;
            }

            // Altera a cor da tag do clan
            clan.setColor(color);
            clanManager.saveClans();

            // Envia mensagem de confirmação
            clanManager.broadcastMessage(clan.getTag(),clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " +
                    "§7A cor da tag do clan foi alterada para " + color.replace('&', '§') + "[" + clan.getTag() + "] " + clanName);

            player.sendMessage("§aCor alterada para: " + clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + clanName + "§a.");

            return true;
        }

        //clan expulsar <jogador>
        if (args[0].equalsIgnoreCase("expulsar") || args[0].equalsIgnoreCase("kick")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <jogador>");
                return true;
            }

            String kickedPlayerName = args[1];
            Player kickedPlayer = Bukkit.getPlayer(kickedPlayerName);
            UUID kickedPlayerUUID = kickedPlayer.getUniqueId();

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId()) && !clan.isManager(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode fazer isso.");
                return true;
            }

            if (!clan.isMember(kickedPlayerUUID)) {
                player.sendMessage("§c" + kickedPlayerName + " não está no seu clan.");
                return true;
            }

            if (clan.isManager(kickedPlayerUUID) && clan.isManager(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode fazer isso.");
                return true;
            }

            clan.removeMember(kickedPlayerUUID);
            clanManager.saveClans();

            if (kickedPlayer.isOnline()) {
                kickedPlayer.sendMessage("§cVocê foi expulso do clan.");
            }

            clanManager.broadcastMessage(clan.getTag(),clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + "§7" + kickedPlayerName + " foi expulso do clan por " + player.getName() + ".");
            return true;
        }

        //clan sair
        if (args[0].equalsIgnoreCase("sair") || args[0].equalsIgnoreCase("leave")) {
            if (args.length != 1) {
                player.sendMessage("§cUse: /clan " + args[0]);
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (clan.isLeader(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode sair do clan sendo o líder \n §cUse /clan excluir para excluir o clan.");
                return true;
            }

            clan.removeMember(player.getUniqueId());
            clanManager.saveClans();

            player.sendMessage("§aVocê saiu do clan.");
            clanManager.broadcastMessage(clan.getTag(), clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + "§c" + player.getName() + " saiu do clan.");
            return true;
        }

        //clan transferir <jogador>
        if (args[0].equalsIgnoreCase("transferir") || args[0].equalsIgnoreCase("transfer")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <jogador>");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId())) {
                player.sendMessage("§cApenas o líder do clan pode transferir a liderança.");
                return true;
            }

            String newLeaderName = args[1];
            Player newLeader = Bukkit.getPlayer(newLeaderName);
            if (newLeader == null || !newLeader.isOnline()) {
                player.sendMessage("§cJogador não encontrado ou offline.");
                return true;
            }

            UUID newLeaderUUID = newLeader.getUniqueId();
            if (!clan.isMember(newLeaderUUID)) {
                player.sendMessage("§cO jogador especificado não está no seu clan.");
                return true;
            }

            clan.setLeader(newLeaderUUID);
            clanManager.saveClans();

            clanManager.broadcastMessage(clan.getTag(), clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + "§7A liderança do clan foi transferida para " + newLeader.getName() + ".");
            return true;
        }

        //clan promover <jogador>
        if (args[0].equalsIgnoreCase("promover") || args[0].equalsIgnoreCase("promote")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <jogador>");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId()) && !clan.isManager(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode fazer isso.");
                return true;
            }

            String playerName = args[1];
            Player promotedPlayer = Bukkit.getPlayer(playerName);

            if (promotedPlayer == null || !promotedPlayer.isOnline()) {
                player.sendMessage("§cJogador não encontrado.");
                return true;
            }

            if (clan.isManager(promotedPlayer.getUniqueId())) {
                player.sendMessage("§cO jogador já é um gerente.");
                return true;
            }

            clan.addManager(promotedPlayer.getUniqueId());
            clan.removeMember(promotedPlayer.getUniqueId());
            clanManager.saveClans();

            player.sendMessage("§a" + playerName + " promovido a gerente.");
            return true;
        }

        //clan rebaixar <jogador>
        if (args[0].equalsIgnoreCase("rebaixar") || args[0].equalsIgnoreCase("demote")) {
            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <jogador>");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId()) && !clan.isManager(player.getUniqueId())) {
                player.sendMessage("§cVocê não pode fazer isso.");
                return true;
            }

            String playerName = args[1];
            Player demotedPlayer = Bukkit.getPlayer(playerName);

            if (demotedPlayer == null || !demotedPlayer.isOnline()) {
                player.sendMessage("§cJogador não encontrado.");
                return true;
            }

            if (!clan.isManager(demotedPlayer.getUniqueId())) {
                player.sendMessage("§cO jogador não é um gerente.");
                return true;
            }

            clan.removeManager(demotedPlayer.getUniqueId());
            clan.addMember(demotedPlayer.getUniqueId());
            clanManager.saveClans();

            player.sendMessage("§a" + playerName + " rebaixado a membro.");
            return true;
        }

        if (args[0].equalsIgnoreCase("trocar") || args[0].equalsIgnoreCase("change")) {
            if (args.length != 3) {
                player.sendMessage("§cUse: /clan " + args[0] + " <nome/tag> <novoNome/novaTag>");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());
            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan.");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId())) {
                player.sendMessage("§cApenas o líder pode trocar as informações do clan.");
                return true;
            }

            String type = args[1];
            String newValue = args[2];

            if (type.equalsIgnoreCase("nome") || type.equalsIgnoreCase("name")) {
                if (!(newValue.length() >= 3 && newValue.length() <= 22)) {
                    player.sendMessage("§cO novo nome do clan deve ter entre 3 e 22 caracteres.");
                    return true;
                }

                if (!newValue.matches("[a-zA-Z1-9]+")) {
                    player.sendMessage("§cO novo nome do clan só pode conter letras e números.");
                    return true;
                }

                clan.setName(newValue);
                player.sendMessage("§aNome do clan alterado para: " + newValue);
            } else if (type.equalsIgnoreCase("tag")) {
                if (!(newValue.length() >= 3 && newValue.length() <= 10)) {
                    player.sendMessage("§cA nova tag do clan deve ter entre 3 e 10 caracteres.");
                    return true;
                }

                if (!newValue.matches("[a-zA-Z1-9]+")) {
                    player.sendMessage("§cA nova tag do clan só pode conter letras e números.");
                    return true;
                }

                clan.setTag(newValue);
                player.sendMessage("§aTag do clan alterada para: " + newValue);
            } else {
                player.sendMessage("§cUse: /clan " + args[0] + " <nome/tag> <novoNome/novaTag>");
                return true;
            }

            clanManager.saveClans();
            return true;
        }

        //clan info <nome/tag>
        if (args[0].equalsIgnoreCase("info")) {

            if (args.length == 1) {
                Clan clan = clanManager.getClanByPlayer(player.getUniqueId());

                if (clan == null) {
                    player.sendMessage("§cVocê não está em nenhum clan.");
                    return true;
                }

                int nMembers = clan.getMembers().size() + clan.getManagers().size() + 1;

                Player leader = Bukkit.getPlayer(clan.getLeader());
                String formattedDate = clanManager.getCreationFormattedDate(clan);
                player.sendMessage("§7Informações do Clan §l" + clan.getName());
                player.sendMessage("");
                player.sendMessage("§7Tag: " + clan.getColor().replace('&', '§') + "[" + clan.getTag() + "]");
                player.sendMessage("§7Data de Criação: §5" + formattedDate);
                player.sendMessage("§7Líder: §5" + leader.getName());
                player.sendMessage("§7Gerentes: §5" + clan.getManagers().size());
                player.sendMessage("§7Membros: §5" + nMembers);
                player.sendMessage("§7Discord: §5" + clan.getDiscord());
                return true;
            }

            if (args.length == 2) {
                String clanIdentifier = args[1];
                Clan clan = clanManager.getClanByNameOrTag(clanIdentifier);

                if (clan == null) {
                    player.sendMessage("§cO clan especificado não foi encontrado.");
                    return true;
                }

                int nMembers = clan.getMembers().size() + clan.getManagers().size() + 1;

                Player leader = Bukkit.getPlayer(clan.getLeader());
                String formattedDate = clanManager.getCreationFormattedDate(clan);
                player.sendMessage("§7Informações do Clan §l" + clan.getName());
                player.sendMessage("");
                player.sendMessage("§7Tag: " + clan.getColor().replace('&', '§') + "[" + clan.getTag() + "]");
                player.sendMessage("§7Data de Criação: §5" + formattedDate);
                player.sendMessage("§7Líder: §5" + leader.getName());
                player.sendMessage("§7Gerentes: §5" + clan.getManagers().size());
                player.sendMessage("§7Membros: §5" + nMembers);
                player.sendMessage("§7Discord: §5" + clan.getDiscord());
                return true;
            }

            player.sendMessage("§cUse /clan info <nome/tag>");
            return true;
        }

        if (args[0].equalsIgnoreCase("discord")) {

            if (args.length != 2) {
                player.sendMessage("§cUse: /clan " + args[0] + " <novoDiscord>");
                return true;
            }

            Clan clan = clanManager.getClanByPlayer(player.getUniqueId());

            if (clan == null) {
                player.sendMessage("§cVocê não está em nenhum clan!");
                return true;
            }

            if (!clan.isLeader(player.getUniqueId())) {
                player.sendMessage("§cVocê não é líder de nenhum clan!");
                return true;
            }

            String newDiscord = args[1];

            if (newDiscord.equalsIgnoreCase("none") || newDiscord.equalsIgnoreCase("nenhum")) {
                player.sendMessage("§aDiscord retirado!");
                clan.setDiscord(null);
                clanManager.saveClans();
                return true;
            }

            if (!newDiscord.startsWith("https://discord.gg/")) {
                player.sendMessage("§cUse: https://discord.gg/<discordDoSeuClan>");
                return true;
            }

            player.sendMessage("§aDiscord alterado!");
            clan.setDiscord(newDiscord);
            clanManager.saveClans();
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("clan.reload")) {
                player.sendMessage("");
                player.sendMessage("§cComando não encontrado.");
                player.sendMessage("");
                player.sendMessage("§5/clan info §8§o<nome/tag> §7- Ver informações de algum clan");
                player.sendMessage("§5/clan membros §8§o<nome/tag> §7- Ver membros de algum clan"); //
                player.sendMessage("§5/clan criar §8§o<nome/tag> §7- Criar um novo clan"); //
                player.sendMessage("§5/clan excluir §7- Deletar seu clan"); //
                player.sendMessage("§5/clan convidar §8§o<jogador> §7- Convidar um jogador ao seu clan"); //
                player.sendMessage("§5/clan expulsar §8§o<jogador> §7- Expulsar um jogador do seu clan"); //
                player.sendMessage("§5/clan convites §7- Ver convites de clans pendentes"); //
                player.sendMessage("§5/clan aceitar §8§o<nome/tag> §7- Aceitar o convite pendente de algum clan"); //
                player.sendMessage("§5/clan rejeitar §8§o<nome/tag> §7- Rejeitar o convite pendente de algum clan"); //
                player.sendMessage("§5/clan sair §7- Sair do seu atual clan"); //
                player.sendMessage("§5/clan transferir §8§o<jogador> §7- Transferir a liderança do clan para outro jogador"); //
                player.sendMessage("§5/clan promover §8§o<jogador> §7- Promover um jogador à gerência"); //
                player.sendMessage("§5/clan rebaixar §8§o<jogador> §7- Tornar um gerente membro"); //
                player.sendMessage("§5/clan trocar §8§o<nome/tag> <novoNome/novaTag> §7- Trocar informações do clan"); //
                player.sendMessage("§5/cc §8§o<mensagem> §7- Mande uma mensagem para o clan");
                player.sendMessage("§5/clan stats §8§o<nome/tag> §7- Veja as estátisticas ranqueadas de um clan");
                if (player.hasPermission("clan.colortag")) {
                    player.sendMessage("§5/clan colortag §8§o<nome> <cor> §7- Mude a cor da tag de algum Clan"); //
                }
                return true;
            }

            clanManager.loadClans();
            player.sendMessage("§aPulseClans teve suas configurações relidas ");
            return true;
        }

        if (args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("status")) {

            if (args.length == 1) {

                Clan clan = clanManager.getClanByPlayer(player.getUniqueId());

                if (clan == null) {
                    player.sendMessage("§cVocê não está em nenhum clan!");
                    return true;
                }

                player.sendMessage("");
                player.sendMessage("§7Estatísticas de " + clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + clan.getName());
                player.sendMessage("");
                player.sendMessage("§7Partidas competitivas ganhas: §5" + clan.getGamesWin());
                player.sendMessage("§7Partidas competitivas perdidas: §5" + clan.getGamesDefeat());
                player.sendMessage("");

                return true;
            }

            if (args.length == 2) {

                Clan clan = clanManager.getClanByNameOrTag(args[1]);

                if (clan == null) {
                    player.sendMessage("§cO clan especificado não foi encontrado.");
                    return true;
                }

                player.sendMessage("");
                player.sendMessage("§7Estatísticas de " + clan.getColor().replace('&', '§') + "[" + clan.getTag() + "] " + clan.getName());
                player.sendMessage("");
                player.sendMessage("§7Partidas competitivas ganhas: §5" + clan.getGamesWin());
                player.sendMessage("§7Partidas competitivas perdidas: §5" + clan.getGamesDefeat());
                player.sendMessage("");

                return true;
            }

            player.sendMessage("§cUse: /clan " + args[0] + " <nome/tag>");
            return true;
        }

        player.sendMessage("");
        player.sendMessage("§cComando não encontrado.");
        player.sendMessage("");
        player.sendMessage("§5/clan info §8§o<nome/tag> §7- Ver informações de algum clan");
        player.sendMessage("§5/clan membros §8§o<nome/tag> §7- Ver membros de algum clan"); //
        player.sendMessage("§5/clan criar §8§o<nome/tag> §7- Criar um novo clan"); //
        player.sendMessage("§5/clan excluir §7- Deletar seu clan"); //
        player.sendMessage("§5/clan convidar §8§o<jogador> §7- Convidar um jogador ao seu clan"); //
        player.sendMessage("§5/clan expulsar §8§o<jogador> §7- Expulsar um jogador do seu clan"); //
        player.sendMessage("§5/clan convites §7- Ver convites de clans pendentes"); //
        player.sendMessage("§5/clan aceitar §8§o<nome/tag> §7- Aceitar o convite pendente de algum clan"); //
        player.sendMessage("§5/clan rejeitar §8§o<nome/tag> §7- Rejeitar o convite pendente de algum clan"); //
        player.sendMessage("§5/clan sair §7- Sair do seu atual clan"); //
        player.sendMessage("§5/clan transferir §8§o<jogador> §7- Transferir a liderança do clan para outro jogador"); //
        player.sendMessage("§5/clan promover §8§o<jogador> §7- Promover um jogador à gerência"); //
        player.sendMessage("§5/clan rebaixar §8§o<jogador> §7- Tornar um gerente membro"); //
        player.sendMessage("§5/clan trocar §8§o<nome/tag> <novoNome/novaTag> §7- Trocar informações do clan"); //
        player.sendMessage("§5/cc §8§o<mensagem> §7- Mande uma mensagem para o clan");
        player.sendMessage("§5/clan stats §8§o<nome/tag> §7- Veja as estátisticas ranqueadas de um clan");
        if (player.hasPermission("clan.colortag")) {
            player.sendMessage("§5/clan colortag §8§o<nome> <cor> §7- Mude a cor da tag de algum Clan"); //
        }
        return true;
    }
}