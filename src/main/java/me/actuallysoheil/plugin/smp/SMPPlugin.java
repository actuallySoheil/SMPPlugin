package me.actuallysoheil.plugin.smp;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import me.actuallysoheil.plugin.smp.command.LanguageCommand;
import me.actuallysoheil.plugin.smp.command.SMPCommand;
import me.actuallysoheil.plugin.smp.command.SettingsCommand;
import me.actuallysoheil.plugin.smp.command.TeamCommand;
import me.actuallysoheil.plugin.smp.database.dao.AccountDao;
import me.actuallysoheil.plugin.smp.database.dao.TeamDao;
import me.actuallysoheil.plugin.smp.database.dao.TeamOptionsDao;
import me.actuallysoheil.plugin.smp.listener.ChatListener;
import me.actuallysoheil.plugin.smp.listener.PlayerListener;
import me.actuallysoheil.plugin.smp.manager.AccountManager;
import me.actuallysoheil.plugin.smp.manager.DatabaseManager;
import me.actuallysoheil.plugin.smp.manager.LanguageManager;
import me.actuallysoheil.plugin.smp.manager.PluginSettingsManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamInvitationManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamOptionsManager;
import me.actuallysoheil.plugin.smp.manager.team.TeamTagManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Accessors(fluent = true)
@Getter
public final class SMPPlugin extends JavaPlugin {

    @Getter
    private static SMPPlugin instance;

    private PluginSettingsManager pluginSettingsManager;
    private DatabaseManager databaseManager;

    private LanguageManager languageManager;

    private AccountDao accountDao;
    private AccountManager accountManager;

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

        this.accountDao = new AccountDao(this.databaseManager);
        this.accountManager = new AccountManager(this.accountDao);

        this.teamDao = new TeamDao(this.databaseManager);
        this.teamOptionsDao = new TeamOptionsDao(this.databaseManager);

        this.teamTagManager = new TeamTagManager(pluginSettings);
        this.teamManager = new TeamManager(pluginSettings, this.teamDao, this.teamTagManager);
        this.teamManager.loadTeamsFromDatabase();
        this.teamInvitationManager = new TeamInvitationManager(
                pluginSettings, this.accountManager, this.teamDao, this.teamTagManager, this.teamManager
        );
        this.teamOptionsManager = new TeamOptionsManager(
                pluginSettings, this.teamTagManager, this.teamManager, this.teamOptionsDao
        );

        registerListeners(
                new PlayerListener(this.languageManager, this.accountManager, this.teamManager),
                new ChatListener(pluginSettings)
        );
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.teamInvitationManager.removeActivePendingInvites();
        this.teamManager.unloadTeams();
        this.teamTagManager.unregisterScoreboardTeams();

        this.accountManager.unloadAccounts();

        this.languageManager.unloadLanguages();

        this.databaseManager.close();
    }

    private void registerCommands() {
        new SMPCommand(this);
        new TeamCommand(this);
        new LanguageCommand(this.languageManager);
        new SettingsCommand(this);
    }

    private void registerListeners(@NotNull Listener... listeners) {
        val pluginManager = getServer().getPluginManager();
        Arrays.stream(listeners)
                .forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

}