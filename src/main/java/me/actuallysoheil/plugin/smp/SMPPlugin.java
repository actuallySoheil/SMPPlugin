package me.actuallysoheil.plugin.smp;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.LanguageCommand;
import me.actuallysoheil.plugin.smp.command.SMPCommand;
import me.actuallysoheil.plugin.smp.command.TeamCommand;
import me.actuallysoheil.plugin.smp.database.dao.TeamDao;
import me.actuallysoheil.plugin.smp.database.dao.TeamOptionsDao;
import me.actuallysoheil.plugin.smp.listener.PlayerListener;
import me.actuallysoheil.plugin.smp.manager.*;
import me.actuallysoheil.plugin.smp.manager.team.TeamInvitationManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamOptionsManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamTagManager;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@Getter
public final class SMPPlugin extends JavaPlugin {

    @Getter
    private static SMPPlugin instance;

    private PluginSettingsManager pluginSettingsManager;
    private DatabaseManager databaseManager;

    private LanguageManager languageManager;

    private TeamDao teamDao;
    private TeamOptionsDao teamOptionsDao;

    private TeamTagManager teamTagManager;
    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;
    private TeamOptionsManager teamOptionsManager;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginSettingsManager = new PluginSettingsManager(this);
        this.pluginSettingsManager.reloadPluginSettings();

        val pluginSettings = this.pluginSettingsManager.pluginSettings();
        this.databaseManager = new DatabaseManager(pluginSettings);

        this.languageManager = new LanguageManager(this);
        this.languageManager.reloadLanguages();

        this.teamDao = new TeamDao(this.databaseManager);
        this.teamOptionsDao = new TeamOptionsDao(this.databaseManager);

        this.teamTagManager = new TeamTagManager();
        this.teamManager = new TeamManager(pluginSettings, this.teamDao, this.teamTagManager);
        this.teamManager.loadTeamsFromDatabase();
        this.teamInvitationManager = new TeamInvitationManager(pluginSettings, this.teamDao, this.teamTagManager, this.teamManager);
        this.teamOptionsManager = new TeamOptionsManager(pluginSettings, this.teamTagManager, this.teamManager, this.teamOptionsDao);

        getServer().getPluginManager().registerEvents(
                new PlayerListener(this.languageManager, this.teamManager), this
        );
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.databaseManager.close();

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