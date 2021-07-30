package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.util.files.BlacklistSystem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;


public class BlacklistCommand implements Command<CommandSourceStack> {

    private static final BlacklistCommand CMD = new BlacklistCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("blacklist")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD::showMessage)
                .then(Commands.literal("add").then(Commands.argument("command", StringArgumentType.greedyString()).executes(CMD)))
                .then(Commands.literal("remove").then(Commands.argument("command", StringArgumentType.greedyString()).executes(CMD::removeFromBlacklist)))
                .then(Commands.literal("clear").executes(CMD::clearBlacklist));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {

        String message = StringArgumentType.getString(context, "command");

        // Add to blacklist
        BlacklistSystem.addToBlacklist(message);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.blacklist_added", message), false);
        showMessage(context);

        return SINGLE_SUCCESS;

    }

    private int removeFromBlacklist(CommandContext<CommandSourceStack> context) {

        String message = StringArgumentType.getString(context, "command");

        // Remove from blacklist
        BlacklistSystem.removeFromBlacklist(message);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.blacklist_removed", message), false);
        showMessage(context);

        return SINGLE_SUCCESS;

    }

    private int clearBlacklist(CommandContext<CommandSourceStack> context) {

        BlacklistSystem.clearBlacklist();
        showMessage(context);

        return SINGLE_SUCCESS;

    }

    private int showMessage(CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.chat.blacklisted_commands", BlacklistSystem.getBlacklist().toString()), false);
        return SINGLE_SUCCESS;

    }
}