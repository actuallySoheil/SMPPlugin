package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.manager.team.TeamInvitationManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
@SubCommand(label = "decline", description = "Decline a team invitation.")
public final class TeamDeclineInvitationSubcommand extends SubCommandHandler {

    private static final @NotNull String NOT_FOUND_TEXT = "???";

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team decline <teamName>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        val playerId = player.getUniqueId();
        val teamLeaderName = teamLeaderNameById(teamId, playerId);
        switch (this.teamInvitationManager.declineInvitation(teamId, playerId)) {
            case PLAYER_LACKING_INVITE ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_LACKING);
            case TEAM_ID_INVALID ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_INVALID);
            case TEAM_INVALID ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_NOT_EXISTS);
            case SUCCESSFUL -> SMPMedia.sendMessage(
                    player,
                    LanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_DECLINED,
                    PlaceholderLike.builder().append("host_name", teamLeaderName)
            );
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 1) return suggestWithStartingPrefix(
                this.teamInvitationManager.pendingTeamNamesByPlayerId(player.getUniqueId()),
                arguments
        );
        return Collections.emptyList();
    }

    private @NotNull String teamLeaderNameById(@NotNull String teamId, @NotNull UUID playerId) {
        val pendingTeamNames = this.teamInvitationManager.pendingTeamNamesByPlayerId(playerId);

        for (val pendingTeamName : pendingTeamNames) {
            if (!pendingTeamName.equalsIgnoreCase(teamId)) continue;

            val team = this.teamManager.findTeamById(pendingTeamName);
            if (team == null) return NOT_FOUND_TEXT;

            val teamLeader = Bukkit.getOfflinePlayer(team.teamLeaderId());
            val teamLeaderName = teamLeader.getName();
            return teamLeaderName != null ? teamLeaderName : NOT_FOUND_TEXT;
        }

        return NOT_FOUND_TEXT;
    }

}