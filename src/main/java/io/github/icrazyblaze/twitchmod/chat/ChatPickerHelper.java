package io.github.icrazyblaze.twitchmod.chat;

import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

public class ChatPickerHelper {

    /**
     * Runs checkChat on the server thread, which is necessary to avoid crashes and
     * client/server related issues.
     *
     * @param message The chat command, e.g. "!creeper"
     * @param sender  The sender's name, which is used in some commands.
     * @see ChatPicker::checkChat
     * @since 3.5.0
     */
    public static void checkChatThreaded(String message, String sender) {

        // Add command to queue
        Runnable runnable = (() -> ChatPicker.checkChat(message, sender));

        // Only run on main (server) thread
        BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        if (!executor.isSameThread()) {
            executor.execute(runnable);
        } else {
            runnable.run();
        }

    }

}
