package me.actuallysoheil.plugin.smp.model.team;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.utility.StringUtility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
@Getter
@Setter
@EqualsAndHashCode(of = "smpTeam")
public final class SMPTeamSettings {

    private final @NotNull SMPTeam smpTeam;

    private @Nullable Location homeLocation;

    private @NotNull String tagName;
    private @NotNull NamedTextColor tagColor = NamedTextColor.DARK_GRAY;

    private boolean friendlyFire;
    private boolean chatMuted;

    private Team scoreboardTeam;

    SMPTeamSettings(@NotNull SMPTeam smpTeam) {
        this.smpTeam = smpTeam;
    }

    public @NotNull String tagColorId() {
        return "<" + this.tagColor + ">";
    }

    public @NotNull String tagColorDisplayName() {
        return StringUtility.formatTagColorName(this.tagColor.toString());
    }

}