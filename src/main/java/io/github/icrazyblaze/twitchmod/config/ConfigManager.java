package io.github.icrazyblaze.twitchmod.config;


import com.google.common.collect.Lists;
import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.FrenzyVote;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import io.github.icrazyblaze.twitchmod.util.files.SecretFileHelper;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Reads from and writes to the main configuration file for the mod.
 */
public class ConfigManager {

    static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec.ConfigValue<String> TWITCH_CHANNEL_NAME;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISCORD_CHANNELS;
    static final ForgeConfigSpec.BooleanValue SHOW_COMMANDS_IN_CHAT;
    static final ForgeConfigSpec.ConfigValue<Integer> CHOOSE_COMMAND_DELAY;
    static final ForgeConfigSpec.ConfigValue<Integer> CHOOSE_MESSAGE_DELAY;
    static final ForgeConfigSpec.ConfigValue<List<? extends String>> MINECRAFT_USERNAME;
    static final ForgeConfigSpec.ConfigValue<String> COMMAND_PREFIX;
    static final ForgeConfigSpec.BooleanValue ENABLE_COOLDOWN;
    static final ForgeConfigSpec.BooleanValue ENABLE_FRENZY;

    static final ForgeConfigSpec.BooleanValue REQUIRE_BITS;
    static final ForgeConfigSpec.ConfigValue<Integer> MINIMUM_BITS;

    static final ForgeConfigSpec.ConfigValue<Integer> VOTES_NEEDED;
    static final ForgeConfigSpec.ConfigValue<Integer> BOOK_LENGTH;
    static final ForgeConfigSpec.BooleanValue SHOW_CHAT_MESSAGES;
    public static ForgeConfigSpec COMMON_CONFIG;

    static {

        COMMON_BUILDER.push("general");
        TWITCH_CHANNEL_NAME = COMMON_BUILDER.comment("Name of Twitch channel").define("twitch_channel_name", "channel");
        DISCORD_CHANNELS = COMMON_BUILDER.comment("Names of Discord channels to read commands from ['separated', 'like', 'this']").defineList("discord_channels", Lists.newArrayList("general"), x -> true);

        SHOW_CHAT_MESSAGES = COMMON_BUILDER.comment("Should chat messages from Twitch or Discord be show in-game?").define("show_chat_messages", false);
        SHOW_COMMANDS_IN_CHAT = COMMON_BUILDER.comment("Should chosen commands be shown if chat messages are enabled?").define("show_commands_in_chat", false);
        CHOOSE_COMMAND_DELAY = COMMON_BUILDER.comment("How many seconds until the next command is chosen").defineInRange("choose_command_delay", 20, 3, 60);
        CHOOSE_MESSAGE_DELAY = COMMON_BUILDER.comment("How many seconds until a random viewer-written message is shown on screen").defineInRange("choose_message_delay", 240, 10, 480);

        MINECRAFT_USERNAME = COMMON_BUILDER.comment("The players' Minecraft usernames that will be effected").defineList("minecraft_username", Lists.newArrayList("Dev", "Test"), x -> true);
        COMMAND_PREFIX = COMMON_BUILDER.comment("The prefix for commands in Twitch or Discord").define("command_prefix", "!");

        ENABLE_COOLDOWN = COMMON_BUILDER.comment("Prevent the same command from being executed twice in a row").define("enable_cooldown", false);
        ENABLE_FRENZY = COMMON_BUILDER.comment("Allow Frenzy Mode").define("enable_frenzy", true);

        REQUIRE_BITS = COMMON_BUILDER.comment("Require a certain amount of bits for any command").define("require_bits", false);
        MINIMUM_BITS = COMMON_BUILDER.comment("How many bits are needed to activate commands if they are required").defineInRange("minimum_bits", 10, 1, Integer.MAX_VALUE);

        VOTES_NEEDED = COMMON_BUILDER.comment("How many votes are needed to activate certain commands").defineInRange("votes_needed", 3, 2, 16);
        BOOK_LENGTH = COMMON_BUILDER.comment("How many messages should be included when chat writes a book").defineInRange("book_length", 10, 5, 99);

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

        BotConfig.prefix = StringUtils.defaultIfEmpty(COMMAND_PREFIX.get(), "!");

        BotConfig.requireBits = REQUIRE_BITS.get();
        BotConfig.minimumBitsAmount = MINIMUM_BITS.get();

        // Default affected player is the first in the list
        PlayerHelper.setUsername(MINECRAFT_USERNAME.get().get(0));
        PlayerHelper.affectedPlayers = MINECRAFT_USERNAME.get();

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
