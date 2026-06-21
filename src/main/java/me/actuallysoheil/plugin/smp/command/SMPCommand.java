package me.actuallysoheil.plugin.smp.command;

import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.command.api.Command;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.command.subcommand.smp.SMPReloadSubcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SMPCommand extends Command {

    public static final @NotNull String SMP_COMMAND_LABEL = "smp";

    private final @NotNull List<SubExecutor> subCommands;
    private final @NotNull String helpMessage;

    public SMPCommand(@NotNull SMPPlugin plugin) {
        super(SMP_COMMAND_LABEL, "smp.command.smp");
        this.subCommands = new ArrayList<>();

        this.subCommands.add(new SMPReloadSubcommand(plugin.pluginSettingsManager(), plugin.languageManager()));

        val stringBuilder = new StringBuilder("<newLine><dark_green>SMP <gray>[v")
                .append(plugin.getPluginMeta().getVersion())
                .append("]").append("<newLine><newLine>");
        this.subCommands.forEach(subCommand -> stringBuilder.append("<green>/")
                .append(getLabel())
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

}