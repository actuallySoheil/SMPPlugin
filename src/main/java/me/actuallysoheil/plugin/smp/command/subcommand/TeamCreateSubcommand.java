package me.actuallysoheil.plugin.smp.command.subcommand;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "create", description = "Create a team.")
public final class TeamCreateSubcommand extends SubExecutor {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team create <name>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamManager.createTeam(teamId, player.getUniqueId())) {
            case SUCCESSFUL -> player.sendRichMessage("<green>Team '" + teamId + "' created successfully.");
            case TEAM_ID_EXISTS -> player.sendRichMessage("<red>Team by this name already exists. Use another name.");
            case TEAM_ID_INVALID ->
                    player.sendRichMessage("<red>Team name can only have characters, numbers and underscore.");
            case TEAM_ID_LONG -> player.sendRichMessage("<red>Team name is too long!");
            case PLAYER_HAS_TEAM -> player.sendRichMessage("<red>You already have a team!");
            case PLAYER_TEAM_CREATION_COOLDOWN ->
                    player.sendRichMessage("<red>You can only create teams every 60 seconds.");
        }
    }

}