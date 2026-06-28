package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "chat", description = "Send a message to your teammates.")
public final class TeamChatSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team chat <message>");
            return;
        }

        val playerId = player.getUniqueId();
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) {
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            return;
        }

        if (playerTeam.teamOptions().chatMuted() && !playerTeam.isTeamLeader(playerId)) {
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_CHAT_ERROR_MUTED);
            return;
        }

        val message = String.join(" ", arguments);
        val placeholderLike = PlaceholderLike.builder()
                .append("player_name", player.getName())
                .append("message", message);

        playerTeam.sendLocalizedMessage(LanguagePath.MESSAGE_COMMAND_TEAM_CHAT_FORMAT, placeholderLike);
    }

}