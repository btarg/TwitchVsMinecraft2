package com.icrazyblaze.twitchmod;

import com.icrazyblaze.twitchmod.chat.ChatPicker;
import com.icrazyblaze.twitchmod.config.ConfigHelper;
import com.icrazyblaze.twitchmod.irc.BotConfig;
import com.icrazyblaze.twitchmod.network.PacketHandler;
import com.icrazyblaze.twitchmod.util.ForgeEventSubscriber;
import com.icrazyblaze.twitchmod.util.Reference;
import com.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

        // Instantiate and subscribe our config instance
        config = ConfigHelper.register(ModConfig.Type.SERVER, ConfigImplementation::new);

        // Initialise system property
        System.setProperty("twitch_oauth_key", "");

        // Register event subscribers
        MinecraftForge.EVENT_BUS.register(ForgeEventSubscriber.class);
        MinecraftForge.EVENT_BUS.register(TickHandler.class);
        MinecraftForge.EVENT_BUS.register(BotCommands.class);

        // Register network messages
        PacketHandler.registerMessages();

        ChatPicker.loadBlacklistFile();

    }


    public static void updateConfig() {

        BotConfig.TWITCH_KEY = System.getProperty("twitch_oauth_key");

        // Set config values from server config file
        BotConfig.CHANNEL_NAME = config.channelProp.get();
        BotConfig.showChatMessages = config.showMessagesProp.get();
        BotConfig.showCommands = config.showCommandsProp.get();
        BotConfig.prefix = config.prefixProp.get();
        BotConfig.setUsername(config.usernameProp.get());

        TickHandler.messageSecondsDefault = config.messageSecondsProp.get();
        TickHandler.messageSeconds = config.messageSecondsProp.get();

        TickHandler.chatSecondsDefault = config.chatSecondsProp.get();
        TickHandler.chatSeconds = config.chatSecondsProp.get();

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

        ConfigImplementation(final ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {
            builder.push("general");

            this.channelProp = subscriber.subscribe(builder
                    .comment("Name of Twitch channel")
                    .translation(Reference.MOD_ID + ".config.channelName")
                    .define("channelName", ""));
            this.showMessagesProp = subscriber.subscribe(builder
                    .comment("Should chat messages be shown")
                    .translation(Reference.MOD_ID + ".config.showMessages")
                    .define("showMessages", false));

            this.showCommandsProp = subscriber.subscribe(builder
                    .comment("Should chosen commands be shown if chat messages are enabled")
                    .translation(Reference.MOD_ID + ".config.showCommands")
                    .define("showCommands", true));
            this.chatSecondsProp = subscriber.subscribe(builder
                    .comment("How many seconds until the next command is chosen")
                    .translation(Reference.MOD_ID + ".config.chatSeconds")
                    .define("chatSeconds", 20));
            this.messageSecondsProp = subscriber.subscribe(builder
                    .comment("How many seconds until a random viewer-written message is shown on screen")
                    .translation(Reference.MOD_ID + ".config.messageSeconds")
                    .define("messageSeconds", 300));

            this.usernameProp = subscriber.subscribe(builder
                    .comment("The streamer's Minecraft username")
                    .translation(Reference.MOD_ID + ".config.username")
                    .define("username", ""));
            this.prefixProp = subscriber.subscribe(builder
                    .comment("The prefix for commands")
                    .translation(Reference.MOD_ID + ".config.prefix")
                    .define("prefix", "!"));

            this.cooldownProp = subscriber.subscribe(builder
                    .comment("Prevent the same command from being executed twice in a row)")
                    .translation(Reference.MOD_ID + ".config.cooldownEnabled")
                    .define("cooldownEnabled", false));

            builder.pop();

        }

    }

}