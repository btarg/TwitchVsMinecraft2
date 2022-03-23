package io.github.icrazyblaze.twitchmod.bots.discord;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.ChatPickerHelper;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
import io.github.icrazyblaze.twitchmod.util.CalculateMinecraftColor;
import io.github.icrazyblaze.twitchmod.util.files.BlacklistSystem;
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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.connecting_discord").withStyle(ChatFormatting.LIGHT_PURPLE));
        jda = JDABuilder.createDefault(BotConfig.DISCORD_TOKEN).build();
        jda.addEventListener(new DiscordBot());

        jda.getPresence().setActivity(Activity.playing(I18n.get("gui.twitchmod.mod_name")));
        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.status.connected_success_discord").withStyle(ChatFormatting.DARK_GREEN));
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

        if (!ConfigManager.DISCORD_CHANNELS.get().contains(channel)) {
            return;
        }

        Color userColor = event.getMember().getColor();

        if (userColor == null) {
            userColor = Color.white;
        }

        boolean isAdmin = event.getMember().hasPermission(Permission.ADMINISTRATOR);

        if ((!message.startsWith(BotConfig.getCommandPrefix()) || ConfigManager.SHOW_COMMANDS_IN_CHAT.get()) && ConfigManager.SHOW_CHAT_MESSAGES.get()) {

            ChatFormatting format = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
            List<String> roleNames = new ArrayList<>();

            // Get role names and add them to a hover
            for (Role r : event.getMember().getRoles()) {
                roleNames.add(r.getName());
            }

            MutableComponent showText = new TranslatableComponent("gui.twitchmod.chat.prefix_discord", new TextComponent(channel).withStyle(ChatFormatting.BLUE), new TextComponent(sender).withStyle(format), message).withStyle(ChatFormatting.WHITE);

            if (!roleNames.isEmpty()) {
                showText.setStyle(showText.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(format + StringUtils.join(roleNames, ", ")))));
            }
            CommandHandlers.broadcastMessage(showText);

        }

        if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "help") || message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "commands")) {

            event.getChannel().sendMessage(I18n.get("gui.twitchmod.commands_link")).queue();

        } else if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "modlink")) {

            event.getChannel().sendMessage(I18n.get("gui.twitchmod.mod_link")).queue();

        } else if (message.startsWith(BotConfig.getCommandPrefix() + "blacklist")) {

            message = message.substring(BotConfig.getCommandPrefix().length());

            if (isAdmin && message.contains(" ")) {

                String cmd = message.substring(11);

                if (cmd.startsWith("add ")) {
                    BlacklistSystem.addToBlacklist(cmd.substring(4));
                } else if (cmd.startsWith("remove ")) {
                    BlacklistSystem.removeFromBlacklist(cmd.substring(7));
                } else if (cmd.equalsIgnoreCase("clear")) {
                    BlacklistSystem.clearBlacklist();
                    event.getChannel().sendMessage(I18n.get("gui.twitchmod.blacklist_cleared")).queue();
                }
            }
            event.getChannel().sendMessage(I18n.get("gui.twitchmod.blacklisted_commands", BlacklistSystem.getBlacklist().toString())).queue();

        } else if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "disconnect") && isAdmin) {
            jda.shutdown();

        } else if (message.startsWith(BotConfig.getCommandPrefix()) || ChatPicker.logMessages) {

            ChatPickerHelper.checkChatThreaded(message, sender);

        }
    }
}
