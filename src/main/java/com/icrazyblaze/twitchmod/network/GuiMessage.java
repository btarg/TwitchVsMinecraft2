package com.icrazyblaze.twitchmod.network;

import com.icrazyblaze.twitchmod.BotCommands;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GuiMessage {

    public String toSend;

    public GuiMessage(PacketBuffer buf) {
        fromBytes(buf);
    }

    public GuiMessage(String message) {
        this.toSend = message;
    }


    public void handle(Supplier<NetworkEvent.Context> ctx) {

        String display = this.toSend;

        ctx.get().enqueueWork(() -> {

            BotCommands.showMessageBoxClient(display);

        });
        ctx.get().setPacketHandled(true);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(toSend);
    }

    public void fromBytes(PacketBuffer buf) {
        toSend = buf.readString(32767);
    }

}