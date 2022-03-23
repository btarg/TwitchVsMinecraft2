package io.github.icrazyblaze.twitchmod.command.discord;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.util.files.SecretFileHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;


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

        SecretFileHelper.writeDiscordToken(key);

        // Update config
        SecretFileHelper.setValuesFromFiles();

        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.chat.ready_discord").withStyle(ChatFormatting.GOLD), false);
        return SINGLE_SUCCESS;

    }
}