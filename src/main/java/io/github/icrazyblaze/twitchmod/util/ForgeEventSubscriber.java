package io.github.icrazyblaze.twitchmod.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.icrazyblaze.twitchmod.bots.discord.DiscordConnectCommand;
import io.github.icrazyblaze.twitchmod.bots.discord.DiscordConnectionHelper;
import io.github.icrazyblaze.twitchmod.bots.discord.DiscordDisconnectCommand;
import io.github.icrazyblaze.twitchmod.bots.discord.TokenCommand;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import io.github.icrazyblaze.twitchmod.chat.ChatCommands;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.command.*;
import io.github.icrazyblaze.twitchmod.integration.IntegrationWrapper;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * SubscribeEvents go here to avoid clutter in the main class.
 *
 * @see io.github.icrazyblaze.twitchmod.Main
 */

public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("ttv")
                .then(TwitchConnectCommand.register())
                .then(TwitchDisconnectCommand.register())
                .then(SetKeyCommand.register())
                .then(TestCommand.register())
                .then(StatusCommand.register())
                .then(QueueCommand.register())
                .then(BlacklistCommand.register())
                .then(ClearBlacklistCommand.register())
                .then(ListCommand.register())
                // Register Discord commands
                .then(dispatcher.register(Commands.literal("discord")
                        .then(DiscordConnectCommand.register())
                        .then(DiscordDisconnectCommand.register())
                        .then(TokenCommand.register())
                ))
        );

        ChatPicker.loadBlacklistFile();
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {

        if (!event.world.isRemote && PlayerHelper.defaultServer == null) {

            // Set the server reference for PlayerHelper
            PlayerHelper.defaultServer = event.world.getServer();
            TimerSystem.enableTimers = true;

        }

    }

    @SubscribeEvent
    public static void serverStarted(FMLServerStartedEvent event) {
        ChatCommands.initCommands();
        IntegrationWrapper.initModCommands();
        ChatCommands.initDynamicCommands("", ""); // this initialisation prevents the dynamic commands not being recognised as real commands
    }


    @SubscribeEvent
    public static void serverStopping(FMLServerStoppingEvent event) {

        if (TwitchConnectionHelper.isConnected()) {
            TwitchConnectionHelper.disconnectBot();
        }
        if (DiscordConnectionHelper.isConnected()) {
            DiscordConnectionHelper.disconnectDiscord();
        }

        TimerSystem.enableTimers = false;
        PlayerHelper.defaultServer = null; // Set to null again to avoid errors when restarting world

    }

}