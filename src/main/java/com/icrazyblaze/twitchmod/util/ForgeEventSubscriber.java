package com.icrazyblaze.twitchmod.util;

import com.icrazyblaze.twitchmod.BotCommands;
import com.icrazyblaze.twitchmod.Main;
import com.icrazyblaze.twitchmod.chat.ChatPicker;
import com.icrazyblaze.twitchmod.command.*;
import com.icrazyblaze.twitchmod.irc.BotConnection;
import net.minecraft.command.Commands;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * SubscribeEvents go here to avoid clutter in the main class.
 *
 * @see com.icrazyblaze.twitchmod.Main
 */

public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void serverStarting(FMLServerStartingEvent event) {

        // Register commands
        event.getCommandDispatcher().register(Commands.literal("ttv")
                .then(ConnectCommand.register(event.getCommandDispatcher()))
                .then(DisconnectCommand.register(event.getCommandDispatcher()))
                .then(SetKeyCommand.register(event.getCommandDispatcher()))
                .then(TestCommand.register(event.getCommandDispatcher()))
                .then(StatusCommand.register(event.getCommandDispatcher()))
                .then(QueueCommand.register(event.getCommandDispatcher()))
                .then(BlacklistCommand.register(event.getCommandDispatcher()))
                .then(ListCommand.register(event.getCommandDispatcher()))
        );

        Main.updateConfig();
        ChatPicker.initCommands();

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {

        if (!event.world.isRemote && BotCommands.defaultServer == null) {

            // Set the server reference for BotCommands (used to get player entity)
            BotCommands.defaultServer = event.world.getServer();
            TickHandler.enabled = true;

        }

    }


    @SubscribeEvent
    public static void serverStopping(FMLServerStoppingEvent event) {

        if (BotConnection.isConnected())
            BotConnection.disconnectBot();

        TickHandler.enabled = false;

    }

}
