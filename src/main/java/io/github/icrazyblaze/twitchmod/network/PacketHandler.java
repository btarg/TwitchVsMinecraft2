package io.github.icrazyblaze.twitchmod.network;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.network.packet.LinkScreenPacket;
import io.github.icrazyblaze.twitchmod.network.packet.MessageboxPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int ID = 0;

    public static void registerMessages() {

        INSTANCE.registerMessage(nextID(),
                MessageboxPacket.class,
                MessageboxPacket::toBytes,
                MessageboxPacket::new,
                MessageboxPacket::handle);
        INSTANCE.registerMessage(nextID(),
                LinkScreenPacket.class,
                LinkScreenPacket::toBytes,
                LinkScreenPacket::new,
                LinkScreenPacket::handle);

    }

    public static int nextID() {
        return ID++;
    }

}