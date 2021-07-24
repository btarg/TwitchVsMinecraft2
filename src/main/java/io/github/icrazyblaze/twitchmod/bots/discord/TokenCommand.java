package io.github.icrazyblaze.twitchmod.bots.discord;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.util.SecretFileHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;


public class TokenCommand implements Command<CommandSourceStack> {

    private static final TokenCommand CMD = new TokenCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("token")
                .requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("token", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        // Get key and store in system properties
        String key = StringArgumentType.getString(context, "token");

        SecretFileHelper.setDiscordToken(key);

        // Update config
        ConfigManager.updateFromConfig();

        context.getSource().sendSuccess(new TextComponent(ChatFormatting.GOLD + "Discord Bot Token set. Use /discord connect to start!"), false);
        return SINGLE_SUCCESS;

    }
}