package com.icrazyblaze.twitchmod.irc;

import com.icrazyblaze.twitchmod.BotCommands;

public class BotConfig {

    public static String TWITCH_KEY = null;
    public static String CHANNEL_NAME = null;
    public static boolean showChatMessages = false;
    public static boolean showCommands = false;
    public static String prefix = "!";

    private static String username = null;

    public static String getUsername() {

        try {
            if (username.isEmpty()) {
                username = BotCommands.getDefaultPlayer().getName().getString();
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