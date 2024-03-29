package io.github.icrazyblaze.twitchmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.icrazyblaze.twitchmod.bots.discord.DiscordConnectionHelper;
import io.github.icrazyblaze.twitchmod.bots.irc.TwitchConnectionHelper;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import io.github.icrazyblaze.twitchmod.util.UptimeReader;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class StatusCommand implements Command<CommandSourceStack> {

    private static final StatusCommand CMD = new StatusCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("status")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        // Display current status and uptime
        if (TwitchConnectionHelper.isConnected()) {
            context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.status.connected_twitch").withStyle(ChatFormatting.GREEN), false);
        } else {
            context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.status.disconnected_twitch").withStyle(ChatFormatting.RED), false);
        }

        if (DiscordConnectionHelper.isConnected()) {
            context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.status.connected_discord").withStyle(ChatFormatting.GREEN), false);
        } else {
            context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.status.disconnected_discord").withStyle(ChatFormatting.RED), false);
        }

        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.twitch_name", BotConfig.getTwitchChannelName()).withStyle(ChatFormatting.GOLD), false);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.twitch_uptime", UptimeReader.getUptimeString(BotConfig.getTwitchChannelName())).withStyle(ChatFormatting.GREEN), false);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.discord_channels", ConfigManager.DISCORD_CHANNELS.get().toString()).withStyle(ChatFormatting.BLUE), false);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.players", PlayerHelper.affectedPlayers.get().toString()).withStyle(ChatFormatting.GOLD), false);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.command_seconds", TimerSystem.chatSecondsTrigger.get()).withStyle(ChatFormatting.DARK_PURPLE), false);
        context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.command_prefix", BotConfig.getCommandPrefix()).withStyle(ChatFormatting.DARK_PURPLE), false);

        if (ConfigManager.REQUIRE_BITS.get()) {
            context.getSource().sendSuccess(new TranslatableComponent("gui.twitchmod.chat.bits_required", ConfigManager.MINIMUM_BITS.get()).withStyle(ChatFormatting.DARK_PURPLE), false);
        }


        MutableComponent keyMessage = new TranslatableComponent("gui.twitchmod.chat.hint_login").withStyle(ChatFormatting.AQUA);

        context.getSource().sendSuccess(keyMessage, false);

        return SINGLE_SUCCESS;
    }
}