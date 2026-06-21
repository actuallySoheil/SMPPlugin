package me.actuallysoheil.plugin.smp.command.api;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import lombok.val;
import me.actuallysoheil.plugin.smp.SMPPlugin;
import me.actuallysoheil.plugin.smp.model.language.LanguagePath;
import me.actuallysoheil.plugin.smp.model.language.placeholder.Placeholder;
import me.actuallysoheil.plugin.smp.model.language.placeholder.PlaceholderLike;
import me.actuallysoheil.plugin.smp.utility.SMPMedia;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public abstract class Command implements BasicCommand {

    private static final @NotNull SMPPlugin PLUGIN = SMPPlugin.instance();

    @Getter
    private final @NotNull String label;
    @Getter
    private final @Nullable String permission;

    public Command(@NotNull String label,
                   @Nullable String permission,
                   @NotNull Collection<String> aliases) {
        this.label = label;
        this.permission = permission;

        PLUGIN.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                commandEvent -> {
                    val registrar = commandEvent.registrar();
                    if (aliases.isEmpty()) registrar.register(label, this);
                    else registrar.register(label, aliases, this);
                }
        );
    }

    public Command(@NotNull String label, @Nullable String permission) {
        this(label, permission, List.of());
    }

    public Command(@NotNull String label, @NotNull Collection<String> aliases) {
        this(label, null, aliases);
    }

    public Command(@NotNull String label) {
        this(label, List.of());
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] arguments) {
        val commandSender = commandSourceStack.getExecutor();

        if (!(commandSender instanceof Player player)) return;

        if (this.permission != null && !player.hasPermission(this.permission)) {
            SMPMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_ERROR_GENERAL_NO_PERMISSION);
            return;
        }

        try {
            execute(player, arguments);
        } catch (@NotNull Exception exception) {
            SMPMedia.sendMessage(
                    player,
                    LanguagePath.MESSAGE_COMMAND_ERROR_GENERAL_EXCEPTION,
                    PlaceholderLike.builder().append(Placeholder.of("command_label", this.label))
            );
            PLUGIN.getLogger().log(
                    Level.SEVERE,
                    "[ERROR] Failed to execute %s command. Please contact developer to solve this problem:"
                            .formatted(this.label),
                    exception
            );
        }
    }

    public abstract void execute(@NotNull Player player, @NotNull String[] arguments);

}