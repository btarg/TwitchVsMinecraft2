package com.icrazyblaze.twitchmod.discord;

import com.icrazyblaze.twitchmod.BotCommands;
import com.icrazyblaze.twitchmod.util.BotConfig;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class DiscordConnection {

    private static Thread botThread = null;
    private static DiscordBot listener = null;

    public static void login() {

        if (BotConfig.DISCORD_TOKEN.isEmpty()) {
            BotCommands.broadcastMessage(new StringTextComponent(TextFormatting.RED + "No Bot Token provided. Use /discord token to set the token."));
            return;
        }

        listener = new DiscordBot();

        botThread = new Thread(() -> {

            try {
                listener.startDiscordBot();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        botThread.start();
    }

    public static void disconnectDiscord() {

        listener.jda.shutdown();
        botThread.interrupt();

        BotCommands.broadcastMessage(new StringTextComponent(TextFormatting.DARK_RED + "Bot disconnected."));

    }
}
