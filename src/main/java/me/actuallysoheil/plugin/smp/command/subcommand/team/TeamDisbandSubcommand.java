package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "disband", description = "Disband your team.")
public final class TeamDisbandSubcommand extends SubExecutor {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        switch (this.teamManager.disbandTeam(player.getUniqueId())) {
            case PLAYER_LACKING_TEAM -> player.sendRichMessage("<red>You don't own team.");
            case PLAYER_NOT_LEADER -> player.sendRichMessage("<red>Only team leader can disband the team.");
        }
    }

}