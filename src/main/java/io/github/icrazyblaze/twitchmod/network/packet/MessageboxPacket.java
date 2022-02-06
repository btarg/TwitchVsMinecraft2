package io.github.icrazyblaze.twitchmod.network.packet;

import io.github.icrazyblaze.twitchmod.network.ClientPacketFunctions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageboxPacket {

    public String toSend;

    public MessageboxPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public MessageboxPacket(String message) {
        this.toSend = message;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        toSend = buf.readUtf(32767);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        String display = this.toSend;
        ctx.get().enqueueWork(() -> ClientPacketFunctions.showMessageBoxClient(display));

        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(toSend);
    }

}