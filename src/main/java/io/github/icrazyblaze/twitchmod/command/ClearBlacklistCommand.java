package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;


public class ClearBlacklistCommand implements Command<CommandSource> {

    private static final ClearBlacklistCommand CMD = new ClearBlacklistCommand();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("clearblacklist")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {

        ChatPicker.clearBlacklist();
        context.getSource().sendMessage(new TextComponent("Blacklisted commands: " + ChatPicker.blacklist.toString()), Util.NIL_UUID);

        return SINGLE_SUCCESS;

    }

}