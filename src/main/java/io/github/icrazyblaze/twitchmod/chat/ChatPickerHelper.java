package io.github.icrazyblaze.twitchmod.chat;

import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;

public class ChatPickerHelper {

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
