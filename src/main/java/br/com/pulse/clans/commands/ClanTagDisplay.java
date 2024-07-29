package br.com.pulse.clans.commands;

import br.com.pulse.clans.Main;
import br.com.pulse.clans.util.Clan;
import br.com.pulse.clans.util.ClanManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ClanTagDisplay implements CommandExecutor {

    private final File file;
    private final Main plugin;
    private final ClanManager clanManager;
    private final FileConfiguration config;

    public ClanTagDisplay(Main plugin, ClanManager clanManager) {
        this.file = new File(plugin.getDataFolder(), "displays.yml");
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.config = YamlConfiguration.loadConfiguration(file);
        loadDisplay();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage("§cUse /clandisplaytag <on/off>");
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        Clan clan = clanManager.getClanByPlayer(playerUUID);

        if (clan == null) {
            player.sendMessage("§cVocê não está em nenhum clan!");
            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {
            clanManager.getDisplayPreferences().put(playerUUID, true);
            player.sendMessage("§aA exibição da tag do clan está ativada.");
            clanManager.saveDisplayPreferences();
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            clanManager.getDisplayPreferences().put(playerUUID, false);
            player.sendMessage("§aA exibição da tag do clan está desativada.");
            clanManager.saveDisplayPreferences();
            return true;
        }

        player.sendMessage("§cUse /clandisplaytag <on/off>");
        return true;
    }

    public void loadDisplay() {
        if (!config.contains("clans")) {
            return;
        }
        for (String uuidString : config.getConfigurationSection("clans").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                boolean display = config.getBoolean("clans." + uuidString);
                clanManager.getDisplayPreferences().put(uuid, display);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}