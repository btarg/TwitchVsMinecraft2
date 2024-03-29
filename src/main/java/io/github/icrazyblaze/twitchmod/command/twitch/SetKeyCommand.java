package io.github.icrazyblaze.twitchmod.command.twitch;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.icrazyblaze.twitchmod.util.files.SecretFileHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;


public class SetKeyCommand implements Command<CommandSourceStack> {

    private static final SetKeyCommand CMD = new SetKeyCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("key")
                .executes(new GetKeyCommand())
                .requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("keystring", StringArgumentType.greedyString()).executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        // Get key and store in system properties
        String key = StringArgumentType.getString(context, "keystring");

        // UPDATE: check if key is valid (starts with oauth and is 30 characters long)
        // This command should be more idiot-proof now.
        if (key.startsWith("oauth:") && key.length() == 36) {

            SecretFileHelper.writeTwitchKey(key);

        } else {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(() -> ""), () -> I18n.get("exception.twitchmod.invalid_oauth"));
        }

        // Update config
        SecretFileHelper.setValuesFromFiles();

        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.chat.ready_twitch").withStyle(ChatFormatting.GOLD), false);
        return SINGLE_SUCCESS;

    }
}