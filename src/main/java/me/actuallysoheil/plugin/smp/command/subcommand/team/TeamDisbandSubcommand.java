package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@SubCommand(label = "disband", description = "Disband your team.")
public final class TeamDisbandSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_DISBAND_CONFIRMATION);
            return;
        }

        val teamId = arguments[0];
        switch (this.teamManager.disbandTeam(player.getUniqueId(), teamId)) {
            case PLAYER_LACKING_TEAM ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_NOT_LEADER ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER);
            case TEAM_NAME_INVALID ->
                    SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_DISBAND_ERROR_INVALID_TEAM_NAME);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 1) {
            val playerTeam = this.teamManager.findTeamByPlayerId(player.getUniqueId());
            if (playerTeam == null) return Collections.emptyList();

            return List.of(playerTeam.teamId());
        }

        return Collections.emptyList();
    }

}