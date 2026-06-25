package me.actuallysoheil.plugin.smp.manager;

import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.config.PluginSettings;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.model.team.SMPTeam;
import me.actuallysoheil.plugin.smp.model.team.status.TeamAcceptInvitationStatus;
import me.actuallysoheil.plugin.smp.model.team.status.TeamInvitationStatus;
import me.actuallysoheil.plugin.smp.task.PlayerTeamInvitationTask;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

public final class TeamInvitationManager {

    private final @NotNull PluginSettings pluginSettings;

    private final @NotNull TeamManager teamManager;
    private final @NotNull HashMap<SMPTeam, HashSet<UUID>> pendingTeamInvites;

    public TeamInvitationManager(@NotNull PluginSettings pluginSettings,
                                 @NotNull TeamManager teamManager) {
        this.pluginSettings = pluginSettings;

        this.teamManager = teamManager;
        this.pendingTeamInvites = new HashMap<>();
    }

    public @NotNull TeamInvitationStatus invitePlayer(@NotNull UUID playerId,
                                                      @NotNull String targetName) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamInvitationStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamInvitationStatus.PLAYER_NOT_LEADER;
        if (playerTeam.teamMembers().size() >= this.pluginSettings.maxTeamMember())
            return TeamInvitationStatus.TEAM_ON_CAPACITY;

        val targetPlayer = Bukkit.getPlayerExact(targetName);
        if (targetPlayer == null) return TeamInvitationStatus.TARGET_OFFLINE;

        val targetId = targetPlayer.getUniqueId();
        if (playerId.equals(targetId)) return TeamInvitationStatus.TARGET_IS_SELF;

        val targetUsername = targetPlayer.getName();

        val targetTeam = this.teamManager.findTeamByPlayerId(targetId);
        if (targetTeam != null) return TeamInvitationStatus.TARGET_HAS_TEAM;

        val targetIsAlreadyInvited = pendingTeamInvites(playerTeam).contains(targetId);
        if (targetIsAlreadyInvited) return TeamInvitationStatus.TARGET_ALREADY_INVITED;

        pendingTeamInvites(playerTeam).add(targetId);

        val teamLeader = Bukkit.getPlayer(playerId);
        if (teamLeader != null) {
            playerTeam.sendLocalizedMessage(
                    LanguagePath.BROADCAST_TEAM_INVITATION_MEMBERS,
                    PlaceholderLike.builder()
                            .append("team_leader_name", teamLeader.getName())
                            .append("target_name", targetUsername)
            );
            SMPMedia.sendMessage(
                    targetPlayer,
                    LanguagePath.BROADCAST_TEAM_INVITATION_TARGET,
                    PlaceholderLike.builder()
                            .append("team_leader_name", teamLeader.getName())
                            .append("team_id", playerTeam.teamId())
            );
        }

        new PlayerTeamInvitationTask(
                this.pluginSettings,
                playerTeam, targetPlayer,
                teamInvitationTask -> {
                    if (this.teamManager.findTeamById(playerTeam.teamId()) != null) return;
                    teamInvitationTask.cancel();
                },
                () -> {
                    pendingTeamInvites(playerTeam).remove(targetId);
                    playerTeam.sendLocalizedMessage(
                            LanguagePath.BROADCAST_TEAM_GENERAL_MEMBER_JOIN,
                            PlaceholderLike.builder().append("member_name", targetUsername)
                    );
                },
                () -> {
                    pendingTeamInvites(playerTeam).remove(targetId);

                    val offlineLeader = Bukkit.getOfflinePlayer(playerId);
                    val offlineLeaderUsername = offlineLeader.getName() != null
                            ? offlineLeader.getName()
                            : "???";
                    if (targetPlayer.isOnline()) SMPMedia.sendMessage(
                            targetPlayer,
                            LanguagePath.BROADCAST_TEAM_INVITATION_EXPIRED_TARGET,
                            PlaceholderLike.builder()
                                    .append("team_leader_name", offlineLeaderUsername)
                    );

                    if (offlineLeader.getPlayer() != null) {
                        SMPMedia.sendMessage(
                                offlineLeader.getPlayer(),
                                LanguagePath.BROADCAST_TEAM_INVITATION_EXPIRED,
                                PlaceholderLike.builder().append("target_name", targetUsername)
                        );
                    }
                }
        ).runTaskTimerAsynchronously(SMPPlugin.instance(), 0L, 20L);

        return TeamInvitationStatus.SUCCESSFUL;
    }

    public @NotNull TeamAcceptInvitationStatus acceptInvite(@NotNull String teamId,
                                                            @NotNull UUID playerId) {
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex()))
            return TeamAcceptInvitationStatus.TEAM_ID_INVALID;

        val targetTeam = this.teamManager.findTeamById(teamId);
        if (targetTeam == null) return TeamAcceptInvitationStatus.TEAM_INVALID;

        val pendingTargetTeamInvites = pendingTeamInvites(targetTeam);
        if (pendingTargetTeamInvites.isEmpty() || !pendingTargetTeamInvites.contains(playerId))
            return TeamAcceptInvitationStatus.PLAYER_LACKING_INVITE;

        if (targetTeam.teamMembers().size() >= this.pluginSettings.maxTeamMember())
            return TeamAcceptInvitationStatus.TEAM_ON_CAPACITY;

        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam != null) return TeamAcceptInvitationStatus.PLAYER_HAS_TEAM;

        targetTeam.addMember(playerId);

        return TeamAcceptInvitationStatus.SUCCESSFUL;
    }

    private @NotNull HashSet<UUID> pendingTeamInvites(@NotNull SMPTeam team) {
        return this.pendingTeamInvites.computeIfAbsent(team, _ -> new HashSet<>());
    }

    public @NotNull Collection<String> pendingTeamNamesByPlayerId(@NotNull UUID playerId) {
        return this.pendingTeamInvites.keySet().stream()
                .filter(this.pendingTeamInvites::containsKey)
                .filter(team -> this.pendingTeamInvites.get(team).contains(playerId))
                .map(SMPTeam::teamId)
                .collect(Collectors.toList());
    }

}