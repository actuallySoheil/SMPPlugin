package me.actuallysoheil.plugin.smp.command.subcommand.smp;

import lombok.RequiredArgsConstructor;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubCommandHandler;
import me.actuallysoheil.plugin.smp.manager.LanguageManager;
import me.actuallysoheil.plugin.smp.manager.PluginSettingsManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamTagManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "reload", description = "Reload config.")
public final class SMPReloadSubcommand extends SubCommandHandler {

    private final @NotNull PluginSettingsManager pluginSettingsManager;
    private final @NotNull LanguageManager languageManager;
    private final @NotNull TeamTagManager teamTagManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        this.pluginSettingsManager.reloadPluginSettings();
        this.languageManager.reloadLanguages();
        this.teamTagManager.updateDefaultScoreboardTeamColor();
        player.sendRichMessage("<green>Plugin settings and languages have been reloaded successfully.");
    }

}