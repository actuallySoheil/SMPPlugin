package me.actuallysoheil.plugin.smp.model.language.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class PlaceholderLike {

    private final @NotNull List<Placeholder> placeholders;

    private PlaceholderLike() {
        this.placeholders = new ArrayList<>();
    }

    public static @NotNull PlaceholderLike builder() {
        return new PlaceholderLike();
    }

    public @NotNull PlaceholderLike append(@NotNull String id, @NotNull Object value) {
        this.placeholders.add(Placeholder.of(id, value));
        return this;
    }

    public @NotNull PlaceholderLike append(@NotNull PlaceholderLike placeholderLike) {
        this.placeholders.addAll(placeholderLike.asPlaceholders());
        return this;
    }

    public @NotNull PlaceholderLike append(@NotNull Placeholder placeholder) {
        this.placeholders.add(placeholder);
        return this;
    }

    public @NotNull PlaceholderLike append(@NotNull Placeholder... placeholders) {
        this.placeholders.addAll(Arrays.stream(placeholders).toList());
        return this;
    }

    public @NotNull Collection<Placeholder> asPlaceholders() {
        return this.placeholders;
    }

}