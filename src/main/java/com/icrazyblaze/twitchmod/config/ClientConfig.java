package com.icrazyblaze.twitchmod.config;

import com.icrazyblaze.twitchmod.util.Reference;
import net.minecraftforge.common.ForgeConfigSpec;


/**
 * For configuration settings that change the behaviour of code on the LOGICAL CLIENT.
 * This can be moved to an inner class of ExampleModConfig, but is separate because of personal preference and to keep the code organised
 *
 * @author Cadiboo
 */
final class ClientConfig {

    public final ForgeConfigSpec.ConfigValue<String> keyProp;
    public final ForgeConfigSpec.ConfigValue<String> channelProp;
    public final ForgeConfigSpec.BooleanValue showMessagesProp;
    public final ForgeConfigSpec.BooleanValue showCommandsProp;
    public final ForgeConfigSpec.ConfigValue<Integer> chatSecondsProp;
    public final ForgeConfigSpec.ConfigValue<Integer> messageSecondsProp;

    public final ForgeConfigSpec.ConfigValue<String> usernameProp;
    public final ForgeConfigSpec.ConfigValue<String> prefixProp;

    public final ForgeConfigSpec.BooleanValue cooldownProp;

    ClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        keyProp = builder
                .comment("Oauth key from twitchapps.com")
                .translation(Reference.MOD_ID + ".config.keyProp")
                .define("keyProp", "");
        channelProp = builder
                .comment("Name of Twitch channel")
                .translation(Reference.MOD_ID + ".config.channelProp")
                .define("channelProp", "");
        showMessagesProp = builder
                .comment("Should chat messages be shown")
                .translation(Reference.MOD_ID + ".config.showMessagesProp")
                .define("showMessagesProp", false);

        showCommandsProp = builder
                .comment("Should chosen commands be shown if chat messages are enabled")
                .translation(Reference.MOD_ID + ".config.showCommandsProp")
                .define("showCommandsProp", true);
        chatSecondsProp = builder
                .comment("How many seconds until the next command is chosen")
                .translation(Reference.MOD_ID + ".config.chatSecondsProp")
                .define("chatSecondsProp", 20);
        messageSecondsProp = builder
                .comment("How many seconds until a random viewer-written message is shown on screen")
                .translation(Reference.MOD_ID + ".config.messageSecondsProp")
                .define("messageSecondsProp", 300);

        usernameProp = builder
                .comment("The streamer's Minecraft username")
                .translation(Reference.MOD_ID + ".config.usernameProp")
                .define("usernameProp", "");
        prefixProp = builder
                .comment("The streamer's Minecraft username")
                .translation(Reference.MOD_ID + ".config.prefixProp")
                .define("prefixProp", "!");

        cooldownProp = builder
                .comment("Prevent the same command from being executed twice in a row.")
                .translation(Reference.MOD_ID + ".config.cooldownProp")
                .define("cooldownProp", false);

        builder.pop();
    }
}