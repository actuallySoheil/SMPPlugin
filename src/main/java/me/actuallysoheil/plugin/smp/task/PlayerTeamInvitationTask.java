package me.actuallysoheil.plugin.smp.task;

import me.actuallysoheil.plugin.smp.config.PluginSettings;
import me.actuallysoheil.plugin.smp.model.team.SMPTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class PlayerTeamInvitationTask extends BukkitRunnable {

    private final @NotNull SMPTeam team;
    private final @NotNull OfflinePlayer targetPlayer;

    private final @NotNull Consumer<PlayerTeamInvitationTask> whileRunning;
    private final @NotNull Runnable whenAccepted, whenExpired;

    private int countdownTimer;

    public PlayerTeamInvitationTask(@NotNull PluginSettings pluginSettings,
                                    @NotNull SMPTeam team,
                                    @NotNull OfflinePlayer targetPlayer,
                                    @NotNull Consumer<PlayerTeamInvitationTask> whileRunning,
                                    @NotNull Runnable whenAccepted, @NotNull Runnable whenExpired) {
        this.team = team;

        this.targetPlayer = targetPlayer;

        this.whileRunning = whileRunning;

        this.whenAccepted = whenAccepted;
        this.whenExpired = whenExpired;

        this.countdownTimer = pluginSettings.teamInviteExpirationTimeSeconds();
    }

    @Override
    public void run() {
        if (this.team.isTeamMember(this.targetPlayer.getUniqueId())) {
            this.whenAccepted.run();
            cancel();
            return;
        }

        if (this.countdownTimer <= 0) {
            this.whenExpired.run();
            cancel();
            return;
        }

        this.whileRunning.accept(this);
        this.countdownTimer--;
    }

}