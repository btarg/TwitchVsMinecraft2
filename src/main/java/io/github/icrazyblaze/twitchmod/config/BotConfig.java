package io.github.icrazyblaze.twitchmod.config;

import java.util.List;

/**
 * Commonly referenced public values are stored here.
 *
 * @see ConfigManager
 */
public class BotConfig {

    public static String TWITCH_KEY = null;
    public static String DISCORD_TOKEN = null;
    public static String CHANNEL_NAME = null;
    public static List<? extends String> DISCORD_CHANNELS;
    public static boolean showChatMessages = false;
    public static boolean showCommandsInChat = false;
    public static String prefix = "!";
    public static boolean requireBits = false;
    public static int minimumBitsAmount = 10;

}