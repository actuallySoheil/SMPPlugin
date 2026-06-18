package me.actuallysoheil.plugin.smp.command.subcommand.smp;

import lombok.RequiredArgsConstructor;
import me.actuallysoheil.plugin.smp.command.api.SubCommand;
import me.actuallysoheil.plugin.smp.command.api.SubExecutor;
import me.actuallysoheil.plugin.smp.manager.PluginSettingsManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "reload", description = "Reload config.")
public final class SMPReloadSubcommand extends SubExecutor {

    private final @NotNull PluginSettingsManager pluginSettingsManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        this.pluginSettingsManager.reloadPluginSettings();
        player.sendRichMessage("<green>Plugin settings have been reloaded successfully.");
    }

}