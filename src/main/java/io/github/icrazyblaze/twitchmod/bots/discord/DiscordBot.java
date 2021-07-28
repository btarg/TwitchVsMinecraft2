package io.github.icrazyblaze.twitchmod.bots.discord;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.ChatPickerHelper;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.util.BlacklistSystem;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
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

        CommandHandlers.broadcastMessage(new TextComponent(ChatFormatting.LIGHT_PURPLE + "Connecting to Discord..."));
        jda = JDABuilder.createDefault(BotConfig.DISCORD_TOKEN).build();
        jda.addEventListener(new DiscordBot());

        jda.getPresence().setActivity(Activity.playing("Twitch Vs Minecraft Reloaded"));
        CommandHandlers.broadcastMessage(new TextComponent(ChatFormatting.DARK_GREEN + "Bot connected!"));
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

        // Don't allow bots to interact, and only allow specified channels to talk
        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        String sender = event.getMember().getEffectiveName();
        String channel = event.getChannel().getName();

        if (!BotConfig.DISCORD_CHANNELS.contains(channel)) {
            return;
        }

        Color userColor = event.getMember().getColor();

        if (userColor == null) {
            userColor = Color.white;
        }

        boolean isAdmin = event.getMember().hasPermission(Permission.ADMINISTRATOR);

        if ((!message.startsWith(BotConfig.prefix) || BotConfig.showCommandsInChat) && BotConfig.showChatMessages) {

            ChatFormatting format = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
            List<String> roleNames = new ArrayList<>();

            // Get role names and add them to a hover
            for (Role r : event.getMember().getRoles()) {
                roleNames.add(r.getName());
            }

            TextComponent showText = new TextComponent(String.format("%s<%s[%s] %s%s%s> %s", ChatFormatting.WHITE, ChatFormatting.BLUE, channel, format, sender, ChatFormatting.WHITE, message));

            if (!roleNames.isEmpty()) {
                showText.setStyle(showText.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(format + StringUtils.join(roleNames, ", ")))));
            }
            CommandHandlers.broadcastMessage(showText);

            if (roleNames.contains("bot-tester")) {
                ChatPicker.forceCommands = true;
            }
        }

        if (message.equalsIgnoreCase(BotConfig.prefix + "help") || message.equalsIgnoreCase(BotConfig.prefix + "commands")) {

            event.getChannel().sendMessage("Click here for a list of commands: http://bit.ly/2UfBCiL").queue();

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "modlink")) {

            event.getChannel().sendMessage("Click here to download the mod: http://bit.ly/TwitchVsMinecraft").queue();

        } else if (message.startsWith(BotConfig.prefix + "blacklist")) {

            message = message.substring(BotConfig.prefix.length());

            if (isAdmin && message.contains(" ")) {

                String cmd = message.substring(11);

                if (cmd.startsWith("add ")) {
                    BlacklistSystem.addToBlacklist(cmd.substring(4));
                } else if (cmd.startsWith("remove ")) {
                    BlacklistSystem.removeFromBlacklist(cmd.substring(7));
                } else if (cmd.equalsIgnoreCase("clear")) {
                    BlacklistSystem.clearBlacklist();
                    event.getChannel().sendMessage("Blacklist cleared.").queue();
                }
            }
            event.getChannel().sendMessage("Blacklisted commands: " + BlacklistSystem.getBlacklist().toString()).queue();

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "disconnect") && isAdmin) {
            jda.shutdown();

        } else if (message.startsWith(BotConfig.prefix) || ChatPicker.logMessages) {

            ChatPickerHelper.checkChatThreaded(message, sender);

        }
    }
}
