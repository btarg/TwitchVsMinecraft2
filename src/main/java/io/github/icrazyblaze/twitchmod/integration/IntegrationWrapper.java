package io.github.icrazyblaze.twitchmod.integration;


public class IntegrationWrapper {

    public static void initModDynamicCommands(String sender) {

        CarrierBeesIntegration.initDynamicCommands(sender);

    }
    public static void initModCommands() {

        ChanceCubesIntegration.initCommands();
        MimicModIntegration.initCommands();

    }

}
