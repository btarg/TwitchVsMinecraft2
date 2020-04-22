package com.icrazyblaze.twitchmod.network;

import com.icrazyblaze.twitchmod.BotCommands;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import net.minecraftforge.fml.network.NetworkEvent;

public class GuiMessage {
    public String toSend;

    // A default constructor is always required
    public GuiMessage() {
    }

    public GuiMessage(String toSend) {
        this.toSend = toSend;
    }

    public static void handle(GuiMessage message, MessagePassingQueue.Supplier<NetworkEvent.Context> ctx) {

        String display = message.toSend;

        ctx.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            // Execute the action on the main server thread by adding it as a scheduled task
            BotCommands.showMessageBoxClient(display);
        });
        ctx.get().setPacketHandled(true);
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtil.writeUtf8(buf, toSend);
    }

    public void fromBytes(ByteBuf buf) {
        toSend = ByteBufUtil.getBytes(buf).toString();
    }

}
