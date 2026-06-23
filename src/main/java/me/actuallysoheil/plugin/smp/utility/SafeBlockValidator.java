package me.actuallysoheil.plugin.smp.utility;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class SafeBlockValidator {

    private final @NotNull Material[] UNSAFE_MATERIALS = {
            Material.LAVA,
            Material.WATER,
            Material.FIRE,
            Material.VOID_AIR,
            Material.CAVE_AIR,
            Material.AIR,
            Material.CACTUS,
            Material.SWEET_BERRY_BUSH,
            Material.POWDER_SNOW,
    };

    public boolean isSafeBlock(@NotNull Location location) {
        if (location.getWorld() == null) return false;

        val blockMaterial = location.getBlock().getType();
        for (val unsafeMaterial : UNSAFE_MATERIALS) if (blockMaterial.equals(unsafeMaterial)) return false;

        val blockBelow = location.clone().subtract(0, 1, 0);
        val belowMaterial = blockBelow.getBlock().getType();

        return belowMaterial.isSolid();
    }

}