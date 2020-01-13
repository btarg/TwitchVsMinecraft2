package com.icrazyblaze.twitchmod;

import com.icrazyblaze.twitchmod.config.ConfigHolder;
import com.icrazyblaze.twitchmod.util.Reference;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Cadiboo, iCrazyBlaze
 */
@Mod(Reference.MOD_ID)
public final class Main {

	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

	public Main() {
		final ModLoadingContext modLoadingContext = ModLoadingContext.get();
		// Register Configs
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
		//modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
	}

}
