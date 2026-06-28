package me.actuallysoheil.plugin.smp.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true)
@Data
public final class PluginSettings {

    // === Database Settings ===
    private String databaseUri;
    private String databaseName;

    // === Team Settings ===
    private String allowedTeamIdRegex;
    private int maxTeamIdLength;
    private int maxTeamTagLength;
    private int maxTeamMember;
    private int teamCreationCooldownTimeSeconds;
    private int teamInviteExpirationTimeSeconds;
    private int teamHomeTeleportCooldownTimeSeconds;
    private List<String> blacklistedTeamHomeWorlds;

}