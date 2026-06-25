package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "create", description = "Create a team.")
public final class TeamCreateSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team create <name>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamManager.createTeam(teamId, player.getUniqueId())) {
            case SUCCESSFUL -> SMPMedia.sendMessage(
                    player,
                    LanguagePath.MESSAGE_COMMAND_TEAM_CREATION_SUCCESS,
                    PlaceholderLike.builder().append("team_name", teamId)
            );
            case TEAM_ID_EXISTS ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_CREATION_ERROR_EXISTS);
            case TEAM_ID_INVALID ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_INVALID);
            case TEAM_ID_LONG ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_LONG);
            case PLAYER_HAS_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM);
            case PLAYER_TEAM_CREATION_COOLDOWN ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_CREATION_ERROR_ON_COOLDOWN);
        }
    }

}