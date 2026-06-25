package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@SubCommand(label = "kick", description = "Kick a team member")
public final class TeamKickMemberSubcommand extends SubExecutor {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team kick <player>");
            return;
        }

        val targetPlayerName = arguments[0];
        switch (this.teamManager.kickMember(player.getUniqueId(), targetPlayerName)) {
            case PLAYER_LACKING_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_NOT_LEADER ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER);
            case TARGET_NOT_IN_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_LACKING_TEAM);
            case TARGET_IS_SELF ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_SELF);
        }
    }

    @Override
    public @NotNull Collection<String> completions(@NotNull Player player, @NonNull @NotNull String[] arguments) {
        if (arguments.length == 1) {
            val playerId = player.getUniqueId();
            val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
            if (playerTeam == null || !playerTeam.isTeamLeader(playerId)) return List.of();

            return playerTeam.teamMembers().stream()
                    .filter(memberId -> !memberId.equals(playerTeam.teamLeaderId()))
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .toList();
        }

        return List.of();
    }

}