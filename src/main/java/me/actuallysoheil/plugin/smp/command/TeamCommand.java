package me.actuallysoheil.plugin.smp.command;

import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.command.api.CommandParent;
import me.actuallysoheil.plugin.smp.command.subcommand.team.*;
import org.jetbrains.annotations.NotNull;

public final class TeamCommand extends CommandParent {

    public TeamCommand(@NotNull SMPPlugin plugin) {
        super("team");

        val teamManager = plugin.teamManager();
        val teamInvitationManager = plugin.teamInvitationManager();
        val teamOptionsManager = plugin.teamOptionsManager();

        registerSubCommands(
                new TeamCreateSubcommand(teamManager),
                new TeamDisbandSubcommand(teamManager),
                new TeamOptionsSubcommand(teamManager, teamOptionsManager),
                new TeamHomeSubcommand(teamManager),
                new TeamKickMemberSubcommand(teamManager),
                new TeamLeaveSubcommand(teamManager),
                new TeamTransferSubcommand(teamManager),
                new TeamInviteSubcommand(teamManager, teamInvitationManager),
                new TeamAcceptInvitationSubcommand(teamInvitationManager)
        );
    }

}