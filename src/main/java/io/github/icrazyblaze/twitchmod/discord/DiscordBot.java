package io.github.icrazyblaze.twitchmod.discord;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.util.BotConfig;
import io.github.icrazyblaze.twitchmod.util.CalculateMinecraftColor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DiscordBot extends ListenerAdapter {

    public JDA jda = null;
    public boolean isConnected = false;

    public void startDiscordBot() throws LoginException {

        if (jda != null) {
            jda.shutdown();
        }

        CommandHandlers.broadcastMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Connecting to Discord..."));
        jda = JDABuilder.createDefault(BotConfig.DISCORD_TOKEN).build();
        jda.addEventListener(new DiscordBot());

        jda.getPresence().setActivity(Activity.playing("Twitch Vs Minecraft Reloaded"));
        CommandHandlers.broadcastMessage(new StringTextComponent(TextFormatting.DARK_GREEN + "Bot connected!"));
        isConnected = true;

    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        isConnected = false;
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        isConnected = false;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Don't allow bots to interact
        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage())
            return;

        String message = event.getMessage().getContentRaw();
        String sender = event.getMember().getEffectiveName();
        String channel = event.getChannel().getName();

        Color userColor = event.getMember().getColor();

        if (userColor == null) {
            userColor = Color.WHITE;
        }

        boolean isAdmin = event.getMember().hasPermission(Permission.ADMINISTRATOR);

        if ((!message.startsWith(BotConfig.prefix) || BotConfig.showCommands) && BotConfig.showChatMessages) {

            TextFormatting format = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
            List<String> roleNames = new ArrayList<>();

            // Get role names and add them to a hover
            for (Role r : event.getMember().getRoles()) {
                roleNames.add(r.getName());
            }

            StringTextComponent showText = new StringTextComponent(String.format("%s<%s[%s] %s%s%s> %s", TextFormatting.WHITE, TextFormatting.BLUE, channel, format, sender, TextFormatting.WHITE, message));

            showText.setStyle(showText.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(format + StringUtils.join(roleNames, ", ")))));

            CommandHandlers.broadcastMessage(showText);

        }

        if (message.equalsIgnoreCase(BotConfig.prefix + "help") || message.equalsIgnoreCase(BotConfig.prefix + "commands")) {

            event.getChannel().sendMessage("Click here for a list of commands: http://bit.ly/2UfBCiL").queue();

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "modlink")) {

            event.getChannel().sendMessage("Click here to download the mod: http://bit.ly/TwitchVsMinecraft").queue();

        } else if (message.startsWith(BotConfig.prefix + "blacklist")) {

            message = message.substring(BotConfig.prefix.length());

            // Moved adding to and clearing blacklist to the Twitch chat (only for mods and broadcasters)
            if (message.startsWith("blacklist ")) {

                if (isAdmin) {

                    if (message.substring(10).startsWith("add ")) {
                        ChatPicker.addToBlacklist(message.substring(14));
                    } else if (message.substring(10).equalsIgnoreCase("clear")) {
                        ChatPicker.clearBlacklist();
                        event.getChannel().sendMessage("Blacklist cleared.").queue();
                    }

                }

            }
            ChatPicker.loadBlacklistFile();
            event.getChannel().sendMessage("Blacklisted commands: " + ChatPicker.blacklist.toString()).queue();

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "disconnect") && isAdmin) {
            jda.shutdown();

        } else if (message.startsWith(BotConfig.prefix) || ChatPicker.tempLogMessages) {

            String finalMessage = message;

            Runnable runnable = (() -> {

                // Add command to queue
                ChatPicker.checkChat(finalMessage, sender);

            });

            // Only run on main thread
            ThreadTaskExecutor executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
            if (!executor.isOnExecutionThread()) {
                executor.deferTask(runnable);
            } else {
                runnable.run();
            }

        }
    }
}
