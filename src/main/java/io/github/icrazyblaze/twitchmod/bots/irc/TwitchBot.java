package io.github.icrazyblaze.twitchmod.bots.irc;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.enums.CommandPermission;
import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.chat.ChatPickerHelper;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.config.ConfigManager;
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
import java.util.Optional;

public class TwitchBot {

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public TwitchBot(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
        eventHandler.onEvent(ChannelJoinEvent.class, this::onConnect);
        eventHandler.onEvent(CheerEvent.class, this::onCheer);
    }

    public void onCheer(CheerEvent event) {

        // Come back when you're a little, hmmmmm, richer.
        if (event.getBits() < ConfigManager.MINIMUM_BITS.get() && ConfigManager.REQUIRE_BITS.get()) {
            return;
        }
        handleMessage(event);
    }

    /**
     * Subscribe to the ChannelMessage Event
     */
    public void onChannelMessage(ChannelMessageEvent event) {

        // Handle cheer messages in the onCheer method
        if (ConfigManager.REQUIRE_BITS.get()) {
            return;
        }
        handleMessage(event);
    }

    public void handleMessage(Object any_event) {

        IRCMessageEvent event;

        if (any_event instanceof ChannelMessageEvent event1) {
            event = event1.getMessageEvent();
        } else if (any_event instanceof IRCMessageEvent) {
            event = (IRCMessageEvent) any_event;
        } else {
            return;
        }

        BlacklistSystem.loadBlacklistFile();

        // Convert optional into string
        Optional<String> messageOptional = event.getMessage();
        if (messageOptional.isEmpty())
            return;
        String message = messageOptional.get();

        // Get sender and chat
        String sender = Objects.requireNonNull(event.getUser()).getName();
        TwitchChat chat = event.getTwitchChat();

        // Debug twitch chat error messages
        chat.getEventManager().onEvent(ChannelNoticeEvent.class, System.out::println);

        ChatFormatting format = ChatFormatting.WHITE;

        MutableComponent showText;
        String role = null;


        if (ConfigManager.SHOW_CHAT_MESSAGES.get()) {

            ChatPicker.forceCommands = false;

            // Get hex colour, convert to RGB, then get nearest Minecraft colour code
            Color userColor = Color.decode(event.getTagValue("color").orElse("#FFFFFF"));

            try {
                format = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
            } catch (Exception e) {
                Main.logger.info("No valid user colour");
            }

            // Set default as "chatter" instead of blank
            role = "Chatter";
            if (event.getClientPermissions().contains(CommandPermission.BROADCASTER)) {
                ChatPicker.forceCommands = true; // Force commands to execute instantly for broadcaster testing
                role = "Broadcaster";
            } else if (event.getClientPermissions().contains(CommandPermission.MODERATOR)) {
                role = "Moderator";
            } else if (event.getClientPermissions().contains(CommandPermission.SUBSCRIBER)) {
                role = "Subscriber";
            }


            if (!message.startsWith(BotConfig.getCommandPrefix()) || ConfigManager.SHOW_COMMANDS_IN_CHAT.get()) {

                showText = new TranslatableComponent("gui.twitchmod.chat.prefix_twitch", new TextComponent("Twitch").withStyle(ChatFormatting.DARK_PURPLE), new TextComponent(sender).withStyle(format), message).withStyle(ChatFormatting.WHITE);

                showText.setStyle(showText.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(format + role))));

                CommandHandlers.broadcastMessage(showText);

            }

        }

        if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "help") || message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "commands")) {

            chat.sendMessage(BotConfig.getTwitchChannelName(), I18n.get("gui.twitchmod.commands_link"));

        } else if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "modlink")) {

            chat.sendMessage(BotConfig.getTwitchChannelName(), I18n.get("gui.twitchmod.mod_link"));

        } else if (message.startsWith(BotConfig.getCommandPrefix() + "blacklist")) {

            message = message.substring(BotConfig.getCommandPrefix().length());

            if (Objects.equals(role, "Moderator") || Objects.equals(role, "Broadcaster") && message.contains(" ")) {

                String cmd = message.substring(11);

                if (cmd.startsWith("add ")) {
                    BlacklistSystem.addToBlacklist(cmd.substring(4));
                } else if (cmd.startsWith("remove ")) {
                    BlacklistSystem.removeFromBlacklist(cmd.substring(7));
                } else if (cmd.equalsIgnoreCase("clear")) {
                    BlacklistSystem.clearBlacklist();
                    chat.sendMessage(BotConfig.getTwitchChannelName(), I18n.get("gui.twitchmod.blacklist_cleared"));
                }

            }
            chat.sendMessage(BotConfig.getTwitchChannelName(), I18n.get("gui.twitchmod.blacklisted_commands", BlacklistSystem.getBlacklist().toString()));

        } else if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "disconnect")) {
            TwitchConnectionHelper.disconnectBot();

        } else if (message.equalsIgnoreCase(BotConfig.getCommandPrefix() + "reconnect")) {

            TwitchConnectionHelper.login();

        } else if (message.startsWith(BotConfig.getCommandPrefix()) || ChatPicker.logMessages) {

            ChatPickerHelper.checkChatThreaded(message, sender);

        }

    }


    public void onConnect(ChannelJoinEvent event) {
        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.connected_success_twitch").withStyle(ChatFormatting.DARK_GREEN));
        Main.logger.info("Twitch Client joined channel: " + event.getChannel().getName());
    }

}
