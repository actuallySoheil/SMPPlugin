package me.actuallysoheil.plugin.smp.listener;

import lombok.RequiredArgsConstructor;
import me.actuallysoheil.plugin.smp.manager.LanguageManager;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class PlayerListener implements Listener {

    private final @NotNull LanguageManager languageManager;
    private final @NotNull TeamManager teamManager;

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        this.teamManager.updateTeamAudience(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        this.languageManager.unloadPlayerLanguage(event.getPlayer().getUniqueId());
    }

}