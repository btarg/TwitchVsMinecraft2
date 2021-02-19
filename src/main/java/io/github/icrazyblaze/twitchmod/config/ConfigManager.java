package io.github.icrazyblaze.twitchmod.config;


import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigManager {

    public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<String> TWITCH_CHANNEL_NAME;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISCORD_CHANNELS;
    private static final ForgeConfigSpec.BooleanValue SHOW_COMMANDS_IN_CHAT;
    private static final ForgeConfigSpec.ConfigValue<Integer> CHOOSE_COMMAND_DELAY;
    private static final ForgeConfigSpec.ConfigValue<Integer> CHOOSE_MESSAGE_DELAY;
    private static final ForgeConfigSpec.ConfigValue<String> MINECRAFT_USERNAME;
    private static final ForgeConfigSpec.ConfigValue<String> COMMAND_PREFIX;
    private static final ForgeConfigSpec.BooleanValue ENABLE_COOLDOWN;
    private static final ForgeConfigSpec.BooleanValue ENABLE_FRENZY;
    private static final ForgeConfigSpec.ConfigValue<Integer> VOTES_NEEDED;
    private static final ForgeConfigSpec.ConfigValue<Integer> BOOK_LENGTH;
    private static final ForgeConfigSpec.BooleanValue SHOW_CHAT_MESSAGES;
    public static ForgeConfigSpec COMMON_CONFIG;

    static {

        COMMON_BUILDER.push("general");
        TWITCH_CHANNEL_NAME = COMMON_BUILDER.comment("Name of Twitch channel").define("twitch_channel_name", "");
        DISCORD_CHANNELS = COMMON_BUILDER.comment("Names of Discord channels to read commands from ['separated', 'like', 'this']").define("discord_channels", Lists.newArrayList("general"));

        SHOW_CHAT_MESSAGES = COMMON_BUILDER.comment("Should chat messages from Twitch or Discord be show in-game?").define("show_chat_messages", false);
        SHOW_COMMANDS_IN_CHAT = COMMON_BUILDER.comment("Should chosen commands be shown if chat messages are enabled?").define("show_commands_in_chat", false);
        CHOOSE_COMMAND_DELAY = COMMON_BUILDER.comment("How many seconds until the next command is chosen").define("choose_command_delay", 20);
        CHOOSE_MESSAGE_DELAY = COMMON_BUILDER.comment("How many seconds until a random viewer-written message is shown on screen").define("choose_message_delay", 240);

        MINECRAFT_USERNAME = COMMON_BUILDER.comment("The player's Minecraft username").define("minecraft_username", "");
        COMMAND_PREFIX = COMMON_BUILDER.comment("The prefix for commands in Twitch or Discord").define("command_prefix", "!");

        ENABLE_COOLDOWN = COMMON_BUILDER.comment("Prevent the same command from being executed twice in a row").define("enable_cooldown", false);
        ENABLE_FRENZY = COMMON_BUILDER.comment("Allow Frenzy Mode").define("enable_frenzy", true);

        VOTES_NEEDED = COMMON_BUILDER.comment("How many votes are needed to activate certain commands").define("votes_needed", 3);
        BOOK_LENGTH = COMMON_BUILDER.comment("How many messages should be included when chat writes a book").define("book_length", 10);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();

    }

    public static String getTwitch_channel_name() {
        return TWITCH_CHANNEL_NAME.get();
    }

    public static boolean isShow_chat_messages() {
        return SHOW_CHAT_MESSAGES.get();
    }

    public static boolean isShow_commands_in_chat() {
        return SHOW_COMMANDS_IN_CHAT.get();
    }

    public static int getChoose_command_delay() {
        return CHOOSE_COMMAND_DELAY.get();
    }

    public static int getChoose_message_delay() {
        return CHOOSE_MESSAGE_DELAY.get();
    }

    public static String getMinecraft_username() {
        return MINECRAFT_USERNAME.get();
    }

    public static String getCommand_prefix() {
        return COMMAND_PREFIX.get();
    }

    public static boolean isEnable_cooldown() {
        return ENABLE_COOLDOWN.get();
    }

    public static boolean isEnable_frenzy() {
        return ENABLE_FRENZY.get();
    }

    public static int getVotes_needed() {
        return VOTES_NEEDED.get();
    }

    public static int getBook_length() {
        return BOOK_LENGTH.get();
    }

    public static List<? extends String> getDiscordChannels() {
        return DISCORD_CHANNELS.get();
    }

}
