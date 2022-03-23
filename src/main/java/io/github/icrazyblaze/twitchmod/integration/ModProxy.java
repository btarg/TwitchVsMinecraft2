package io.github.icrazyblaze.twitchmod.integration;

import io.github.icrazyblaze.twitchmod.integration.mods.ChanceCubesIntegration;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class ModProxy {

    public static Optional<ChanceCubesIntegration> chanceCubesProxy;

    public static void initModProxies() {

        chanceCubesProxy = ModList.get().isLoaded("chancecubes") ? Optional.of(new ChanceCubesIntegration()) : Optional.empty();

    }
}
