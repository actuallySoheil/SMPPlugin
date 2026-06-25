package me.actuallysoheil.plugin.smp.command;

import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.command.api.CommandParent;
import me.actuallysoheil.plugin.smp.command.subcommand.smp.SMPReloadSubcommand;
import org.jetbrains.annotations.NotNull;

public final class SMPCommand extends CommandParent {

    public SMPCommand(@NotNull SMPPlugin plugin) {
        super("smp", "smp.command.smp");

        registerSubCommands(
                new SMPReloadSubcommand(plugin.pluginSettingsManager(), plugin.languageManager())
        );
    }

}