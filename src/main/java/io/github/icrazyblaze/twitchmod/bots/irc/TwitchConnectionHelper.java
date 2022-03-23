package io.github.icrazyblaze.twitchmod.bots.irc;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.chat.enums.TMIConnectionState;
import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.util.files.SecretFileHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * This class is responsible for connecting the IRC bot, which is defined by the TwitchBot class.
 * The tryConnect method will either connect the bot or reconnect if it is already connected.
 */

public class TwitchConnectionHelper {

    private static Thread botThread = null;
    private static TwitchChat twitchClient = null;

    public static TwitchChat getBot() {
        return twitchClient;
    }

    public static boolean login() {

        // Update settings before connecting
        SecretFileHelper.setValuesFromFiles();

        if (BotConfig.TWITCH_KEY.isEmpty()) {
            return false;
        }


        if (isConnected()) {

            // Reconnect if already connected
            CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.reconnecting").withStyle(ChatFormatting.DARK_PURPLE));
            try {
                twitchClient.reconnect();
            } catch (Exception e) {
                Main.logger.error(e);
                return false;
            }
            return true;

        } else {
            CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.connecting_to", BotConfig.getTwitchChannelName()).withStyle(ChatFormatting.DARK_PURPLE));
        }

        try {

            // Twitch4J setup
            OAuth2Credential credential = new OAuth2Credential("twitch", BotConfig.TWITCH_KEY);
            twitchClient = TwitchChatBuilder.builder()
                    .withChatAccount(credential)
                    .withDefaultEventHandler(SimpleEventHandler.class)
                    .build();


            botThread = new Thread(() -> {

                TwitchBot bot = new TwitchBot(twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class));
                twitchClient.joinChannel(BotConfig.getTwitchChannelName());
                twitchClient.connect();

            });

            botThread.start();
            return true;

        } catch (Exception e) {
            Main.logger.error(e);
            CommandHandlers.broadcastMessage(new TranslatableComponent("exception.twitchmod.connection_error", e));
            return false;
        }
    }

    public static boolean isConnected() {

        if (twitchClient != null) {
            return twitchClient.getConnectionState().equals(TMIConnectionState.CONNECTED);
        } else {
            return false;
        }

    }

    public static void disconnectBot() {

        twitchClient.disconnect();
        botThread.interrupt();

        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.disconnected_success_twitch").withStyle(ChatFormatting.DARK_RED));

    }

}