package me.actuallysoheil.plugin.smp.command.subcommand.team;

import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import me.actuallysoheil.plugin.smp.manager.TeamOptionsManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.Placeholder;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.model.team.SMPTeamOptions;
import me.actuallysoheil.plugin.smp.model.team.status.TeamChangeOptionsStatus;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import me.actuallysoheil.plugin.smp.utility.StringUtility;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static me.actuallysoheil.plugin.smp.model.language.LanguagePath.*;
import static me.actuallysoheil.plugin.smp.model.team.status.TeamChangeOptionsStatus.*;

@SubCommand(label = "options", description = "Adjust team options.")
public final class TeamOptionsSubcommand extends SubExecutor {

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamOptionsManager teamOptionsManager;

    private final @NotNull Map<String, BiConsumer<SMPTeamOptions, String>> optionSetters;
    private final @NotNull Map<String, LanguagePath> broadcastMessages;
    private final @NotNull Map<TeamChangeOptionsStatus, LanguagePath> errorMessages;

    @SuppressWarnings("SpellCheckingInspection")
    public TeamOptionsSubcommand(@NotNull TeamManager teamManager,
                                 @NotNull TeamOptionsManager teamOptionsManager) {
        this.teamManager = teamManager;
        this.teamOptionsManager = teamOptionsManager;

        this.optionSetters = Map.ofEntries(
                Map.entry("tagname", SMPTeamOptions::tagName),
                Map.entry("tagcolor", (options, value) -> options.tagColor(StringUtility.stringToNamedTextColor(value))),
                Map.entry("friendlyfire", (options, value) -> options.friendlyFire(Boolean.parseBoolean(value))),
                Map.entry("chatmuted", (options, value) -> options.chatMuted(Boolean.parseBoolean(value)))
        );

        this.broadcastMessages = Map.ofEntries(
                Map.entry("tagname", BROADCAST_TEAM_OPTION_TAG_NAME_CHANGED),
                Map.entry("tagcolor", BROADCAST_TEAM_OPTION_TAG_COLOR_CHANGED),
                Map.entry("friendlyfire", BROADCAST_TEAM_OPTION_FRIENDLY_FIRE_TOGGLED),
                Map.entry("chatmuted", BROADCAST_TEAM_OPTION_CHAT_TOGGLED)
        );

        this.errorMessages = Map.ofEntries(
                Map.entry(TAG_NAME_INVALID, MESSAGE_COMMAND_ERROR_TEAM_TAG_INVALID),
                Map.entry(TAG_NAME_LONG, MESSAGE_COMMAND_ERROR_TEAM_TAG_LONG),
                Map.entry(TAG_COLOR_INVALID, MESSAGE_COMMAND_ERROR_TEAM_TAG_COLOR_INVALID),
                Map.entry(PLAYER_LACKING_TEAM, MESSAGE_COMMAND_ERROR_TEAM_PLAYER_LACKING_TEAM),
                Map.entry(PLAYER_NOT_LEADER, MESSAGE_COMMAND_ERROR_TEAM_PLAYER_NOT_LEADER),
                Map.entry(UNKNOWN_ERROR, MESSAGE_COMMAND_ERROR_TEAM_OPTION_UNKNOWN_ERROR)
        );
    }

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length <= 1) {
            sendUsageMessage(player);
            return;
        }

        val optionId = arguments[0].toLowerCase();
        val optionValue = arguments[1];
        if (!this.optionSetters.containsKey(optionId)) {
            sendUsageMessage(player);
            return;
        }

        val playerId = player.getUniqueId();
        val optionSetter = this.optionSetters.get(optionId);
        val status = this.teamOptionsManager.changeTeamOptions(
                playerId,
                teamOptions -> optionSetter.accept(teamOptions, optionValue)
        );

        if (status.equals(SUCCESSFUL)) {
            broadcastSuccess(playerId, optionId, optionValue);
            return;
        }

        val errorMessage = this.errorMessages.get(status);
        if (errorMessage != null) SMPMedia.sendMessage(player, errorMessage);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void broadcastSuccess(@NotNull UUID playerId,
                                  @NotNull String optionId, @NotNull String optionValue) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return;

        val enabledText = SMPMedia.findValueByPath(playerId, BROADCAST_TEAM_OPTION_ENABLED).asText();
        val disabledText = SMPMedia.findValueByPath(playerId, BROADCAST_TEAM_OPTION_DISABLED).asText();

        val broadcastMessage = this.broadcastMessages.get(optionId);
        val placeholder = switch (optionId) {
            case "tagname" -> Placeholder.of("team_tag_name", optionValue);
            case "tagcolor" -> Placeholder.of(
                    "team_tag_color_name",
                    playerTeam.teamOptions().tagColorId() + playerTeam.teamOptions().tagColorDisplayName()
            );
            case "friendlyfire" -> Placeholder.of(
                    "team_friendly_fire_status",
                    playerTeam.teamOptions().friendlyFire() ? enabledText : disabledText
            );
            case "chatmuted" -> Placeholder.of(
                    "team_chat_muted_status",
                    playerTeam.teamOptions().chatMuted() ? disabledText : enabledText
            );
            default -> null;
        };

        if (broadcastMessage != null && placeholder != null) playerTeam.sendLocalizedMessage(
                broadcastMessage,
                PlaceholderLike.builder().append(placeholder)
        );
    }

    private void sendUsageMessage(@NotNull Player player) {
        val separatorFormat = SMPMedia.findValueByPath(
                player.getUniqueId(),
                MESSAGE_COMMAND_TEAM_OPTIONS_USAGE_SEPARATOR_FORMAT
        ).asText();
        SMPMedia.sendMessage(
                player,
                MESSAGE_COMMAND_TEAM_OPTIONS_USAGE,
                PlaceholderLike.builder()
                        .append(Placeholder.of(
                                "options",
                                String.join(separatorFormat, this.optionSetters.keySet())
                        ))
        );
    }

}