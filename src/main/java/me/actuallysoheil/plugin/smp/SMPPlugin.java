package me.actuallysoheil.plugin.smp;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.SMPCommand;
import me.actuallysoheil.plugin.smp.command.TeamCommand;
import me.actuallysoheil.plugin.smp.manager.ConfigManager;
import me.actuallysoheil.plugin.smp.manager.PluginSettingsManager;
import me.actuallysoheil.plugin.smp.manager.TeamInvitationManager;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@Getter
public final class SMPPlugin extends JavaPlugin {

    @Getter
    private static SMPPlugin instance;

    private ConfigManager configManager;
    private PluginSettingsManager pluginSettingsManager;

    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.pluginSettingsManager = new PluginSettingsManager(this, this.configManager);
        this.pluginSettingsManager.reloadPluginSettings();

        val pluginSettings = this.pluginSettingsManager.pluginSettings();
        this.teamManager = new TeamManager(pluginSettings);
        this.teamInvitationManager = new TeamInvitationManager(pluginSettings, this.teamManager);

        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerCommands() {
        new SMPCommand(this);
        new TeamCommand(this);
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

}