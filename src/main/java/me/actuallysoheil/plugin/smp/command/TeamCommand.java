package me.actuallysoheil.plugin.smp.command;

import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.command.api.Command;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.command.subcommand.team.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class TeamCommand extends Command {

    private final @NotNull List<SubExecutor> subCommands;
    private final @NotNull String helpMessage;

    public TeamCommand(@NotNull SMPPlugin plugin) {
        super("team");
        this.subCommands = new ArrayList<>();

        val teamManager = plugin.teamManager();
        val teamInvitationManager = plugin.teamInvitationManager();
        val teamOptionsManager = plugin.teamOptionsManager();

        this.subCommands.add(new TeamCreateSubcommand(teamManager));
        this.subCommands.add(new TeamDisbandSubcommand(teamManager));
        this.subCommands.add(new TeamOptionsSubcommand(teamManager, teamOptionsManager));
        this.subCommands.add(new TeamHomeSubcommand(teamManager));
        this.subCommands.add(new TeamKickMemberSubcommand(teamManager));
        this.subCommands.add(new TeamLeaveSubcommand(teamManager));
        this.subCommands.add(new TeamTransferSubcommand(teamManager));
        this.subCommands.add(new TeamInviteSubcommand(teamManager, teamInvitationManager));
        this.subCommands.add(new TeamAcceptInvitationSubcommand(teamInvitationManager));

        val stringBuilder = new StringBuilder("<newLine><dark_green>SMP Teams <gray>[v")
                .append(plugin.getPluginMeta().getVersion())
                .append("]").append("<newLine><newLine>");
        this.subCommands.forEach(subCommand -> stringBuilder.append("<green>/")
                .append(label())
                .append(" ")
                .append(subCommand.label())
                .append(" <gray>- <white>")
                .append(subCommand.description())
                .append("<newLine>")
        );
        stringBuilder.append("<newLine>");
        this.helpMessage = stringBuilder.toString();
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage(this.helpMessage);
            return;
        }

        for (val subExecutor : this.subCommands) {
            if (!arguments[0].equalsIgnoreCase(subExecutor.label())) continue;
            subExecutor.execute(
                    player,
                    Arrays.stream(arguments)
                            .skip(1)
                            .toArray(String[]::new)
            );
            return;
        }

        player.sendRichMessage(this.helpMessage);
    }

    @Override
    public Collection<String> completions(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 0) return this.subCommands.stream()
                .map(SubExecutor::label)
                .toList();

        if (arguments.length == 1) return this.subCommands.stream().map(SubExecutor::label)
                .filter(name -> name.toLowerCase().startsWith(arguments[arguments.length - 1].toLowerCase()))
                .toList();

        for (val subExecutor : this.subCommands) {
            if (!arguments[0].equalsIgnoreCase(subExecutor.label())) continue;
            return subExecutor.completions(
                    player,
                    Arrays.stream(arguments)
                            .skip(1)
                            .toArray(String[]::new)
            );
        }

        return List.of();
    }

}