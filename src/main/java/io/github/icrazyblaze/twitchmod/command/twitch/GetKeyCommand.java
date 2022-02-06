package io.github.icrazyblaze.twitchmod.command.twitch;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.icrazyblaze.twitchmod.network.PacketHandler;
import io.github.icrazyblaze.twitchmod.network.packet.LinkScreenPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.network.NetworkDirection;

import static io.github.icrazyblaze.twitchmod.util.PlayerHelper.player;

public class GetKeyCommand implements Command<CommandSourceStack> {

    private static final GetKeyCommand CMD = new GetKeyCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("getkey")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {

        PacketHandler.INSTANCE.sendTo(new LinkScreenPacket("https://twitchapps.com/tmi"), player().connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);

        return SINGLE_SUCCESS;
    }
}