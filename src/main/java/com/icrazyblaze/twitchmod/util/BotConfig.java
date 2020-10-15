package com.icrazyblaze.twitchmod.util;

public class BotConfig {

    public static String TWITCH_KEY = null;
    public static String DISCORD_TOKEN = null;
    public static String CHANNEL_NAME = null;
    public static boolean showChatMessages = false;
    public static boolean showCommands = false;
    public static String prefix = "!";

    private static String username = null;

    public static String getUsername() {

        try {
            if (username.isEmpty()) {
                username = PlayerHelper.getDefaultPlayer().getName().getString();
            }

            return username;

        } catch (Exception e) {
            return null;
        }

    }

    public static void setUsername(String newname) {
        username = newname;
    }

}