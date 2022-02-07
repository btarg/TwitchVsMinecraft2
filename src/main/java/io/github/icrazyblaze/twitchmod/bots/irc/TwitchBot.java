package io.github.icrazyblaze.twitchmod.bots.irc;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelJoinEvent;
import com.github.twitch4j.chat.events.channel.ChannelLeaveEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.ChatPickerHelper;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.util.CalculateMinecraftColor;
import io.github.icrazyblaze.twitchmod.util.files.BlacklistSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.awt.*;
import java.util.Objects;

public class TwitchBot {

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public TwitchBot(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
        eventHandler.onEvent(ChannelJoinEvent.class, this::onConnect);
        eventHandler.onEvent(ChannelLeaveEvent.class, this::onDisconnect);
    }

    /**
     * Subscribe to the ChannelMessage Event
     */
    public void onChannelMessage(ChannelMessageEvent event) {

        BlacklistSystem.loadBlacklistFile();

        String message = event.getMessage();
        String sender = Objects.requireNonNull(event.getUser()).getName();
        TwitchChat chat = event.getTwitchChat();

        ChatFormatting format = ChatFormatting.WHITE;

        MutableComponent showText;
        String role = null;

        if (BotConfig.showChatMessages) {

            ChatPicker.forceCommands = false;

            // Get hex colour, convert to RGB, then get nearest Minecraft colour code
            Color userColor = Color.decode(event.getMessageEvent().getBadges().get("color"));

            try {
                format = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
            } catch (Exception e) {
                Main.logger.info("No valid user colour");
            }

            if (event.getMessageEvent().getClientPermissions().contains(CommandPermission.BROADCASTER)) {
                ChatPicker.forceCommands = true; // Force commands to execute instantly for broadcaster testing
                role = "Broadcaster";
            } else if (event.getMessageEvent().getClientPermissions().contains(CommandPermission.SUBSCRIBER)) {
                role = "Subscriber";
            } else if (event.getMessageEvent().getClientPermissions().contains(CommandPermission.MODERATOR)) {
                role = "Moderator";
            }


            if (!message.startsWith(BotConfig.prefix) || BotConfig.showCommandsInChat) {

                showText = new TranslatableComponent("gui.twitchmod.chat.prefix_twitch", new TextComponent("Twitch").withStyle(ChatFormatting.DARK_PURPLE), new TextComponent(sender).withStyle(format), message).withStyle(ChatFormatting.WHITE);

                if (role != null) {
                    showText.setStyle(showText.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(format + role))));
                }

                CommandHandlers.broadcastMessage(showText);

            }

        }

        if (message.equalsIgnoreCase(BotConfig.prefix + "help") || message.equalsIgnoreCase(BotConfig.prefix + "commands")) {

            event.reply(chat, I18n.get("gui.twitchmod.commands_link"));

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "modlink")) {

            event.reply(chat, I18n.get("gui.twitchmod.mod_link"));

        } else if (message.startsWith(BotConfig.prefix + "blacklist")) {

            message = message.substring(BotConfig.prefix.length());

            if (Objects.equals(role, "Moderator") || Objects.equals(role, "Broadcaster") && message.contains(" ")) {

                String cmd = message.substring(11);

                if (cmd.startsWith("add ")) {
                    BlacklistSystem.addToBlacklist(cmd.substring(4));
                } else if (cmd.startsWith("remove ")) {
                    BlacklistSystem.removeFromBlacklist(cmd.substring(7));
                } else if (cmd.equalsIgnoreCase("clear")) {
                    BlacklistSystem.clearBlacklist();
                    event.reply(chat, I18n.get("gui.twitchmod.blacklist_cleared"));
                }

            }
            event.reply(chat, I18n.get("gui.twitchmod.blacklisted_commands", BlacklistSystem.getBlacklist().toString()));

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "disconnect")) {
            TwitchConnectionHelper.disconnectBot();

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "reconnect")) {

            TwitchConnectionHelper.login();

        } else if (message.startsWith(BotConfig.prefix) || ChatPicker.logMessages) {

            ChatPickerHelper.checkChatThreaded(message, sender);

        }

    }

    public void onDisconnect(ChannelLeaveEvent event) {
        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.disconnected_success_twitch").withStyle(ChatFormatting.DARK_RED));
        Main.logger.info("Twitch Client left channel: " + event.getChannel().getName());
    }

    public void onConnect(ChannelJoinEvent event) {
        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.connected_success_twitch").withStyle(ChatFormatting.DARK_GREEN));
        Main.logger.info("Twitch Client joined channel: " + event.getChannel().getName());
    }

}
