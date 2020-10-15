package com.icrazyblaze.twitchmod.network;

import com.icrazyblaze.twitchmod.CommandHandlers;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageboxPacket {

    public String toSend;

    public MessageboxPacket(PacketBuffer buf) {
        fromBytes(buf);
    }

    public void fromBytes(PacketBuffer buf) {
        toSend = buf.readString(32767);
    }


    public MessageboxPacket(String message) {
        this.toSend = message;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        String display = this.toSend;

        ctx.get().enqueueWork(() -> {

            CommandHandlers.showMessageBoxClient(display);

        });
        ctx.get().setPacketHandled(true);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(toSend);
    }

}