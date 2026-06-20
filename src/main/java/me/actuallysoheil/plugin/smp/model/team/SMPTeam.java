package me.actuallysoheil.plugin.smp.model.team;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
public final class SMPTeam {

    private final @NotNull String teamId;
    @Setter
    private @NotNull UUID teamLeaderId;

    private final @NotNull HashSet<UUID> teamMembers;

    private final @NotNull SMPTeamSettings teamSettings;

    private Audience teamAudience;

    public SMPTeam(@NotNull String teamId, @NotNull UUID teamLeaderId) {
        this.teamId = teamId;
        this.teamLeaderId = teamLeaderId;

        this.teamMembers = new HashSet<>();
        this.teamMembers.add(teamLeaderId);

        this.teamSettings = new SMPTeamSettings(this);
        this.teamSettings.tagName(this.teamId);

        updateTeamAudience();
    }

    public void addMember(@NotNull UUID playerId) {
        this.teamMembers.add(playerId);
        updateTeamAudience();
    }

    public void removeMember(@NotNull UUID playerId) {
        this.teamMembers.remove(playerId);
        updateTeamAudience();
    }

    private void updateTeamAudience() {
        this.teamAudience = Audience.audience(
                this.teamMembers.stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .toArray(Audience[]::new)
        );
    }

    public void sendMessage(@NotNull Component component) {
        this.teamAudience.sendMessage(component);
    }

    public void playSound(@NotNull Sound sound) {
        this.teamAudience.playSound(sound);
    }

    public void showTitle(@NotNull Title title) {
        this.teamAudience.showTitle(title);
    }

    public void showBossBar(@NotNull BossBar bossBar) {
        this.teamAudience.showBossBar(bossBar);
    }

    public boolean isTeamMember(@NotNull UUID playerId) {
        return this.teamMembers.contains(playerId);
    }

    public boolean isTeamLeader(@NotNull UUID playerId) {
        return playerId.equals(this.teamLeaderId);
    }

}