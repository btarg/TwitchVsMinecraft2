package io.github.icrazyblaze.twitchmod.discord;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.Main;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;


public class TokenCommand implements Command<CommandSource> {

    private static final TokenCommand CMD = new TokenCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("token")
                .requires(cs -> cs.hasPermissionLevel(0))
                .then(Commands.argument("token", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        // Get key and store in system properties
        String key = StringArgumentType.getString(context, "token");

        System.setProperty("discord_bot_token", key);

        // Update config
        Main.updateConfig();

        context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GOLD + "Discord Bot Token set. Use /discord connect to start!"), false);
        return SINGLE_SUCCESS;

    }
}