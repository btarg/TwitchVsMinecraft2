package io.github.icrazyblaze.twitchmod;

import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.integration.ModProxy;
import io.github.icrazyblaze.twitchmod.network.PacketHandler;
import io.github.icrazyblaze.twitchmod.util.ForgeEventSubscriber;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.icrazyblaze.twitchmod.config.ConfigManager.COMMON_CONFIG;

/**
 * @author RonaRage, AKA Btarg (https://github.com/iCrazyBlaze)
 */
@Mod(Main.MOD_ID)
public final class Main {

    public static final String MOD_ID = "twitchmod";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public Main() {

        // Instantiate and subscribe our config instance
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);

        // Register event subscribers
        MinecraftForge.EVENT_BUS.register(ForgeEventSubscriber.class);
        MinecraftForge.EVENT_BUS.register(TimerSystem.class);
        MinecraftForge.EVENT_BUS.register(CommandHandlers.class);

        // Mod bus events
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        // Register network messages
        PacketHandler.registerMessages();

        // Initialise mod integration proxies
        ModProxy.initModProxies();

    }

    @SubscribeEvent
    public static void configLoaded(ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_CONFIG) {
            ConfigManager.updateFromConfig();
        }
    }


}
