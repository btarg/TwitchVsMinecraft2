package io.github.icrazyblaze.twitchmod.bots.irc;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;

/**
 * This class is responsible for connecting the IRC bot, which is defined by the TwitchBot class.
 * The tryConnect method will either connect the bot or reconnect if it is already connected.
 */

public class TwitchConnectionHelper {

    private static PircBotX bot = null;
    private static Thread botThread = null;

    public static PircBotX getBot() {
        return bot;
    }

    public static boolean login() {

        // Update settings before connecting
        ConfigManager.updateFromConfig();

        if (BotConfig.TWITCH_KEY.isEmpty()) {
            return false;
        }

        if (isConnected()) {

            // Disconnect before connecting again
            disconnectBot();
            CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.reconnecting").withStyle(ChatFormatting.DARK_PURPLE));

        } else {
            CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.connecting_to", BotConfig.CHANNEL_NAME).withStyle(ChatFormatting.DARK_PURPLE));
        }

        try {

            Configuration config = new Configuration.Builder()
                    .setAutoReconnect(true)
                    .setAutoNickChange(false) // Twitch doesn't support multiple users
                    .setOnJoinWhoEnabled(false) // Twitch doesn't support WHO command
                    .setCapEnabled(true)
                    .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                    .addCapHandler(new EnableCapHandler("twitch.tv/tags"))
                    .setName("MinecraftBot")
                    .addServer("irc.twitch.tv", 6667)
                    .setServerPassword(BotConfig.TWITCH_KEY)
                    .addAutoJoinChannel("#" + BotConfig.CHANNEL_NAME)
                    .addListener(new TwitchBot())
                    .buildConfiguration();

            bot = new PircBotX(config);

            botThread = new Thread(() -> {

                try {
                    bot.startBot();
                } catch (Exception e) {
                    Main.logger.error(e);
                }

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

        if (bot != null) {
            return bot.isConnected();
        } else {
            return false;
        }

    }

    public static void disconnectBot() {

        bot.stopBotReconnect();
        bot.close();
        botThread.interrupt();

    }

}