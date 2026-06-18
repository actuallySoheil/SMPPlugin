package me.actuallysoheil.plugin.smp.manager;

import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.task.PlayerTeamInvitationTask;
import me.actuallysoheil.plugin.smp.team.SMPTeam;
import me.actuallysoheil.plugin.smp.team.status.TeamAcceptInvitationStatus;
import me.actuallysoheil.plugin.smp.team.status.TeamInvitationStatus;
import me.actuallysoheil.plugin.smp.utility.DefaultMessages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class TeamInvitationManager {

    private static final int MAX_TEAM_MEMBERS = 6;

    private final @NotNull TeamManager teamManager;
    private final @NotNull HashMap<SMPTeam, HashSet<UUID>> pendingTeamInvites;

    public TeamInvitationManager(@NotNull TeamManager teamManager) {
        this.teamManager = teamManager;
        this.pendingTeamInvites = new HashMap<>();
    }

    public @NotNull TeamInvitationStatus invitePlayer(@NotNull UUID playerId,
                                                      @NotNull String targetName) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamInvitationStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamInvitationStatus.PLAYER_NOT_LEADER;
        if (playerTeam.teamMembers().size() >= MAX_TEAM_MEMBERS) return TeamInvitationStatus.TEAM_ON_CAPACITY;

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
            playerTeam.sendMessage(DefaultMessages.TEAM_INVITE.message(
                    Placeholder.unparsed("team_leader_name", teamLeader.getName()),
                    Placeholder.unparsed("player_name", targetUsername)
            ));
            targetPlayer.sendMessage(DefaultMessages.TEAM_INVITE_TARGET.message(
                    Placeholder.unparsed("team_leader_name", teamLeader.getName()),
                    Placeholder.parsed("team_id", playerTeam.teamId())
            ));
        }

        new PlayerTeamInvitationTask(
                playerTeam, targetPlayer,
                teamInvitationTask -> {
                    if (this.teamManager.findTeamById(playerTeam.teamId()) == null) {
                        Bukkit.getOnlinePlayers()
                                .forEach(onlinePlayer -> onlinePlayer.sendMessage(
                                        "<red>Task canceled"
                                ));
                        teamInvitationTask.cancel();
                    }
                },
                () -> {
                    pendingTeamInvites(playerTeam).remove(targetId);
                    playerTeam.sendMessage(DefaultMessages.TEAM_MEMBER_JOIN.message(
                            Placeholder.unparsed("player_name", targetUsername)
                    ));
                },
                () -> {
                    pendingTeamInvites(playerTeam).remove(targetId);

                    val offlineLeader = Bukkit.getOfflinePlayer(playerId);
                    val offlineLeaderUsername = offlineLeader.getName() != null
                            ? offlineLeader.getName()
                            : "an unknown team";
                    if (targetPlayer.isOnline()) targetPlayer.sendMessage(
                            DefaultMessages.TEAM_INVITE_EXPIRED.message(
                                    Placeholder.unparsed("team_leader_name", offlineLeaderUsername)
                            )
                    );

                    if (offlineLeader.getPlayer() != null) offlineLeader.getPlayer().sendMessage(
                            DefaultMessages.TEAM_INVITE_TARGET_EXPIRED.message(
                                    Placeholder.unparsed("player_name", targetUsername)
                            )
                    );
                }
        ).runTaskTimerAsynchronously(SMPPlugin.instance(), 0L, 20L);

        return TeamInvitationStatus.SUCCESSFUL;
    }

    public @NotNull TeamAcceptInvitationStatus acceptInvite(@NotNull String teamId,
                                                            @NotNull UUID playerId) {
        val targetTeam = this.teamManager.findTeamById(teamId);
        if (targetTeam == null) return TeamAcceptInvitationStatus.TEAM_INVALID;

        val pendingTargetTeamInvites = pendingTeamInvites(targetTeam);
        if (pendingTargetTeamInvites.isEmpty() || !pendingTargetTeamInvites.contains(playerId))
            return TeamAcceptInvitationStatus.PLAYER_LACKING_INVITE;

        if (targetTeam.teamMembers().size() >= MAX_TEAM_MEMBERS) return TeamAcceptInvitationStatus.TEAM_ON_CAPACITY;

        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam != null) return TeamAcceptInvitationStatus.PLAYER_HAS_TEAM;

        targetTeam.addMember(playerId);

        return TeamAcceptInvitationStatus.SUCCESSFUL;
    }

    private @NotNull HashSet<UUID> pendingTeamInvites(@NotNull SMPTeam team) {
        return this.pendingTeamInvites.computeIfAbsent(team, _ -> new HashSet<>());
    }

}