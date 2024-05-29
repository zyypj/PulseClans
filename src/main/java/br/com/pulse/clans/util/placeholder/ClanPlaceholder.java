package br.com.pulse.clans.util.placeholder;
import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import org.bukkit.OfflinePlayer;

public class ClanPlaceholder {

    private final ClanManager clanManagerS;

    public ClanPlaceholder(ClanManager clanManagerS) {
        this.clanManagerS = clanManagerS;
    }

    public String replacePlaceholders(OfflinePlayer player, String text) {
        if (player == null) {
            return text;
        }

        Clan clan = clanManagerS.getClanByPlayer(player.getUniqueId());
        if (clan == null) {
            return text.replace("%clanName%", "§c[Sem Clan]").replace("%clanTag%", "§c[Sem Clan]");
        }

        return text.replace("%clanName%", clan.getName())
                .replace("%clanTag%", clan.getTag());
    }
}
