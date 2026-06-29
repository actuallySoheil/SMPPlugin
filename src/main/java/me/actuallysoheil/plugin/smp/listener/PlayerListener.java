package me.actuallysoheil.plugin.smp.listener;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.manager.AccountManager;
import me.actuallysoheil.plugin.smp.manager.LanguageManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class PlayerListener implements Listener {

    private final @NotNull LanguageManager languageManager;
    private final @NotNull AccountManager accountManager;
    private final @NotNull TeamManager teamManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        val player = event.getPlayer();
        val playerId = player.getUniqueId();

        this.accountManager.loadAccount(playerId, player.getName());
        this.teamManager.updateScoreboardTeamFor(playerId);
        this.teamManager.updateTeamAudience(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        val playerId = event.getPlayer().getUniqueId();
        this.languageManager.unloadPlayerLanguage(playerId);
        this.accountManager.unloadAccount(playerId);
    }

}