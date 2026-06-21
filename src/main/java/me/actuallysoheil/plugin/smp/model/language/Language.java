package me.actuallysoheil.plugin.smp.model.language;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;

@Accessors(fluent = true)
public final class Language {

    @Getter
    private final @NotNull String id;
    @Getter
    private final @NotNull String displayName;
    @Getter
    private final @NotNull FileConfiguration config;
    private final @NotNull HashMap<LanguagePath, LanguageValue> languageValuesMap;

    @Getter
    @Setter
    private boolean defaultLanguage;

    public Language(@NotNull String id,
                    @NotNull String displayName,
                    @NotNull FileConfiguration config) {
        this.id = id;
        this.displayName = displayName;
        this.config = config;

        this.languageValuesMap = new HashMap<>();
    }

    public void clearConfigCache() {
        this.languageValuesMap.clear();
    }

    public void loadConfigCache() {
        Arrays.stream(LanguagePath.values()).forEach(this::addConfigCache);
    }

    private void addConfigCache(@NotNull LanguagePath languagePath) {
        val languageConfigValue = this.config.get(languagePath.languagePath());
        if (languageConfigValue == null) return;

        this.languageValuesMap.put(languagePath, new LanguageValue(languageConfigValue));
    }

    public @NotNull LanguageValue findValueByPath(@Nullable LanguagePath languagePath) {
        return this.languageValuesMap.getOrDefault(languagePath, LanguageValue.NOT_FOUND_VALUE);
    }

}