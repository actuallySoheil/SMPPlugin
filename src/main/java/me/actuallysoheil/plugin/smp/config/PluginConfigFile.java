package me.actuallysoheil.plugin.smp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class PluginConfigFile {

    private static final @NotNull SMPPlugin PLUGIN = SMPPlugin.instance();
    @Getter
    private final @NotNull File file;

    @Getter
    private FileConfiguration config;

    public PluginConfigFile(@NotNull String fileName) {
        this(PLUGIN.getDataFolder(), fileName);
    }

    public PluginConfigFile(@NotNull File parent, @NotNull String childName) {
        this(new File(parent, childName + ".yml"));
    }

    @SneakyThrows
    public void createFile() {
        if (exists()) return;
        if (this.file.createNewFile()) return;

        PLUGIN.getLogger().severe(
                "[ERROR] Failed to create file '%s'. Disabling plugin..."
                        .formatted(this.file.getName())
        );
        PLUGIN.disablePlugin();
    }

    public void load() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void set(@NotNull String path, @Nullable Object value) {
        this.config.set(path, value);
    }

    @SneakyThrows
    public void save() {
        if (!exists()) return;
        this.config.save(this.file);
    }

    public boolean exists() {
        return this.file.exists();
    }

}