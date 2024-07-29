package br.com.pulse.clans.commands;

import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ClanAdminCommand implements CommandExecutor {

    private final ClanManager clanManager;

    static String NO_PERM = "§cComando não encontrado ou você não tem permissão!";
    static String NO_FOUND = "§cClan não encontrado!";

    public ClanAdminCommand(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("clan.admin")) {
            sender.sendMessage(NO_PERM);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("");
            sender.sendMessage("§5§lPulseMC §cAdmin §7Clans");
            sender.sendMessage("");
            sender.sendMessage("§c/clanadmin setTournament §8§o<clan/tag> <nomeDoTorneio> <lugar>");
            sender.sendMessage("§c/clanadmin addColor §8§o<clan/tag> <nomeParaACor> <cor>");
            sender.sendMessage("§c/clan excluir §8§o<clan/tag>");
            sender.sendMessage("");
            return true;
        }

        if (args[0].equalsIgnoreCase("setTournament")) {

            if (!sender.hasPermission("clan.setTournament")) {
                sender.sendMessage(NO_PERM);
                return true;
            }

            if (args.length < 4) {
                sender.sendMessage("§cUso incorreto do comando. Use: /clan setarTorneio <clan/tag> <nomeDoTorneio> <lugar>");
                return true;
            }

            String clanIdentifier = args[1];
            String tournamentName = args[2];
            String place = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

            Clan clan = clanManager.getClanByNameOrTag(clanIdentifier);

            if (clan == null) {
                sender.sendMessage(NO_FOUND);
                return true;
            }

            if (!place.matches("1º Lugar|2º Lugar|3º Lugar|Semi Final|Quartas de Final|Fase de Grupos|EXPULSO")) {
                sender.sendMessage("§cLugar inválido. Use 1º Lugar, 2º Lugar, 3º Lugar, Semi Final, Quartas de Final, Fase de Grupos ou EXPULSO.");
                return true;
            }

            clanManager.setTournamentResult(clan.getName(), tournamentName, place);
            sender.sendMessage("§aParticipação do torneio registrada com sucesso.");
            return true;
        }

        if (args[0].equalsIgnoreCase("addColor")) {

            if (!sender.hasPermission("clan.addColor")) {
                sender.sendMessage(NO_PERM);
                return true;
            }

            if (args.length < 4) {
                sender.sendMessage("§cUse: /clan addColor <nome/tag> <NomeParaACor> <cor>");
                return true;
            }

            String clanNameOrTag = args[1];
            String nameForColor = args[2];
            String color = args[3];

            Clan clan = clanManager.getClanByNameOrTag(clanNameOrTag);

            if (clan == null) {
                sender.sendMessage(NO_FOUND);
                return true;
            }

            if (!color.matches("&[0-9a-fA-FlL]")) {
                sender.sendMessage("§cCor inválida. Use uma cor válida, como &a, &b, etc.");
                return true;
            }

            clan.addColorTag(nameForColor, color);
            sender.sendMessage("§aA cor foi adicionada ao clan §l" + clanNameOrTag);
            return true;
        }

        return true;
    }
}
