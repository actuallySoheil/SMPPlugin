package me.actuallysoheil.plugin.smp.command.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class SubExecutor {

    private final SubCommand subCommand;

    public SubExecutor() {
        this.subCommand = getClass().getDeclaredAnnotation(SubCommand.class);
        Objects.requireNonNull(this.subCommand, "Subcommands must have annotation!");
    }

    public abstract void execute(@NotNull Player player, @NotNull String[] arguments);

    public @NotNull Collection<String> completions(@NotNull Player player, @NotNull String[] arguments) {
        return List.of();
    }

    public @NotNull String label() {
        return this.subCommand.label();
    }

    public @NotNull String description() {
        return this.subCommand.description();
    }

}