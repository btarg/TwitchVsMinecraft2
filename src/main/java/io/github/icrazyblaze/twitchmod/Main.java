package io.github.icrazyblaze.twitchmod;

import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.FrenzyVote;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.network.PacketHandler;
import io.github.icrazyblaze.twitchmod.util.ForgeEventSubscriber;
import io.github.icrazyblaze.twitchmod.util.Reference;
import io.github.icrazyblaze.twitchmod.util.TimerSystem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author iCrazyBlaze
 */
@Mod(Reference.MOD_ID)
public final class Main {

    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public Main() {

        // Get rid of that annoying log4j:WARN message
        BasicConfigurator.configure();

        // Instantiate and subscribe our config instance
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);

        // Initialise system property
        System.setProperty("twitch_oauth_key", "");
        System.setProperty("discord_bot_token", "");

        // Register event subscribers
        MinecraftForge.EVENT_BUS.register(ForgeEventSubscriber.class);
        MinecraftForge.EVENT_BUS.register(TimerSystem.class);
        MinecraftForge.EVENT_BUS.register(CommandHandlers.class);

        // Register network messages
        PacketHandler.registerMessages();

    }


    public static void updateConfig() {

        // System properties
        BotConfig.TWITCH_KEY = System.getProperty("twitch_oauth_key");
        BotConfig.DISCORD_TOKEN = System.getProperty("discord_bot_token");

        // From common config file
        BotConfig.DISCORD_CHANNELS = ConfigManager.getDiscordChannels();
        BotConfig.CHANNEL_NAME = ConfigManager.getTwitch_channel_name();
        BotConfig.showChatMessages = ConfigManager.isShow_chat_messages();
        BotConfig.showCommandsInChat = ConfigManager.isShow_commands_in_chat();
        BotConfig.prefix = ConfigManager.getCommand_prefix();
        BotConfig.setUsername(ConfigManager.getMinecraft_username());
        TimerSystem.chatSecondsTrigger = ConfigManager.getChoose_command_delay();
        TimerSystem.chatSeconds = ConfigManager.getChoose_command_delay();
        TimerSystem.messageSecondsTrigger = ConfigManager.getChoose_message_delay();
        TimerSystem.messageSeconds = ConfigManager.getChoose_message_delay();
        CommandHandlers.enableFrenzyMode = ConfigManager.isEnable_frenzy();
        FrenzyVote.votesNeeded = ConfigManager.getVotes_needed();
        ChatPicker.chatLogLength = ConfigManager.getBook_length();
        ChatPicker.cooldownEnabled = ConfigManager.isEnable_cooldown();

    }


}
