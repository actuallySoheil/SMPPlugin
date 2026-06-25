package me.actuallysoheil.plugin.smp;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.LanguageCommand;
import me.actuallysoheil.plugin.smp.command.SMPCommand;
import me.actuallysoheil.plugin.smp.command.TeamCommand;
import me.actuallysoheil.plugin.smp.listener.PlayerListener;
import me.actuallysoheil.plugin.smp.manager.*;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@Getter
public final class SMPPlugin extends JavaPlugin {

    @Getter
    private static SMPPlugin instance;

    private PluginSettingsManager pluginSettingsManager;
    private LanguageManager languageManager;

    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;
    private TeamOptionsManager teamOptionsManager;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginSettingsManager = new PluginSettingsManager(this);
        this.pluginSettingsManager.reloadPluginSettings();

        this.languageManager = new LanguageManager(this);
        this.languageManager.reloadLanguages();

        val pluginSettings = this.pluginSettingsManager.pluginSettings();
        this.teamManager = new TeamManager(pluginSettings);
        this.teamInvitationManager = new TeamInvitationManager(pluginSettings, this.teamManager);
        this.teamOptionsManager = new TeamOptionsManager(pluginSettings, this.teamManager);

        getServer().getPluginManager().registerEvents(
                new PlayerListener(this.languageManager), this
        );
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.teamInvitationManager.removeActivePendingInvites();
        this.teamManager.unloadTeams();

        this.languageManager.unloadLanguages();
    }

    private void registerCommands() {
        new SMPCommand(this);
        new TeamCommand(this);
        new LanguageCommand(this.languageManager);
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

}