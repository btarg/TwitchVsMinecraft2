package io.github.icrazyblaze.twitchmod.bots.discord;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class DiscordConnectionHelper {

    private static Thread botThread = null;
    private static DiscordBot listener = null;

    public static void login() {

        if (BotConfig.DISCORD_TOKEN.isEmpty()) {
            CommandHandlers.broadcastMessage(new StringTextComponent(TextFormatting.RED + "No Bot Token provided. Use /discord token to set the token."));
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
        listener = null;

        CommandHandlers.broadcastMessage(new StringTextComponent(TextFormatting.DARK_RED + "Bot disconnected."));

    }

    public static boolean isConnected() {

        if (listener != null) {
            return listener.isConnected;
        } else {
            return false;
        }
    }

}
