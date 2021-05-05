package io.github.icrazyblaze.twitchmod.config;


import com.google.common.collect.Lists;
import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.FrenzyVote;
import io.github.icrazyblaze.twitchmod.util.SecretFileHelper;
import io.github.icrazyblaze.twitchmod.util.TimerSystem;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigManager {

    static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec.ConfigValue<String> TWITCH_CHANNEL_NAME;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISCORD_CHANNELS;
    static final ForgeConfigSpec.BooleanValue SHOW_COMMANDS_IN_CHAT;
    static final ForgeConfigSpec.ConfigValue<Integer> CHOOSE_COMMAND_DELAY;
    static final ForgeConfigSpec.ConfigValue<Integer> CHOOSE_MESSAGE_DELAY;
    static final ForgeConfigSpec.ConfigValue<String> MINECRAFT_USERNAME;
    static final ForgeConfigSpec.ConfigValue<String> COMMAND_PREFIX;
    static final ForgeConfigSpec.BooleanValue ENABLE_COOLDOWN;
    static final ForgeConfigSpec.BooleanValue ENABLE_FRENZY;
    static final ForgeConfigSpec.ConfigValue<Integer> VOTES_NEEDED;
    static final ForgeConfigSpec.ConfigValue<Integer> BOOK_LENGTH;
    static final ForgeConfigSpec.BooleanValue SHOW_CHAT_MESSAGES;
    public static ForgeConfigSpec COMMON_CONFIG;

    static {

        COMMON_BUILDER.push("general");
        TWITCH_CHANNEL_NAME = COMMON_BUILDER.comment("Name of Twitch channel").define("twitch_channel_name", "channel");
        DISCORD_CHANNELS = COMMON_BUILDER.comment("Names of Discord channels to read commands from ['separated', 'like', 'this']").define("discord_channels", Lists.newArrayList("general"));

        SHOW_CHAT_MESSAGES = COMMON_BUILDER.comment("Should chat messages from Twitch or Discord be show in-game?").define("show_chat_messages", false);
        SHOW_COMMANDS_IN_CHAT = COMMON_BUILDER.comment("Should chosen commands be shown if chat messages are enabled?").define("show_commands_in_chat", false);
        CHOOSE_COMMAND_DELAY = COMMON_BUILDER.comment("How many seconds until the next command is chosen").define("choose_command_delay", 20);
        CHOOSE_MESSAGE_DELAY = COMMON_BUILDER.comment("How many seconds until a random viewer-written message is shown on screen").define("choose_message_delay", 240);

        MINECRAFT_USERNAME = COMMON_BUILDER.comment("The player's Minecraft username").define("minecraft_username", "name");
        COMMAND_PREFIX = COMMON_BUILDER.comment("The prefix for commands in Twitch or Discord").define("command_prefix", "!");

        ENABLE_COOLDOWN = COMMON_BUILDER.comment("Prevent the same command from being executed twice in a row").define("enable_cooldown", false);
        ENABLE_FRENZY = COMMON_BUILDER.comment("Allow Frenzy Mode").define("enable_frenzy", true);

        VOTES_NEEDED = COMMON_BUILDER.comment("How many votes are needed to activate certain commands").define("votes_needed", 3);
        BOOK_LENGTH = COMMON_BUILDER.comment("How many messages should be included when chat writes a book").define("book_length", 10);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();

    }

    public static void updateFromConfig() {

        SecretFileHelper.setValuesFromFiles();

        // From common config file
        BotConfig.DISCORD_CHANNELS = DISCORD_CHANNELS.get();
        BotConfig.CHANNEL_NAME = TWITCH_CHANNEL_NAME.get();
        BotConfig.showChatMessages = SHOW_CHAT_MESSAGES.get();
        BotConfig.showCommandsInChat = SHOW_COMMANDS_IN_CHAT.get();
        BotConfig.prefix = COMMAND_PREFIX.get();
        BotConfig.setUsername(MINECRAFT_USERNAME.get());
        TimerSystem.chatSecondsTrigger = CHOOSE_COMMAND_DELAY.get();
        TimerSystem.chatSeconds = CHOOSE_COMMAND_DELAY.get();
        TimerSystem.messageSecondsTrigger = CHOOSE_MESSAGE_DELAY.get();
        TimerSystem.messageSeconds = CHOOSE_MESSAGE_DELAY.get();
        CommandHandlers.enableFrenzyMode = ENABLE_FRENZY.get();
        FrenzyVote.votesNeeded = VOTES_NEEDED.get();
        ChatPicker.chatLogLength = BOOK_LENGTH.get();
        ChatPicker.cooldownEnabled = ENABLE_COOLDOWN.get();

    }

}
