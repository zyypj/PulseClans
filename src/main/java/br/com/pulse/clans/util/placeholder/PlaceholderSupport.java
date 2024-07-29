package br.com.pulse.clans.util.placeholder;

import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class PlaceholderSupport extends PlaceholderExpansion {

    private final ClanPlaceholder clanPlaceholder;
    private final ClanManager clanManagerS;
    private final Map<UUID, Boolean> displayPreferences;

    public PlaceholderSupport(ClanManager clanManagerS, File pluginFolder, Map<UUID, Boolean> displayPreferences) {
        this.clanPlaceholder = new ClanPlaceholder(clanManagerS);
        this.clanManagerS = clanManagerS;
        this.displayPreferences = displayPreferences;
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
                    boolean displayTag = displayPreferences.getOrDefault(player.getUniqueId(), true);
                    if (displayTag) {
                        return clan.getColor() + " [" + clan.getTag() + "] "; // Tag com espaços em branco ao redor
                    } else {
                        return " ";
                    }
                }
        }

        return null;
    }
}