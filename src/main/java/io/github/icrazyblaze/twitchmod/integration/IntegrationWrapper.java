package io.github.icrazyblaze.twitchmod.integration;

import io.github.icrazyblaze.twitchmod.Main;
import net.minecraftforge.fml.ModList;

public class IntegrationWrapper {

    public static void initModDynamicCommands(String sender) {

        if (ModList.get().isLoaded("carrierbees")) {
            Main.carrierBeesProxy.get();
            CarrierBeesIntegration.initDynamicCommands(sender);
        }

    }

}
