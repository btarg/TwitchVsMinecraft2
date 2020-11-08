package io.github.icrazyblaze.twitchmod.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.command.*;
import io.github.icrazyblaze.twitchmod.discord.DiscordConnectCommand;
import io.github.icrazyblaze.twitchmod.discord.DiscordConnectionHelper;
import io.github.icrazyblaze.twitchmod.discord.DiscordDisconnectCommand;
import io.github.icrazyblaze.twitchmod.discord.TokenCommand;
import io.github.icrazyblaze.twitchmod.irc.TwitchConnectionHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * SubscribeEvents go here to avoid clutter in the main class.
 *
 * @see io.github.icrazyblaze.twitchmod.Main
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
                // Register Discord commands under /ttv
                .then(dispatcher.register(Commands.literal("discord")
                        .then(DiscordConnectCommand.register())
                        .then(DiscordDisconnectCommand.register())
                        .then(TokenCommand.register())
                ))
        );

        Main.updateConfig();
        ChatPicker.initCommands();

    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {

        if (!event.world.isRemote && PlayerHelper.defaultServer == null) {

            // Set the server reference for BotCommands (used to get player entity)
            PlayerHelper.defaultServer = event.world.getServer();
            TickHandler.enableTimers = true;

        }

    }


    @SubscribeEvent
    public static void serverStopping(FMLServerStoppingEvent event) {

        if (TwitchConnectionHelper.isConnected()) {
            TwitchConnectionHelper.disconnectBot();
        }
        if (DiscordConnectionHelper.isConnected()) {
            DiscordConnectionHelper.disconnectDiscord();
        }

        TickHandler.enableTimers = false;
        PlayerHelper.defaultServer = null; // Set to null again to avoid errors when restarting world

    }

}