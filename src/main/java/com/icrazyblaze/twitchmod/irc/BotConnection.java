package com.icrazyblaze.twitchmod.irc;

import com.icrazyblaze.twitchmod.BotCommands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;


public class BotConnection {

    public static PircBotX bot = null;
    private static Thread botThread = null;

    public static void tryConnect() {

        BotCommands.player().sendMessage(new StringTextComponent(TextFormatting.DARK_GREEN + "Connecting..."));

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
                    e.printStackTrace();
                }

            });

            botThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            BotCommands.player().sendMessage(new StringTextComponent(TextFormatting.RED + "Could not connect: " + e.toString()));
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
