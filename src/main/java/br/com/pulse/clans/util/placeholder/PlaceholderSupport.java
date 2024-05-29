package br.com.pulse.clans.util.placeholder;

import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderSupport extends PlaceholderExpansion {

    private final ClanPlaceholder clanPlaceholder;
    private final ClanManager clanManagerS;

    public PlaceholderSupport(ClanManager clanManagerS) {
        this.clanPlaceholder = new ClanPlaceholder(clanManagerS);
        this.clanManagerS = clanManagerS;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bw1058clans";
    }

    @Override
    public @NotNull String getAuthor() {
        return "tadeu";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        switch (identifier) {
            case "clanName":
                return clanPlaceholder.replacePlaceholders(player, "%clanName%");
            case "clanTag":
                return clanPlaceholder.replacePlaceholders(player, "%clanTag%");
            case "clanTabTag":
                Clan clan = clanManagerS.getClanByPlayer(player.getUniqueId());
                if (clan == null || clan.getTag().isEmpty()) {
                    return " "; // Espaços vazios para substituir a tag
                } else {
                    return clan.getColor() + " [" + clan.getTag() + "] "; // Tag com espaços em branco ao redor
                }
        }

        return null;
    }
}