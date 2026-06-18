package me.actuallysoheil.plugin.smp;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.command.TeamCommand;
import me.actuallysoheil.plugin.smp.manager.TeamInvitationManager;
import me.actuallysoheil.plugin.smp.manager.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@Getter
public final class SMPPlugin extends JavaPlugin {

    @Getter
    private static SMPPlugin instance;

    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;

    @Override
    public void onEnable() {
        instance = this;

        this.teamManager = new TeamManager();
        this.teamInvitationManager = new TeamInvitationManager(this.teamManager);

        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void registerCommands() {
        new TeamCommand(this);
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

}