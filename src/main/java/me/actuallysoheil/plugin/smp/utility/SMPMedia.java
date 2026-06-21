package me.actuallysoheil.plugin.smp.utility;

import lombok.experimental.UtilityClass;
import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.manager.LanguageManager;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.LanguageValue;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@UtilityClass
public final class SMPMedia {

    private final @NotNull LanguageManager languageManager = SMPPlugin.instance().languageManager();

    public void sendMessage(@NotNull Player player, @NotNull LanguagePath languagePath) {
        val languageValue = findValueByPath(player.getUniqueId(), languagePath);
        player.sendMessage(languageValue.asComponent());
    }

    public void sendMessage(@NotNull Player player,
                            @NotNull LanguagePath languagePath,
                            @NotNull PlaceholderLike placeholderLike) {
        val languageValue = findValueByPath(player.getUniqueId(), languagePath);
        player.sendMessage(languageValue.asParsedComponent(placeholderLike));
    }

    public @NotNull LanguageValue findValueByPath(@NotNull UUID playerId,
                                                  @NotNull LanguagePath languagePath) {
        return languageManager.findLanguageByPlayerId(playerId)
                .findValueByPath(languagePath);
    }

}