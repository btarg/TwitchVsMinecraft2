package io.github.icrazyblaze.twitchmod.config;

/**
 * Helper for Bot classes to quickly get commonly referenced values from config
 *
 * @see ConfigManager
 */
public class BotConfig {

    // These values are taken from SUCK files
    public static String TWITCH_KEY = null;
    public static String DISCORD_TOKEN = null;

    public static String getTwitchChannelName() {
        return ConfigManager.TWITCH_CHANNEL_NAME.get();
    }

    public static String getCommandPrefix() {
        return ConfigManager.COMMAND_PREFIX.get();
    }


}