package me.actuallysoheil.plugin.smp.command.subcommand;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamInvitationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "accept", description = "Accept a team invitation.")
public final class TeamAcceptInvitationSubcommand extends SubExecutor {

    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team accept <teamName>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamInvitationManager.acceptInvite(teamId, player.getUniqueId())) {
            case PLAYER_HAS_TEAM ->
                    player.sendRichMessage("<red>Leave your current team to accept this team invitation.");
            case PLAYER_LACKING_INVITE -> player.sendRichMessage("<red>You don't have an invitation from this team.");
            case TEAM_INVALID -> player.sendRichMessage("<red>Team by this name doesn't exist!");
            case TEAM_ON_CAPACITY -> player.sendRichMessage("<red>Cannot accept this invitation. Team is full.");
        }
    }

}