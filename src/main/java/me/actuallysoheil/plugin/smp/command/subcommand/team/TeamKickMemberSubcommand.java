package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

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
            case PLAYER_LACKING_TEAM -> player.sendRichMessage("<red>You don't have a team.");
            case PLAYER_NOT_LEADER -> player.sendRichMessage("<red>Only team leader can kick members.");
            case TARGET_NOT_IN_TEAM -> player.sendRichMessage("<red>Target is not in the team!");
            case TARGET_IS_SELF -> player.sendRichMessage("<red>You cannot kick yourself!");
        }
    }

}