package me.actuallysoheil.plugin.smp.manager;

import lombok.val;
import me.actuallysoheil.plugin.smp.config.PluginSettings;
import me.actuallysoheil.plugin.smp.database.dao.TeamDao;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.model.team.SMPTeam;
import me.actuallysoheil.plugin.smp.model.team.status.*;
import me.actuallysoheil.plugin.smp.utility.TimedHashSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class TeamManager {

    private final @NotNull PluginSettings pluginSettings;
    private final @NotNull TeamDao teamDao;

    private final @NotNull HashSet<SMPTeam> teams;

    // todo: resume cooldown time on server restart.
    private final @NotNull TimedHashSet<UUID> teamCreationCooldown;
    private final @NotNull TimedHashSet<UUID> teamHomeCooldown;

    public TeamManager(@NotNull PluginSettings pluginSettings,
                       @NotNull TeamDao teamDao) {
        this.pluginSettings = pluginSettings;
        this.teamDao = teamDao;

        this.teams = new HashSet<>();

        this.teamCreationCooldown = new TimedHashSet<>();
        this.teamHomeCooldown = new TimedHashSet<>();
    }

    public void loadTeamsFromDatabase() {
        this.teams.addAll(this.teamDao.findAll());
    }

    public @NotNull TeamCreationStatus createTeam(@NotNull String teamId, @NotNull UUID teamLeaderId) {
        if (findTeamByPlayerId(teamLeaderId) != null) return TeamCreationStatus.PLAYER_HAS_TEAM;
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex())) return TeamCreationStatus.TEAM_ID_INVALID;
        if (teamId.length() > this.pluginSettings.maxTeamIdLength()) return TeamCreationStatus.TEAM_ID_LONG;
        if (findTeamById(teamId) != null) return TeamCreationStatus.TEAM_ID_EXISTS;
        if (this.teamCreationCooldown.contains(teamLeaderId)) return TeamCreationStatus.PLAYER_TEAM_CREATION_COOLDOWN;

        val newTeam = new SMPTeam(teamId, teamLeaderId);
        this.teams.add(newTeam);
        this.teamDao.insert(newTeam);

        this.teamCreationCooldown.add(
                teamLeaderId, this.pluginSettings.teamCreationCooldownTimeSeconds(), TimeUnit.SECONDS
        );
        return TeamCreationStatus.SUCCESSFUL;
    }

    public @NotNull TeamDisbandStatus disbandTeam(@NotNull UUID playerId, @NotNull String teamId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamDisbandStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamDisbandStatus.PLAYER_NOT_LEADER;
        if (!playerTeam.teamId().equalsIgnoreCase(teamId)) return TeamDisbandStatus.TEAM_NAME_INVALID;

        playerTeam.sendLocalizedMessage(LanguagePath.BROADCAST_TEAM_DISBAND);
        playerTeam.teamMembers().clear();

        this.teams.remove(playerTeam);
        this.teamDao.delete(teamId);

        return TeamDisbandStatus.SUCCESSFUL;
    }

    public @NotNull TeamKickMemberStatus kickMember(@NotNull UUID playerId, @NotNull String targetName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamKickMemberStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamKickMemberStatus.PLAYER_NOT_LEADER;

        val targetPlayer = Bukkit.getOfflinePlayer(targetName);
        val targetId = targetPlayer.getUniqueId();

        if (playerId.equals(targetId)) return TeamKickMemberStatus.TARGET_IS_SELF;
        if (!playerTeam.isTeamMember(targetId)) return TeamKickMemberStatus.TARGET_NOT_IN_TEAM;

        playerTeam.removeMember(targetId);
        playerTeam.sendLocalizedMessage(
                LanguagePath.BROADCAST_TEAM_KICK,
                PlaceholderLike.builder()
                        .append("member_name", targetPlayer.getName() != null ? targetPlayer.getName() : "???")
        );
        this.teamDao.update(playerTeam);

        return TeamKickMemberStatus.SUCCESSFUL;
    }

    public @NotNull TeamTransferStatus transferTeamLeader(@NotNull UUID playerId, @NotNull String targetName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamTransferStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamTransferStatus.PLAYER_NOT_LEADER;

        val targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) return TeamTransferStatus.TARGET_OFFLINE;

        val targetId = targetPlayer.getUniqueId();
        if (playerId.equals(targetId)) return TeamTransferStatus.TARGET_IS_SELF;
        if (!playerTeam.isTeamMember(targetId)) return TeamTransferStatus.TARGET_NOT_IN_TEAM;

        val offlineOldLeader = Bukkit.getOfflinePlayer(playerTeam.teamLeaderId());
        val oldLeaderUsername = offlineOldLeader.getName() != null ? offlineOldLeader.getName() : "???";

        val offlineNewLeader = Bukkit.getOfflinePlayer(playerId);
        val newLeaderUsername = offlineNewLeader.getName() != null ? offlineNewLeader.getName() : "???";

        playerTeam.sendLocalizedMessage(
                LanguagePath.BROADCAST_TEAM_TRANSFER,
                PlaceholderLike.builder()
                        .append("old_leader", oldLeaderUsername)
                        .append("new_leader", newLeaderUsername)
        );
        playerTeam.teamLeaderId(targetId);
        this.teamDao.update(playerTeam);

        return TeamTransferStatus.SUCCESSFUL;
    }

    public @NotNull TeamLeaveStatus leaveTeam(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamLeaveStatus.PLAYER_LACKING_TEAM;
        if (playerTeam.isTeamLeader(playerId)) return TeamLeaveStatus.PLAYER_IS_LEADER;

        val offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        val memberUsername = offlinePlayer.getName() != null ? offlinePlayer.getName() : "A member";

        playerTeam.teamMembers().remove(playerId);

        playerTeam.sendLocalizedMessage(
                LanguagePath.BROADCAST_TEAM_GENERAL_MEMBER_LEAVE,
                PlaceholderLike.builder().append("member_name", memberUsername)
        );
        this.teamDao.update(playerTeam);

        return TeamLeaveStatus.SUCCESSFUL;
    }

    public @NotNull TeamHomeTeleportStatus teleportToTeamHome(@NotNull Player player) {
        val playerId = player.getUniqueId();

        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamHomeTeleportStatus.PLAYER_LACKING_TEAM;

        if (this.teamHomeCooldown.contains(playerId)) return TeamHomeTeleportStatus.PLAYER_ON_COOLDOWN;

        val homeLocation = playerTeam.teamOptions().homeLocation();
        if (homeLocation == null) return TeamHomeTeleportStatus.TEAM_HOME_NOT_EXIST;

        this.teamHomeCooldown.add(playerId, this.pluginSettings.teamHomeTeleportCooldownTimeSeconds(), TimeUnit.SECONDS);
        player.teleportAsync(homeLocation);

        return TeamHomeTeleportStatus.SUCCESSFUL;
    }

    public void unloadTeams() {
        this.teams.clear();
    }

    public @Nullable SMPTeam findTeamByPlayerId(@NotNull UUID playerId) {
        return this.teams.stream()
                .filter(team -> team.isTeamMember(playerId))
                .findFirst()
                .orElse(null);
    }

    public @Nullable SMPTeam findTeamById(@NotNull String teamId) {
        return this.teams.stream()
                .filter(team -> team.teamId().equalsIgnoreCase(teamId))
                .findFirst()
                .orElse(null);
    }

}