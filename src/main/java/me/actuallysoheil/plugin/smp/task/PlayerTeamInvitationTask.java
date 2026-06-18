package me.actuallysoheil.plugin.smp.task;

import lombok.RequiredArgsConstructor;
import me.actuallysoheil.plugin.smp.team.SMPTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@RequiredArgsConstructor
public final class PlayerTeamInvitationTask extends BukkitRunnable {

    private static final int DEFAULT_EXPIRATION_START_TIMER = 60;

    private final @NotNull SMPTeam team;
    private final @NotNull OfflinePlayer targetPlayer;

    private final @NotNull Consumer<PlayerTeamInvitationTask> whileRunning;
    private final @NotNull Runnable whenAccepted, whenExpired;

    private int countdownTimer = DEFAULT_EXPIRATION_START_TIMER;

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