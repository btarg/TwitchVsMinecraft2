package io.github.icrazyblaze.twitchmod.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.icrazyblaze.twitchmod.bots.discord.DiscordConnectionHelper;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import io.github.icrazyblaze.twitchmod.chat.ChatCommands;
import io.github.icrazyblaze.twitchmod.command.*;
import io.github.icrazyblaze.twitchmod.command.discord.DiscordConnectCommand;
import io.github.icrazyblaze.twitchmod.command.discord.DiscordDisconnectCommand;
import io.github.icrazyblaze.twitchmod.command.discord.GetTokenCommand;
import io.github.icrazyblaze.twitchmod.command.discord.TokenCommand;
import io.github.icrazyblaze.twitchmod.command.twitch.GetKeyCommand;
import io.github.icrazyblaze.twitchmod.command.twitch.SetKeyCommand;
import io.github.icrazyblaze.twitchmod.command.twitch.TwitchConnectCommand;
import io.github.icrazyblaze.twitchmod.command.twitch.TwitchDisconnectCommand;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.integration.IntegrationWrapper;
import io.github.icrazyblaze.twitchmod.util.files.BlacklistSystem;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * SubscribeEvents go here to avoid clutter in the main class.
 *
 * @see io.github.icrazyblaze.twitchmod.Main
 */

public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("ttv")
                .then(TwitchConnectCommand.register())
                .then(TwitchDisconnectCommand.register())
                .then(SetKeyCommand.register())
                .then(GetKeyCommand.register())
                .then(TestCommand.register())
                .then(StatusCommand.register())
                .then(QueueCommand.register())
                .then(BlacklistCommand.register())
                .then(ListCommand.register())
                // Register Discord commands
                .then(dispatcher.register(Commands.literal("discord")
                        .then(DiscordConnectCommand.register())
                        .then(DiscordDisconnectCommand.register())
                        .then(TokenCommand.register())
                        .then(GetTokenCommand.register())
                ))
        );

        BlacklistSystem.loadBlacklistFile();
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event) {

        if (!event.world.isClientSide() && PlayerHelper.defaultServer == null) {

            // Set the server reference for PlayerHelper
            PlayerHelper.defaultServer = event.world.getServer();
            TimerSystem.enableTimers = true;

        }

    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        ChatCommands.initCommands();
        IntegrationWrapper.initModCommands();
        ChatCommands.initDynamicCommands("", ""); // this initialisation prevents the dynamic commands not being recognised as real commands

        ConfigManager.updateFromConfig();
    }


    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {

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