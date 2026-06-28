package me.actuallysoheil.plugin.smp.model.team;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.model.audience.SMPAudience;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import net.kyori.adventure.text.Component;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
@EqualsAndHashCode(of = {"teamId", "teamLeaderId"})
@ToString(of = {"teamId", "teamMembers", "teamLeaderId", "teamOptions"})
public final class SMPTeam {

    @BsonId
    @Setter
    private @NotNull String teamId;
    @Setter
    private @NotNull UUID teamLeaderId;

    @Setter
    private @NotNull HashSet<UUID> teamMembers;

    @Setter
    private @NotNull SMPTeamOptions teamOptions;

    private transient SMPAudience teamAudience;

    @SuppressWarnings("unused") // Used by database.
    public SMPTeam() {
        this.teamMembers = new HashSet<>();
        this.teamOptions = new SMPTeamOptions(this);
    }

    public SMPTeam(@NotNull String teamId, @NotNull UUID teamLeaderId) {
        this.teamId = teamId;
        this.teamLeaderId = teamLeaderId;

        this.teamMembers = new HashSet<>();

        this.teamOptions = new SMPTeamOptions(this);

        addMember(teamLeaderId);
    }

    public void addMember(@NotNull UUID playerId) {
        this.teamMembers.add(playerId);
        updateTeamAudience();
    }

    public void removeMember(@NotNull UUID playerId) {
        this.teamMembers.remove(playerId);
        updateTeamAudience();
    }

    public void updateTeamAudience() {
        this.teamAudience = SMPAudience.of(
                this.teamMembers.stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    public void sendMessage(@NotNull Component component) {
        if (this.teamAudience != null) this.teamAudience.sendMessage(component);
    }

    public void sendLocalizedMessage(@NotNull LanguagePath languagePath) {
        if (this.teamAudience != null) this.teamAudience.sendLocalizedMessage(languagePath);
    }

    public void sendLocalizedMessage(@NotNull LanguagePath languagePath,
                                     @NotNull PlaceholderLike placeholderLike) {
        if (this.teamAudience != null) this.teamAudience.sendLocalizedMessage(languagePath, placeholderLike);
    }

    public boolean isTeamMember(@NotNull UUID playerId) {
        return this.teamMembers.contains(playerId);
    }

    public boolean isTeamLeader(@NotNull UUID playerId) {
        return playerId.equals(this.teamLeaderId);
    }

}