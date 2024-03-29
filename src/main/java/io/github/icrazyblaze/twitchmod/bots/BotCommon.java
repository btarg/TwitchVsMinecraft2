package io.github.icrazyblaze.twitchmod.bots;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.bots.discord.DiscordConnectionHelper;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class BotCommon {

    public static void sendBotMessage(String message) {

        if (DiscordConnectionHelper.isConnected()) {

            JDA jda = DiscordConnectionHelper.getListener().jda;

            for (String ch : ConfigManager.DISCORD_CHANNELS.get()) {
                List<TextChannel> channels = jda.getTextChannelsByName(ch, true);
                for (TextChannel chan : channels) {
                    if (chan.canTalk()) {
                        try {
                            chan.sendMessage(message).queue();
                        } catch (Exception e) {
                            Main.logger.error("Could not send message to Discord: " + e);
                        }
                    }
                }
            }
        }

        if (TwitchConnectionHelper.isConnected()) {
            try {
                TwitchConnectionHelper.getBot().sendMessage(BotConfig.getTwitchChannelName(), message);
            } catch (Exception e) {
                Main.logger.error("Could not send message to Twitch: " + e);
            }
        }
    }
}
