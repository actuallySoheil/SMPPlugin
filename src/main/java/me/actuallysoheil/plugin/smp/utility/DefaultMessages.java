package me.actuallysoheil.plugin.smp.utility;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor
@Getter
public enum DefaultMessages {

    TEAM_DISBAND("""
            \s
            <green>Team <white><bold>></bold> <yellow>Team leader has disbanded the team.
            \s
            """
    ),
    TEAM_MEMBER_KICK("""
            \s
            <green>Team <white><bold>></bold> <yellow>Team leader has kicked <aqua><player_name></aqua> from the team.
            \s
            """
    ),
    TEAM_MEMBER_TRANSFER("""
            \s
            <green>Team <white><bold>></bold> <green><old_leader><yellow> has transferred the party leader to <aqua><new_leader></aqua>.
            \s
            """
    ),
    TEAM_MEMBER_LEAVE("""
            \s
            <green>Team <white><bold>></bold> <aqua><player_name><yellow> has left the team.
            \s
            """
    ),
    TEAM_INVITE("""
            \s
            <aqua><team_leader_name> <yellow>has invited <green><player_name></green> to join the team.
            \s
            """
    ),
    TEAM_INVITE_TARGET("""
            \s
            <aqua><team_leader_name> <yellow>has invited you to join their team!
            <yellow>You have <red>60 <yellow>seconds to accept. \
            <gold><hover:show_text:'<gold>Click to accept'>\
            <click:run_command:'/team accept <team_id>'><gold>Click here to join!</click>
            \s
            """
    ),
    TEAM_MEMBER_JOIN("""
            \s
            <green>Team <white><bold>></bold> <aqua><player_name><yellow> has joined the team.
            \s
            """
    ),
    TEAM_INVITE_EXPIRED(
            "<red>Your invitation from <yellow><team_leader_name></yellow> has expired."
    ),
    TEAM_INVITE_TARGET_EXPIRED(
            "<red>Your invite request for <yellow><player_name></yellow> has expired."
    );

    private final @NonNull String text;

    public @NotNull Component message(@NotNull TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(this.text, placeholders);
    }

}