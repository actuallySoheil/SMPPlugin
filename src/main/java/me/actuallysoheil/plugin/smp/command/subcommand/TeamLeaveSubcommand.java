package me.actuallysoheil.plugin.smp.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "leave", description = "Leave the team.")
public final class TeamLeaveSubcommand extends SubExecutor {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        switch (this.teamManager.leaveTeam(player.getUniqueId())) {
            case PLAYER_LACKING_TEAM -> player.sendRichMessage("<red>You don't have a team!");
            case PLAYER_IS_LEADER -> player.sendRichMessage(
                    "<red>Team leader cannot leave the team. Use <gold>/team disband</gold> or <gold>/team transfer</gold> to proceed."
            );
        }
    }

}