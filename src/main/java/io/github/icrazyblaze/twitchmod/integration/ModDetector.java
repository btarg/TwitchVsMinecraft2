package io.github.icrazyblaze.twitchmod.integration;

import net.minecraftforge.fml.ModList;

public class ModDetector {

    public static boolean isCarrierBeesLoaded() {
        return ModList.get().isLoaded("carrierbees");
    }

}
