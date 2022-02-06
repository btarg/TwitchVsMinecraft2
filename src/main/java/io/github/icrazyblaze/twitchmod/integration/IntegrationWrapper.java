package io.github.icrazyblaze.twitchmod.integration;


import io.github.icrazyblaze.twitchmod.integration.mods.ChanceCubesIntegration;

public class IntegrationWrapper {

    public static void initModDynamicCommands(String sender) {

    }

    public static void initModCommands() {

        ChanceCubesIntegration.initCommands();

    }

}
