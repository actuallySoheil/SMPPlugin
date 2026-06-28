package me.actuallysoheil.plugin.smp.manager;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.config.PluginSettings;
import me.actuallysoheil.plugin.smp.database.dao.TeamOptionsDao;
import me.actuallysoheil.plugin.smp.model.team.SMPTeamOptions;
import me.actuallysoheil.plugin.smp.model.team.status.TeamChangeOptionsStatus;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public final class TeamOptionsManager {

    private final @NotNull PluginSettings pluginSettings;

    private final @NotNull TeamTagManager teamTagManager;
    private final @NotNull TeamManager teamManager;

    private final @NotNull TeamOptionsDao teamOptionsDao;

    public @NotNull TeamChangeOptionsStatus changeTeamOptions(@NotNull UUID playerId,
                                                              @NotNull SMPTeamOptions.Builder optionsBuilder) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamChangeOptionsStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamChangeOptionsStatus.PLAYER_NOT_LEADER;

        val currentOptions = playerTeam.teamOptions();
        val testOptions = new SMPTeamOptions(playerTeam);
        testOptions.tagName(currentOptions.tagName());
        testOptions.tagColor(currentOptions.tagColor());
        testOptions.friendlyFire(currentOptions.friendlyFire());
        testOptions.chatMuted(currentOptions.chatMuted());
        testOptions.homeLocation(currentOptions.homeLocation());

        try {
            optionsBuilder.apply(testOptions);
        } catch (Exception exception) {
            return TeamChangeOptionsStatus.UNKNOWN_ERROR;
        }

        val validationStatus = validateTeamOptions(testOptions);
        if (!validationStatus.equals(TeamChangeOptionsStatus.SUCCESSFUL)) return validationStatus;

        currentOptions.tagName(testOptions.tagName());
        currentOptions.tagColor(testOptions.tagColor());
        currentOptions.friendlyFire(testOptions.friendlyFire());
        currentOptions.chatMuted(testOptions.chatMuted());
        currentOptions.homeLocation(testOptions.homeLocation());

        this.teamTagManager.updateScoreboardTeam(playerTeam);
        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);

        this.teamOptionsDao.update(currentOptions);

        return TeamChangeOptionsStatus.SUCCESSFUL;
    }

    private boolean isInBlacklistedWorld(@NotNull World world) {
        return this.pluginSettings.blacklistedTeamHomeWorlds().stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .anyMatch(blacklistedWorld -> blacklistedWorld.getName().equals(world.getName()));
    }

    private @NotNull TeamChangeOptionsStatus validateTeamOptions(@NotNull SMPTeamOptions teamOptions) {
        if (!teamOptions.tagName().matches(this.pluginSettings.allowedTeamIdRegex()))
            return TeamChangeOptionsStatus.TAG_NAME_INVALID;
        if (teamOptions.tagName().length() > this.pluginSettings.maxTeamTagLength())
            return TeamChangeOptionsStatus.TAG_NAME_LONG;
        if (teamOptions.tagColor() == null) return TeamChangeOptionsStatus.TAG_COLOR_INVALID;

        val homeLocation = teamOptions.homeLocation();
        if (homeLocation != null)
            if (isInBlacklistedWorld(homeLocation.getWorld()))
                return TeamChangeOptionsStatus.HOME_WORLD_BLACKLISTED;

        return TeamChangeOptionsStatus.SUCCESSFUL;
    }

}