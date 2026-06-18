package me.actuallysoheil.plugin.smp.manager;

import lombok.val;
import me.actuallysoheil.plugin.smp.team.SMPTeam;
import me.actuallysoheil.plugin.smp.team.status.*;
import me.actuallysoheil.plugin.smp.utility.DefaultMessages;
import me.actuallysoheil.plugin.smp.utility.TimedHashSet;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class TeamManager {

    private static final @NotNull String ALLOWED_TEAM_ID_REGEX = "^[a-zA-Z0-9_]+$";
    private static final int MAX_TEAM_ID_LENGTH = 12;
    private static final int TEAM_CREATION_COOLDOWN_TIME_SECONDS = 60;

    private final @NotNull HashSet<SMPTeam> teams;

    // todo: resume cooldown time on server restart.
    private final @NotNull TimedHashSet<UUID> teamCreationCooldown;

    public TeamManager() {
        this.teams = new HashSet<>();

        this.teamCreationCooldown = new TimedHashSet<>();
    }

    public @NotNull TeamCreationStatus createTeam(@NotNull String teamId, @NotNull UUID teamLeaderId) {
        if (findTeamByPlayerId(teamLeaderId) != null) return TeamCreationStatus.PLAYER_HAS_TEAM;
        if (!teamId.matches(ALLOWED_TEAM_ID_REGEX)) return TeamCreationStatus.TEAM_ID_INVALID;
        if (teamId.length() > MAX_TEAM_ID_LENGTH) return TeamCreationStatus.TEAM_ID_LONG;
        if (findTeamById(teamId) != null) return TeamCreationStatus.TEAM_ID_EXISTS;
        if (this.teamCreationCooldown.contains(teamLeaderId)) return TeamCreationStatus.PLAYER_TEAM_CREATION_COOLDOWN;

        this.teams.add(new SMPTeam(teamId, teamLeaderId));
        this.teamCreationCooldown.add(teamLeaderId, TEAM_CREATION_COOLDOWN_TIME_SECONDS, TimeUnit.SECONDS);
        return TeamCreationStatus.SUCCESSFUL;
    }

    public @NotNull TeamDisbandStatus disbandTeam(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamDisbandStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isTeamLeader(playerId)) return TeamDisbandStatus.PLAYER_NOT_LEADER;

        playerTeam.sendMessage(DefaultMessages.TEAM_DISBAND.message());
        playerTeam.teamMembers().clear();

        this.teams.remove(playerTeam);
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
        playerTeam.sendMessage(DefaultMessages.TEAM_MEMBER_KICK.message(
                Placeholder.unparsed(
                        "player_name",
                        targetPlayer.getName() != null ? targetPlayer.getName() : "A member"
                )
        ));

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
        val oldLeaderUsername = offlineOldLeader.getName() != null ? offlineOldLeader.getName() : "Old leader";

        val offlineNewLeader = Bukkit.getOfflinePlayer(playerId);
        val newLeaderUsername = offlineNewLeader.getName() != null ? offlineNewLeader.getName() : "New leader";

        playerTeam.sendMessage(DefaultMessages.TEAM_MEMBER_TRANSFER.message(
                Placeholder.unparsed("old_leader", oldLeaderUsername),
                Placeholder.unparsed("new_leader", newLeaderUsername)
        ));
        playerTeam.teamLeaderId(targetId);

        return TeamTransferStatus.SUCCESSFUL;
    }

    public @NotNull TeamLeaveStatus leaveTeam(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return TeamLeaveStatus.PLAYER_LACKING_TEAM;
        if (playerTeam.isTeamLeader(playerId)) return TeamLeaveStatus.PLAYER_IS_LEADER;

        val offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        val offlineUsername = offlinePlayer.getName() != null ? offlinePlayer.getName() : "A member";

        playerTeam.teamMembers().remove(playerId);
        playerTeam.sendMessage(DefaultMessages.TEAM_MEMBER_LEAVE.message(
                Placeholder.unparsed("player_name", offlineUsername)
        ));

        return TeamLeaveStatus.SUCCESSFUL;
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