package me.actuallysoheil.plugin.smp.model.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor
@Getter
public enum LanguagePath {

    // === COMMANDS ===
    // General
    MESSAGE_COMMAND_ERROR_GENERAL_NO_PERMISSION("message.command.general.error.no-permission"),
    MESSAGE_COMMAND_ERROR_GENERAL_EXCEPTION("message.command.general.error.exception"),
    MESSAGE_COMMAND_ERROR_GENERAL_PLAYER_OFFLINE("message.command.general.error.player-not-found"),

    // Language
    MESSAGE_COMMAND_LANGUAGE_USAGE("message.command.language.usage"),
    MESSAGE_COMMAND_LANGUAGE_USAGE_SEPARATOR_FORMAT("message.command.language.usage-separator-format"),
    MESSAGE_COMMAND_LANGUAGE_PLAYER_UPDATED("message.command.language.updated"),
    MESSAGE_COMMAND_ERROR_LANGUAGE_ALREADY_SELECTED("message.command.language.error.already-selected"),
    MESSAGE_COMMAND_ERROR_LANGUAGE_NOT_FOUND("message.command.language.error.not-found"),

    // Team
    MESSAGE_COMMAND_TEAM_CREATED("message.command.team.created"),
    MESSAGE_COMMAND_TEAM_DISBAND_CONFIRMATION("message.command.team.disband.confirmation"),
    MESSAGE_COMMAND_TEAM_OPTIONS_USAGE("message.command.team.option.usage"),
    MESSAGE_COMMAND_TEAM_OPTIONS_USAGE_SEPARATOR_FORMAT("message.command.team.option.usage-separator-format"),
    MESSAGE_COMMAND_ERROR_TEAM_DISBAND_INVALID_TEAM_NAME("message.command.team.error.disband.invalid-team-name"),
    MESSAGE_COMMAND_ERROR_TEAM_EXISTS("message.command.team.error.exists"),
    MESSAGE_COMMAND_ERROR_TEAM_NOT_EXISTS("message.command.team.error.not-exists"),
    MESSAGE_COMMAND_ERROR_TEAM_NAME_LONG("message.command.team.error.long-name"),
    MESSAGE_COMMAND_ERROR_TEAM_NAME_INVALID("message.command.team.error.invalid-name"),
    MESSAGE_COMMAND_ERROR_TEAM_TAG_LONG("message.command.team.error.long-tag-name"),
    MESSAGE_COMMAND_ERROR_TEAM_TAG_INVALID("message.command.team.error.invalid-tag-name"),
    MESSAGE_COMMAND_ERROR_TEAM_TAG_COLOR_INVALID("message.command.team.error.invalid-tag-color"),
    MESSAGE_COMMAND_ERROR_TEAM_OPTION_UNKNOWN_ERROR("message.command.team.error.option-unknown-error"),
    MESSAGE_COMMAND_ERROR_TEAM_ON_CAPACITY("message.command.team.error.on-capacity"),
    MESSAGE_COMMAND_ERROR_TEAM_PLAYER_HAS_TEAM("message.command.team.error.player.has-team"),
    MESSAGE_COMMAND_ERROR_TEAM_PLAYER_LACKING_TEAM("message.command.team.error.player.lacking-team"),
    MESSAGE_COMMAND_ERROR_TEAM_PLAYER_NOT_LEADER("message.command.team.error.player.not-leader"),
    MESSAGE_COMMAND_ERROR_TEAM_PLAYER_CREATION_COOLDOWN("message.command.team.error.player.on-creation-cooldown"),
    MESSAGE_COMMAND_ERROR_TEAM_TARGET_IS_SELF("message.command.team.error.target.is-self"),
    MESSAGE_COMMAND_ERROR_TEAM_TARGET_HAS_TEAM("message.command.team.error.target.has-team"),
    MESSAGE_COMMAND_ERROR_TEAM_TARGET_LACKING_TEAM("message.command.team.error.target.lacking-team"),
    MESSAGE_COMMAND_ERROR_TEAM_INVITATION_ALREADY_INVITED("message.command.team.error.invitation.already-invited"),
    MESSAGE_COMMAND_ERROR_TEAM_INVITATION_LACKING("message.command.team.error.invitation.lacking"),
    MESSAGE_COMMAND_ERROR_TEAM_LEADER_CANNOT_LEAVE("message.command.team.error.leader.cannot-leave"),

    // === BROADCASTING ===
    // Team
    BROADCAST_TEAM_GENERAL_PREFIX("broadcast.team.general.prefix"),
    BROADCAST_TEAM_GENERAL_MEMBER_JOIN("broadcast.team.general.member-join"),
    BROADCAST_TEAM_GENERAL_MEMBER_LEAVE("broadcast.team.general.member-leave"),
    BROADCAST_TEAM_DISBAND("broadcast.team.disband"),
    BROADCAST_TEAM_KICK("broadcast.team.kick"),
    BROADCAST_TEAM_TRANSFER("broadcast.team.transfer"),
    BROADCAST_TEAM_INVITATION_MEMBERS("broadcast.team.invitation.members"),
    BROADCAST_TEAM_INVITATION_TARGET("broadcast.team.invitation.target"),
    BROADCAST_TEAM_INVITATION_EXPIRED("broadcast.team.invitation.expired"),
    BROADCAST_TEAM_INVITATION_EXPIRED_TARGET("broadcast.team.invitation.expired-target"),
    BROADCAST_TEAM_OPTION_TAG_NAME_CHANGED("broadcast.team.option.tag-name-changed"),
    BROADCAST_TEAM_OPTION_TAG_COLOR_CHANGED("broadcast.team.option.tag-color-changed"),
    BROADCAST_TEAM_OPTION_FRIENDLY_FIRE_TOGGLED("broadcast.team.option.friendly-fire-toggled"),
    BROADCAST_TEAM_OPTION_CHAT_TOGGLED("broadcast.team.option.chat-toggled"),
    BROADCAST_TEAM_OPTION_ENABLED("broadcast.team.option.enabled"),
    BROADCAST_TEAM_OPTION_DISABLED("broadcast.team.option.disabled"),

    ;

    private final @NotNull String languagePath;

}