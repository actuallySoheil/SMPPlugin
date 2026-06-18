package me.actuallysoheil.plugin.smp.manager;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.config.PluginConfigFile;
import me.actuallysoheil.plugin.smp.config.PluginSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@Accessors(fluent = true)
public final class PluginSettingsManager {

    private final @NotNull SMPPlugin plugin;

    private final @NotNull ConfigManager configManager;
    private final @NotNull PluginConfigFile settingsConfigFile;

    @Getter
    private final @NotNull PluginSettings pluginSettings;

    public PluginSettingsManager(@NotNull SMPPlugin plugin,
                                 @NotNull ConfigManager configManager) {
        this.plugin = plugin;

        this.configManager = configManager;
        this.settingsConfigFile = configManager.settingsConfigFile();

        this.pluginSettings = new PluginSettings();
    }

    public void reloadPluginSettings() {
        val settingsFile = new File(this.plugin.getDataFolder(), "settings.yml");
        if (!settingsFile.exists()) {
            try {
                this.configManager.copyFromResource(settingsFile, "settings.yml");
            } catch (@NotNull IOException exception) {
                this.plugin.getLogger().severe(
                        "[Language] [ERROR] Failed to copy default settings file. Disabling plugin..."
                );
                this.plugin.disablePlugin();
                return;
            }
        }

        this.settingsConfigFile.load();
        val config = this.settingsConfigFile.config();

        val allowedTeamIdRegex = config.getString("settings.team.allowed-id-regex", "^[a-zA-Z0-9_]+$");
        val maxTeamIdLength = config.getInt("settings.team.id-max-length", 12);
        val teamCreationCooldownTimeSeconds = config.getInt("settings.team.creation-cooldown-time-seconds", 60);
        val teamInviteExpirationTimeSeconds = config.getInt("settings.team.invite-expiration-time-seconds", 60);
        val maxTeamMembers = config.getInt("settings.team.max-members", 6);

        this.pluginSettings.allowedTeamIdRegex(allowedTeamIdRegex);
        this.pluginSettings.maxTeamIdLength(maxTeamIdLength);
        this.pluginSettings.teamCreationCooldownTimeSeconds(teamCreationCooldownTimeSeconds);
        this.pluginSettings.teamInviteExpirationTimeSeconds(teamInviteExpirationTimeSeconds);
        this.pluginSettings.maxTeamMember(maxTeamMembers);
    }

}