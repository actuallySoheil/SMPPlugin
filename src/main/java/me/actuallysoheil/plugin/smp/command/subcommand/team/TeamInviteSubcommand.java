package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.manager.team.TeamInvitationManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@SubCommand(label = "invite", description = "Invite a player.")
public final class TeamInviteSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team invite <player>");
            return;
        }

        val targetPlayerName = arguments[0];
        switch (this.teamInvitationManager.invitePlayer(player.getUniqueId(), targetPlayerName)) {
            case PLAYER_LACKING_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_NOT_LEADER ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER);
            case TEAM_ON_CAPACITY ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ON_CAPACITY);
            case TARGET_OFFLINE ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_GENERAL_ERROR_PLAYER_OFFLINE);
            case TARGET_IS_IN_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_TEAM);
            case TARGET_IS_IN_ANOTHER_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_ANOTHER_TEAM);
            case TARGET_IS_SELF ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_SELF);
            case TARGET_ALREADY_INVITED ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ALREADY_INVITED);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 1) {
            return suggestWithStartingPrefix(
                    Bukkit.getOnlinePlayers().stream()
                            .filter(onlinePlayer -> this.teamManager
                                    .findTeamByPlayerId(onlinePlayer.getUniqueId()) == null
                            )
                            .map(Player::getName)
                            .toList(),
                    arguments
            );
        }

        return Collections.emptyList();
    }

}