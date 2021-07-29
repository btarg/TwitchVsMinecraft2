package io.github.icrazyblaze.twitchmod.network;

import io.github.icrazyblaze.twitchmod.gui.MessageboxScreen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;


/**
 * This class is for functions that only run on the client.
 *
 * @see io.github.icrazyblaze.twitchmod.network.PacketHandler
 */
public class ClientPacketFunctions {
    /*
        This code is run on the client when the Messagebox packet is received.
    */
    public static void showMessageBoxClient(String message) {
        Minecraft.getInstance().setScreen(new MessageboxScreen(message));
    }

    /*
        This code is run on the client when the LinkScreen packet is received.
    */
    public static void showLinkScreenClient(String link) {
        ConfirmLinkScreen screen = new ConfirmLinkScreen((p_169232_) -> {
            if (p_169232_) {
                Util.getPlatform().openUri(link);
            }
            Minecraft.getInstance().setScreen(null);
        }, link, true);

        Minecraft.getInstance().setScreen(screen);
    }
}
