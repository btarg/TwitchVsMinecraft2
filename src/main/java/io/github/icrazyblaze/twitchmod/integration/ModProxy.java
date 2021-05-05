package io.github.icrazyblaze.twitchmod.integration;

import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class ModProxy {

    public static Optional<CarrierBeesIntegration> carrierBeesProxy;
    public static Optional<ChanceCubesIntegration> chanceCubesProxy;
    public static Optional<MimicModIntegration> mimicModProxy;

    public static void initModProxies() {

        carrierBeesProxy = ModList.get().isLoaded("carrierbees") ? Optional.of(new CarrierBeesIntegration()) : Optional.empty();
        chanceCubesProxy = ModList.get().isLoaded("chancecubes") ? Optional.of(new ChanceCubesIntegration()) : Optional.empty();
        mimicModProxy = ModList.get().isLoaded("mimic") ? Optional.of(new MimicModIntegration()) : Optional.empty();

    }
}
