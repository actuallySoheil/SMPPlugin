package me.actuallysoheil.plugin.smp.manager;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.config.PluginConfigFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Accessors(fluent = true)
public final class ConfigManager {

    private final @NotNull SMPPlugin plugin;

    @Getter
    private final @NotNull PluginConfigFile settingsConfigFile;

    public ConfigManager(@NotNull SMPPlugin plugin) {
        this.plugin = plugin;

        createPluginDataFolder();

        this.settingsConfigFile = new PluginConfigFile("settings");
    }

    private void createPluginDataFolder() {
        val pluginDataFolder = this.plugin.getDataFolder();
        if (pluginDataFolder.exists()) return;
        if (pluginDataFolder.mkdir()) return;

        this.plugin.getLogger().severe(
                "[Config] [ERROR] Failed to create plugin data folder. Disabling plugin..."
        );
        this.plugin.disablePlugin();
    }

    public void copyFromResource(@NotNull File toBePlaced,
                                 @NotNull String fileName) throws IOException {
        val inputStream = this.plugin.getResource(fileName);
        if (inputStream == null) return;

        Files.copy(inputStream, toBePlaced.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}