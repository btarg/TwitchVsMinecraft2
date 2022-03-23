package io.github.icrazyblaze.twitchmod.config;


import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * Reads from and writes to the main configuration file for the mod.
 */
public class ConfigManager {

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.ConfigValue<String> TWITCH_CHANNEL_NAME;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DISCORD_CHANNELS;
    public static ForgeConfigSpec.BooleanValue SHOW_COMMANDS_IN_CHAT;
    public static ForgeConfigSpec.IntValue CHOOSE_COMMAND_DELAY;
    public static ForgeConfigSpec.IntValue CHOOSE_MESSAGE_DELAY;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> PLAYERS_AFFECTED;
    public static ForgeConfigSpec.ConfigValue<String> COMMAND_PREFIX;
    public static ForgeConfigSpec.BooleanValue ENABLE_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue ENABLE_FRENZY;
    public static ForgeConfigSpec.BooleanValue REQUIRE_BITS;
    public static ForgeConfigSpec.IntValue MINIMUM_BITS;
    public static ForgeConfigSpec.IntValue VOTES_NEEDED;
    public static ForgeConfigSpec.IntValue BOOK_LENGTH;
    public static ForgeConfigSpec.BooleanValue SHOW_CHAT_MESSAGES;

    static {

        // Instantiate Builder
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        setupCommon(COMMON_BUILDER);
        COMMON_CONFIG = COMMON_BUILDER.build();

    }

    public static void setupCommon(ForgeConfigSpec.Builder builder) {

        builder.push("general");
        TWITCH_CHANNEL_NAME = builder.comment("Name of Twitch channel").define("twitch_channel_name", "channel");
        DISCORD_CHANNELS = builder.comment("Names of Discord channels to read commands from ['separated', 'like', 'this']").defineList("discord_channels", Lists.newArrayList("general"), x -> true);

        SHOW_CHAT_MESSAGES = builder.comment("Should chat messages from Twitch or Discord be show in-game?").define("show_chat_messages", false);
        SHOW_COMMANDS_IN_CHAT = builder.comment("Should chosen commands be shown if chat messages are enabled?").define("show_commands_in_chat", false);
        CHOOSE_COMMAND_DELAY = builder.comment("How many seconds until the next command is chosen").defineInRange("choose_command_delay", 20, 3, 60);
        CHOOSE_MESSAGE_DELAY = builder.comment("How many seconds until a random viewer-written message is shown on screen").defineInRange("choose_message_delay", 240, 10, 480);

        PLAYERS_AFFECTED = builder.comment("The players' Minecraft usernames that will be effected").defineList("minecraft_username", Lists.newArrayList("Dev", "Test"), x -> true);
        COMMAND_PREFIX = builder.comment("The prefix for commands in Twitch or Discord").define("command_prefix", "!");

        ENABLE_COOLDOWN = builder.comment("Prevent the same command from being executed twice in a row").define("enable_cooldown", false);
        ENABLE_FRENZY = builder.comment("Allow Frenzy Mode").define("enable_frenzy", true);

        REQUIRE_BITS = builder.comment("Require a certain amount of bits for any command").define("require_bits", false);
        MINIMUM_BITS = builder.comment("How many bits are needed to activate commands if they are required").defineInRange("minimum_bits", 10, 1, Integer.MAX_VALUE);

        VOTES_NEEDED = builder.comment("How many votes are needed to activate special commands (e.g. Frenzy Mode)").defineInRange("votes_needed", 3, 2, Integer.MAX_VALUE);
        BOOK_LENGTH = builder.comment("How many messages should be included when chat writes a book").defineInRange("book_length", 10, 5, 99);

        builder.pop();

    }

}
