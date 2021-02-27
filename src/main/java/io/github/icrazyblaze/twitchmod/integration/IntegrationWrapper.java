package io.github.icrazyblaze.twitchmod.integration;

import net.minecraftforge.fml.ModList;

public class IntegrationWrapper {

    public static void initModDynamicCommands(String sender) {

        if (ModList.get().isLoaded("carrierbees")) {
            CarrierBeesIntegration.initDynamicCommands(sender);
        }

    }

}
