package io.github.icrazyblaze.twitchmod.bots.discord;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

public class DiscordConnectionHelper {

    private static Thread botThread = null;
    private static DiscordBot listener = null;

    public static DiscordBot getListener() {
        return listener;
    }

    public static void login() {

        if (BotConfig.DISCORD_TOKEN.isEmpty()) {
            CommandHandlers.broadcastMessage(new TranslatableComponent("exception.twitchmod.no_discord_token").withStyle(ChatFormatting.RED));
            return;
        }

        listener = new DiscordBot();

        botThread = new Thread(() -> {

            try {
                listener.startDiscordBot();
            } catch (Exception e) {
                Main.logger.error(e);
            }

        });

        botThread.start();
    }

    public static void disconnectDiscord() {

        listener.jda.shutdown();
        botThread.interrupt();
        listener = null;

        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.disconnected_success_discord").withStyle(ChatFormatting.DARK_RED));

    }

    public static boolean isConnected() {

        if (listener != null) {
            return listener.isConnected;
        } else {
            return false;
        }
    }

}
