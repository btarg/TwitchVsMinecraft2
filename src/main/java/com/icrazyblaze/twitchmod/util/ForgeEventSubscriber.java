package com.icrazyblaze.twitchmod.util;

import com.icrazyblaze.twitchmod.BotCommands;
import com.icrazyblaze.twitchmod.Main;
import com.icrazyblaze.twitchmod.chat.ChatPicker;
import com.icrazyblaze.twitchmod.command.*;
import com.icrazyblaze.twitchmod.discord.DiscordConnectCommand;
import com.icrazyblaze.twitchmod.discord.DiscordDisconnectCommand;
import com.icrazyblaze.twitchmod.discord.TokenCommand;
import com.icrazyblaze.twitchmod.irc.TwitchConnection;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
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
        // Dispatcher is now a variable, like it should be
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
        dispatcher.register(Commands.literal("ttv")
                .then(ConnectCommand.register())
                .then(DisconnectCommand.register())
                .then(SetKeyCommand.register())
                .then(TestCommand.register())
                .then(StatusCommand.register())
                .then(QueueCommand.register())
                .then(BlacklistCommand.register())
                .then(ListCommand.register())
        );

        dispatcher.register(Commands.literal("discord")
                .then(DiscordConnectCommand.register())
                .then(DiscordDisconnectCommand.register())
                .then(TokenCommand.register())
        );

        Main.updateConfig();
        ChatPicker.initCommands();

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {

        if (!event.world.isRemote && BotCommands.defaultServer == null) {

            // Set the server reference for BotCommands (used to get player entity)
            BotCommands.defaultServer = event.world.getServer();
            TickHandler.enableTimers = true;

        }

    }


    @SubscribeEvent
    public static void serverStopping(FMLServerStoppingEvent event) {

        if (TwitchConnection.isConnected())
            TwitchConnection.disconnectBot();

        TickHandler.enableTimers = false;

    }

}