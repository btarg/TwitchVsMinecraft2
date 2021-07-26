package io.github.icrazyblaze.twitchmod.network;

import io.github.icrazyblaze.twitchmod.ClientPacketFunctions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class LinkScreenPacket {

    public String toSend;

    public LinkScreenPacket(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public LinkScreenPacket(String message) {
        this.toSend = message;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        toSend = buf.readUtf(32767);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        String display = this.toSend;
        ctx.get().enqueueWork(() -> ClientPacketFunctions.showLinkScreenClient(display));

        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(toSend);
    }

}