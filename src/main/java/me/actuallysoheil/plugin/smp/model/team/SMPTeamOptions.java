package me.actuallysoheil.plugin.smp.model.team;

import lombok.*;
import lombok.experimental.Accessors;
import me.actuallysoheil.plugin.smp.utility.StringUtility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Accessors(fluent = true)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(of = {"tagName", "tagColor", "friendlyFire", "chatMuted", "homeLocation"})
public final class SMPTeamOptions {

    private transient @NotNull SMPTeam smpTeam;

    private @UnknownNullability String tagName = Strings.EMPTY;
    private @UnknownNullability NamedTextColor tagColor = NamedTextColor.AQUA;

    private boolean friendlyFire;
    private boolean chatMuted;

    private @Nullable Location homeLocation;

    public SMPTeamOptions(@NotNull SMPTeam smpTeam) {
        this.smpTeam = smpTeam;
        this.tagName = smpTeam.teamId();
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