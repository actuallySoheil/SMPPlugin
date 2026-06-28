package me.actuallysoheil.plugin.smp.manager.team;

import lombok.val;
import me.actuallysoheil.plugin.smp.model.team.SMPTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public final class TeamTagManager {

    private static final @NotNull String SMP_TEAM_PREFIX = "smp_";

    private final @NotNull HashMap<String, Team> scoreboardTeams;

    private final @NotNull Scoreboard scoreboard;

    public TeamTagManager() {
        this.scoreboardTeams = new HashMap<>();

        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public @NotNull Team createTeamScoreboardForTeam(@NotNull String teamId) {
        val scoreboardTeamName = SMP_TEAM_PREFIX + teamId;
        var scoreboardTeam = this.scoreboard.getTeam(scoreboardTeamName);
        if (scoreboardTeam == null) scoreboardTeam = this.scoreboard.registerNewTeam(scoreboardTeamName);

        this.scoreboardTeams.putIfAbsent(teamId, scoreboardTeam);
        return scoreboardTeam;
    }

    public void updateScoreboardTeam(@NotNull SMPTeam team) {
        val teamId = team.teamId();
        val scoreboardTeam = findScoreboardTeamByTeamIdElse(
                teamId,
                _ -> createTeamScoreboardForTeam(teamId)
        );

        val teamOptions = team.teamOptions();
        val tagName = teamOptions.tagName();
        val tagColor = teamOptions.tagColor();

        scoreboardTeam.displayName(Component.text(tagName, tagColor));
        scoreboardTeam.prefix(Component.text("[" + tagName + "] ", tagColor));
        scoreboardTeam.color(tagColor);
        scoreboardTeam.setAllowFriendlyFire(teamOptions.friendlyFire());
    }

    public void updateScoreboardTeamMembers(@NotNull SMPTeam team) {
        val scoreboardTeam = findScoreboardTeamByTeamId(team.teamId());
        if (scoreboardTeam == null) return;

        team.teamMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(scoreboardTeam::addPlayer);
    }

    public void removeScoreboardTeam(@NotNull String teamId) {
        val scoreboardTeamName = SMP_TEAM_PREFIX + teamId;
        val scoreboardTeam = this.scoreboard.getTeam(scoreboardTeamName);

        if (scoreboardTeam != null) scoreboardTeam.unregister();
        this.scoreboardTeams.remove(teamId);
    }

    public @Nullable Team findScoreboardTeamByTeamId(@NotNull String teamId) {
        return this.scoreboardTeams.get(teamId);
    }

    public @NotNull Team findScoreboardTeamByTeamIdElse(@NotNull String teamId,
                                                        @NotNull Function<String, Team> defaultValue) {
        return this.scoreboardTeams.getOrDefault(teamId, defaultValue.apply(teamId));
    }

}