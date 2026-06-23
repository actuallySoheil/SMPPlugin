package me.actuallysoheil.plugin.smp.model.team;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.model.audience.SMPAudience;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = {"teamId", "teamLeaderId"})
public final class SMPTeam {

    private final @NotNull String teamId;
    private final @NotNull HashSet<UUID> teamMembers;
    private final @NotNull SMPTeamOptions teamOptions;
    @Setter
    private @NotNull UUID teamLeaderId;
    private SMPAudience teamAudience;

    public SMPTeam(@NotNull String teamId, @NotNull UUID teamLeaderId) {
        this.teamId = teamId;
        this.teamLeaderId = teamLeaderId;

        this.teamMembers = new HashSet<>();
        this.teamMembers.add(teamLeaderId);

        this.teamOptions = new SMPTeamOptions(this);
        this.teamOptions.tagName(this.teamId);

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

    public boolean isTeamMember(@NotNull UUID playerId) {
        return this.teamMembers.contains(playerId);
    }

    public boolean isTeamLeader(@NotNull UUID playerId) {
        return playerId.equals(this.teamLeaderId);
    }

}