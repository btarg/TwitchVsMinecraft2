package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;


public class ClearBlacklistCommand implements Command<CommandSourceStack> {

    private static final ClearBlacklistCommand CMD = new ClearBlacklistCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("clearblacklist")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {

        ChatPicker.clearBlacklist();
        context.getSource().sendSuccess(new TextComponent("Blacklisted commands: " + ChatPicker.blacklist.toString()), false);

        return SINGLE_SUCCESS;

    }

}