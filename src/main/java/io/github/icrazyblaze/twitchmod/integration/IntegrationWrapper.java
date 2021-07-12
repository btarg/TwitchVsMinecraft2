package io.github.icrazyblaze.twitchmod.integration;


import io.github.icrazyblaze.twitchmod.integration.mods.CarrierBeesIntegration;
import io.github.icrazyblaze.twitchmod.integration.mods.ChanceCubesIntegration;
import io.github.icrazyblaze.twitchmod.integration.mods.MimicModIntegration;

public class IntegrationWrapper {

    public static void initModDynamicCommands(String sender) {

        CarrierBeesIntegration.initDynamicCommands(sender);

    }

    public static void initModCommands() {

        ChanceCubesIntegration.initCommands();
        MimicModIntegration.initCommands();

    }

}
