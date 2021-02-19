package io.github.icrazyblaze.twitchmod.integration;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import net.minecraftforge.fml.ModList;
import noobanidus.mods.carrierbees.init.ModEntities;

import static io.github.icrazyblaze.twitchmod.chat.ChatPicker.registerCommand;

public class CarrierBeesIntegration {

    public static boolean isCarrierBeesLoaded() {
        return ModList.get().isLoaded("carrierbees");
    }

    public static void initDynamicCommands(String sender) {

        if (!isCarrierBeesLoaded())
            return;

        registerCommand(() -> CommandHandlers.spawnCarrierBee(sender, ModEntities.CARRIER_BEE.get()), "carrierbee", "bee");
        registerCommand(() -> CommandHandlers.spawnCarrierBee(sender, ModEntities.BOMBLE_BEE.get()), "bomblebee", "bee2");
        registerCommand(() -> CommandHandlers.spawnCarrierBee(sender, ModEntities.FUMBLE_BEE.get()), "fumblebee", "bee3");
    }

}
