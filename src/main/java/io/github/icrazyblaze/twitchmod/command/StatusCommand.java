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
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;

public class StatusCommand implements Command<CommandSourceStack> {

    private static final StatusCommand CMD = new StatusCommand();

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("status")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ConfigManager.updateFromConfig();

        // Display current status and uptime
        if (TwitchConnectionHelper.isConnected()) {
            context.getSource().sendSuccess(new TextComponent(ChatFormatting.GREEN + "Twitch bot connected."), false);
        } else {
            context.getSource().sendSuccess(new TextComponent(ChatFormatting.RED + "Twitch bot not connected."), false);
        }

        if (DiscordConnectionHelper.isConnected()) {
            context.getSource().sendSuccess(new TextComponent(ChatFormatting.GREEN + "Discord bot connected."), false);
        } else {
            context.getSource().sendSuccess(new TextComponent(ChatFormatting.RED + "Discord bot not connected."), false);
        }

        context.getSource().sendSuccess(new TextComponent(ChatFormatting.GOLD + "Twitch channel name: " + BotConfig.CHANNEL_NAME), false);
        context.getSource().sendSuccess(new TextComponent(ChatFormatting.GREEN + "Twitch stream uptime: " + UptimeReader.getUptimeString(BotConfig.CHANNEL_NAME)), false);
        context.getSource().sendSuccess(new TextComponent(ChatFormatting.BLUE + "Watching Discord channels: " + BotConfig.DISCORD_CHANNELS.toString()), false);
        context.getSource().sendSuccess(new TextComponent(ChatFormatting.GOLD + "Player(s) affected: " + PlayerHelper.affectedPlayers.toString()), false);
        context.getSource().sendSuccess(new TextComponent(ChatFormatting.DARK_PURPLE + "A new command will be chosen every " + TimerSystem.chatSecondsTrigger + " seconds."), false);
        context.getSource().sendSuccess(new TextComponent(ChatFormatting.DARK_PURPLE + "Commands start with " + BotConfig.prefix), false);

        // Click chat message to go to Twitch login page
        TextComponent keyMessage = new TextComponent(ChatFormatting.AQUA + "Click here to get your Twitch OAuth key!");
        ClickEvent goLinkEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitchapps.com/tmi/");
        HoverEvent goHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to open the link in your browser"));

        keyMessage.setStyle(keyMessage.getStyle().withClickEvent(goLinkEvent));
        keyMessage.setStyle(keyMessage.getStyle().withHoverEvent(goHoverEvent));

        context.getSource().sendSuccess(keyMessage, false);

        return SINGLE_SUCCESS;
    }
}