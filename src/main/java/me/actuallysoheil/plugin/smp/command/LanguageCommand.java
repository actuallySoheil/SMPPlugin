package me.actuallysoheil.plugin.smp.command;

import lombok.val;
import me.actuallysoheil.plugin.smp.command.api.Command;
import me.actuallysoheil.plugin.smp.manager.LanguageManager;
import me.actuallysoheil.plugin.smp.model.language.Language;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.Placeholder;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public final class LanguageCommand extends Command {

    private final @NotNull LanguageManager languageManager;

    private final @NotNull List<String> cachedLanguageIds;

    public LanguageCommand(@NotNull LanguageManager languageManager) {
        super("language");
        this.languageManager = languageManager;

        this.cachedLanguageIds = this.languageManager.languages().stream()
                .map(Language::id)
                .toList();
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 0) {
            val separatorFormat = SMPMedia.findValueByPath(
                    player.getUniqueId(),
                    LanguagePath.MESSAGE_COMMAND_LANGUAGE_USAGE_SEPARATOR_FORMAT
            ).asText();
            SMPMedia.sendMessage(
                    player,
                    LanguagePath.MESSAGE_COMMAND_LANGUAGE_USAGE,
                    PlaceholderLike.builder()
                            .append(Placeholder.of(
                                    "languages",
                                    String.join(separatorFormat, this.cachedLanguageIds)
                            ))
            );
            return;
        }

        val languageId = arguments[0].toLowerCase();
        val language = this.languageManager.findLanguageById(languageId);
        if (language == null) {
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_LANGUAGE_ERROR_NOT_FOUND);
            return;
        }

        if (this.languageManager.updatePlayerLanguage(player.getUniqueId(), language))
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_LANGUAGE_PLAYER_UPDATED);
        else
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_LANGUAGE_ERROR_ALREADY_SELECTED);
    }

    @Override
    public Collection<String> completions(@NotNull Player player, @NonNull @NotNull String[] arguments) {
        if (arguments.length == 0) return this.cachedLanguageIds;
        if (arguments.length == 1) return this.cachedLanguageIds.stream()
                .filter(name -> name.toLowerCase().startsWith(arguments[arguments.length - 1].toLowerCase()))
                .toList();
        return List.of();
    }

}