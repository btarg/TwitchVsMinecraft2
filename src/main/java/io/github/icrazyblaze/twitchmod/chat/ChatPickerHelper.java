package io.github.icrazyblaze.twitchmod.chat;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class ChatPickerHelper {

    public static void checkChatThreaded(String message, String sender) {

        // Add command to queue
        Runnable runnable = (() -> ChatPicker.checkChat(message, sender));

        // Only run on main (server) thread
        ThreadTaskExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        if (!executor.isOnExecutionThread()) {
            executor.deferTask(runnable);
        } else {
            runnable.run();
        }

    }

}
