package io.github.icrazyblaze.twitchmod.integration;

import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class ModProxy {
    public static Optional<CarrierBeesIntegration> carrierBeesProxy;

    public static void initModProxies() {
        carrierBeesProxy = ModList.get().isLoaded("carrierbees") ? Optional.of(new CarrierBeesIntegration()) : Optional.empty();
    }
}
