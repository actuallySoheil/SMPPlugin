package me.actuallysoheil.plugin.smp.model.audience;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SMPAudience implements ForwardingAudience {

    private final @NotNull Collection<? extends Player> players;

    public static @NotNull SMPAudience of(@NotNull Collection<? extends Player> players) {
        return new SMPAudience(players);
    }

    public void sendLocalizedMessage(@NotNull LanguagePath languagePath) {
        this.players.forEach(player -> {
            val prefix = SMPMedia.findValueByPath(player.getUniqueId(), LanguagePath.BROADCAST_TEAM_GENERAL_PREFIX).asText();
            SMPMedia.sendMessage(
                    player,
                    languagePath,
                    PlaceholderLike.builder().append("team_broadcast_prefix", prefix)
            );
        });
    }

    public void sendLocalizedMessage(@NotNull LanguagePath languagePath,
                                     @NotNull PlaceholderLike placeholderLike) {
        this.players.forEach(player -> {
            val prefix = SMPMedia.findValueByPath(player.getUniqueId(), LanguagePath.BROADCAST_TEAM_GENERAL_PREFIX).asText();
            SMPMedia.sendMessage(
                    player,
                    languagePath,
                    PlaceholderLike.builder()
                            .append("team_broadcast_prefix", prefix)
                            .append(placeholderLike)
            );
        });
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.players;
    }

}