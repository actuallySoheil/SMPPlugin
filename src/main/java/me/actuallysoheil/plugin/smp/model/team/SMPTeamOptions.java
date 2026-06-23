package me.actuallysoheil.plugin.smp.model.team;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.utility.StringUtility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Accessors(fluent = true)
@Getter
@Setter
@EqualsAndHashCode(of = "smpTeam")
@ToString(of = {"tagName", "tagColor", "friendlyFire", "chatMuted"})
public final class SMPTeamOptions {

    private final @NotNull SMPTeam smpTeam;

    private @Nullable Location homeLocation;

    private @NotNull String tagName;
    private @UnknownNullability NamedTextColor tagColor = NamedTextColor.DARK_GRAY;

    private boolean friendlyFire;
    private boolean chatMuted;

    private Team scoreboardTeam;

    public SMPTeamOptions(@NotNull SMPTeam smpTeam) {
        this.smpTeam = smpTeam;
    }

    public @NotNull String tagColorId() {
        return "<" + this.tagColor + ">";
    }

    public @NotNull String tagColorDisplayName() {
        return StringUtility.formatTagColorName(this.tagColor.toString());
    }

    @FunctionalInterface
    public interface Builder {

        void apply(@NotNull SMPTeamOptions teamOptions);

    }

}