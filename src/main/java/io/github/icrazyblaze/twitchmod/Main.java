package io.github.icrazyblaze.twitchmod;

import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.FrenzyVote;
import io.github.icrazyblaze.twitchmod.config.ConfigHelper;
import io.github.icrazyblaze.twitchmod.network.PacketHandler;
import io.github.icrazyblaze.twitchmod.util.BotConfig;
import io.github.icrazyblaze.twitchmod.util.ForgeEventSubscriber;
import io.github.icrazyblaze.twitchmod.util.Reference;
import io.github.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Commoble, iCrazyBlaze
 */
@Mod(Reference.MOD_ID)
public final class Main {

    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);
    public static ConfigImplementation config;

    public Main() {

        // Get rid of that annoying log4j:WARN message
        BasicConfigurator.configure();

        // Instantiate and subscribe our config instance
        config = ConfigHelper.register(ModConfig.Type.SERVER, ConfigImplementation::new);

        // Initialise system property
        System.setProperty("twitch_oauth_key", "");
        System.setProperty("discord_bot_token", "");

        // Register event subscribers
        MinecraftForge.EVENT_BUS.register(ForgeEventSubscriber.class);
        MinecraftForge.EVENT_BUS.register(TickHandler.class);
        MinecraftForge.EVENT_BUS.register(CommandHandlers.class);

        // Register network messages
        PacketHandler.registerMessages();

        ChatPicker.loadBlacklistFile();

    }


    public static void updateConfig() {

        BotConfig.TWITCH_KEY = System.getProperty("twitch_oauth_key");
        BotConfig.DISCORD_TOKEN = System.getProperty("discord_bot_token");

        // Set config values from server config file
        BotConfig.CHANNEL_NAME = config.channelProp.get();
        BotConfig.showChatMessages = config.showMessagesProp.get();
        BotConfig.showCommands = config.showCommandsProp.get();
        BotConfig.prefix = config.prefixProp.get();
        BotConfig.setUsername(config.usernameProp.get());

        TickHandler.messageSecondsTrigger = config.messageSecondsProp.get();
        TickHandler.messageSeconds = config.messageSecondsProp.get();

        TickHandler.chatSecondsTrigger = config.chatSecondsProp.get();
        TickHandler.chatSeconds = config.chatSecondsProp.get();

        CommandHandlers.enableFrenzyMode = config.frenzyProp.get();
        FrenzyVote.votesNeeded = config.votesProp.get();

        ChatPicker.chatLogLength = config.chatLogProp.get();

        ChatPicker.initCommands();

    }


    public static class ConfigImplementation {

        public final ConfigHelper.ConfigValueListener<String> channelProp;
        public final ConfigHelper.ConfigValueListener<Boolean> showMessagesProp;
        public final ConfigHelper.ConfigValueListener<Boolean> showCommandsProp;
        public final ConfigHelper.ConfigValueListener<Integer> chatSecondsProp;
        public final ConfigHelper.ConfigValueListener<Integer> messageSecondsProp;

        public final ConfigHelper.ConfigValueListener<String> usernameProp;
        public final ConfigHelper.ConfigValueListener<String> prefixProp;

        public final ConfigHelper.ConfigValueListener<Boolean> cooldownProp;
        public final ConfigHelper.ConfigValueListener<Boolean> frenzyProp;
        public final ConfigHelper.ConfigValueListener<Integer> votesProp;

        public final ConfigHelper.ConfigValueListener<Integer> chatLogProp;

        ConfigImplementation(final ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {
            builder.push("general");

            this.channelProp = subscriber.subscribe(builder
                    .comment("Name of Twitch channel")
                    .define("channelName", ""));
            this.showMessagesProp = subscriber.subscribe(builder
                    .comment("Should chat messages be shown?")
                    .define("showMessages", false));

            this.showCommandsProp = subscriber.subscribe(builder
                    .comment("Should chosen commands be shown if chat messages are enabled?")
                    .define("showCommands", true));
            this.chatSecondsProp = subscriber.subscribe(builder
                    .comment("How many seconds until the next command is chosen")
                    .define("chatSeconds", 20));
            this.messageSecondsProp = subscriber.subscribe(builder
                    .comment("How many seconds until a random viewer-written message is shown on screen")
                    .define("messageSeconds", 240));

            this.usernameProp = subscriber.subscribe(builder
                    .comment("The streamer's Minecraft username")
                    .define("username", ""));
            this.prefixProp = subscriber.subscribe(builder
                    .comment("The prefix for commands")
                    .define("prefix", "!"));

            this.cooldownProp = subscriber.subscribe(builder
                    .comment("Prevent the same command from being executed twice in a row")
                    .define("cooldownEnabled", true));

            this.frenzyProp = subscriber.subscribe(builder
                    .comment("Enable Frenzy Mode")
                    .define("frenzyModeEnabled", true));

            this.votesProp = subscriber.subscribe(builder
                    .comment("How many votes are needed to activate certain commands")
                    .define("votesNeeded", 3));

            this.chatLogProp = subscriber.subscribe(builder
                    .comment("How many messages should be included when chat writes a book")
                    .define("chatLogLength", 10));

            builder.pop();

        }

    }

}