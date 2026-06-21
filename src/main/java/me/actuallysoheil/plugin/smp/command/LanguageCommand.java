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

import java.util.stream.Collectors;

public final class LanguageCommand extends Command {

    private final @NotNull LanguageManager languageManager;

    public LanguageCommand(@NotNull LanguageManager languageManager) {
        super("language");
        this.languageManager = languageManager;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 0) {
            val separatorFormat = SMPMedia.findValueByPath(
                    player.getUniqueId(),
                    LanguagePath.MESSAGE_COMMAND_LANGUAGE_USAGE_SEPARATOR_FORMAT
            ).asText();
            val languages = this.languageManager.languages().stream()
                    .map(Language::id)
                    .collect(Collectors.joining(separatorFormat));
            SMPMedia.sendMessage(
                    player,
                    LanguagePath.MESSAGE_COMMAND_LANGUAGE_USAGE,
                    PlaceholderLike.builder().append(Placeholder.of("languages", languages))
            );
            return;
        }

        val languageId = arguments[0].toLowerCase();
        val language = this.languageManager.findLanguageById(languageId);
        if (language == null) {
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_ERROR_LANGUAGE_NOT_FOUND);
            return;
        }

        if (this.languageManager.updatePlayerLanguage(player.getUniqueId(), language))
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_LANGUAGE_PLAYER_UPDATED);
        else
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_ERROR_LANGUAGE_ALREADY_SELECTED);
    }

}