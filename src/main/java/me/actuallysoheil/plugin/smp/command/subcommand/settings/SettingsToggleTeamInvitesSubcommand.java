package me.actuallysoheil.plugin.smp.command.subcommand.settings;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.database.dao.AccountDao;
import me.actuallysoheil.plugin.smp.manager.AccountManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@SubCommand(label = "toggleTeamInvites", description = "Toggle team invites.")
public final class SettingsToggleTeamInvitesSubcommand extends SubCommandHandler {

    private final @NotNull AccountDao accountDao;
    private final @NotNull AccountManager accountManager;

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        val playerId = player.getUniqueId();
        val account = this.accountManager.findOnlineById(playerId);
        if (account == null) {
            player.sendRichMessage("<red>Failed to find your account. Please rejoin.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(
                SMPPlugin.instance(),
                () -> {
                    account.teamInvitesDisabled(!account.teamInvitesDisabled());
                    this.accountDao.update(account);
                    val status = SMPMedia.findValueByPath(
                            playerId,
                            account.teamInvitesDisabled() ?
                                    LanguagePath.MESSAGE_GENERAL_STATUS_DISABLED :
                                    LanguagePath.MESSAGE_GENERAL_STATUS_ENABLED
                    ).asText();

                    SMPMedia.sendMessage(
                            player,
                            LanguagePath.MESSAGE_COMMAND_SETTINGS_TEAM_INVITES_TOGGLED,
                            PlaceholderLike.builder().append("status", status)
                    );
                }
        );
    }

}