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
@SubCommand(label = "invite", description = "Invite a player.")
public final class TeamInviteSubcommand extends SubExecutor {

    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team invite <player>");
            return;
        }

        val targetPlayerName = arguments[0];
        switch (this.teamInvitationManager.invitePlayer(player.getUniqueId(), targetPlayerName)) {
            case PLAYER_LACKING_TEAM -> player.sendRichMessage("<red>You don't have a team!");
            case PLAYER_NOT_LEADER -> player.sendRichMessage("<red>Only team leader can invite players.");
            case TEAM_ON_CAPACITY ->
                    player.sendRichMessage("<red>Cannot invite a new member. Team has reached the maximum amount of members.");
            case TARGET_OFFLINE -> player.sendRichMessage("<red>Target player is offline!");
            case TARGET_HAS_TEAM -> player.sendRichMessage("<red>Target is already in another team!");
            case TARGET_IS_SELF -> player.sendRichMessage("<red>You cannot invite yourself!");
            case TARGET_ALREADY_INVITED ->
                    player.sendRichMessage("<red>You've already invited this person. Wait for them to accept the team invitation.");
        }
    }

}