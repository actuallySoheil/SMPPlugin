package me.actuallysoheil.plugin.smp.model.team;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.model.audience.SMPAudience;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
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
    private final @NotNull HashSet<UUID> teamMembers;
    private final @NotNull SMPTeamSettings teamSettings;
    @Setter
    private @NotNull UUID teamLeaderId;
    private SMPAudience teamAudience;

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
        this.teamAudience = SMPAudience.of(
                this.teamMembers.stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    public void sendMessage(@NotNull Component component) {
        this.teamAudience.sendMessage(component);
    }

    public void sendLocalizedMessage(@NotNull LanguagePath languagePath) {
        this.teamAudience.sendLocalizedMessage(languagePath);
    }

    public void sendLocalizedMessage(@NotNull LanguagePath languagePath,
                                     @NotNull PlaceholderLike placeholderLike) {
        this.teamAudience.sendLocalizedMessage(languagePath, placeholderLike);
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