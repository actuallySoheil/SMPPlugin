package me.actuallysoheil.plugin.smp.command;

import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.command.api.CommandParent;
import me.actuallysoheil.plugin.smp.command.subcommand.settings.SettingsToggleTeamInvitesSubcommand;
import org.jetbrains.annotations.NotNull;

public final class SettingsCommand extends CommandParent {

    public SettingsCommand(@NotNull SMPPlugin plugin) {
        super("settings");

        registerSubCommands(
                new SettingsToggleTeamInvitesSubcommand(
                        plugin.accountDao(), plugin.accountManager()
                )
        );
    }

}