package io.github.icrazyblaze.twitchmod.bots.irc;


import com.google.common.collect.ImmutableMap;
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
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PingEvent;

import java.awt.*;
import java.util.Objects;


public class TwitchBot extends ListenerAdapter {

    public TwitchBot() {
        BlacklistSystem.loadBlacklistFile();
    }

    public void onConnect(ConnectEvent event) {
        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.connected_success_twitch").withStyle(ChatFormatting.DARK_GREEN));
        Main.logger.info("IRC Bot connected.");
    }

    public void onDisconnect(DisconnectEvent event) {
        CommandHandlers.broadcastMessage(new TranslatableComponent("gui.twitchmod.chat.disconnected_success_twitch").withStyle(ChatFormatting.DARK_RED));
        Main.logger.info("IRC Bot disconnected: " + event.getDisconnectException());
    }

    @Override
    public void onMessage(MessageEvent event) {

        String message = event.getMessage();
        String sender = Objects.requireNonNull(event.getUser()).getNick();
        ImmutableMap<String, String> tags = event.getV3Tags();

        ChatFormatting format = ChatFormatting.WHITE;

        MutableComponent showText;
        String role = null;

        if (BotConfig.showChatMessages) {

            ChatPicker.forceCommands = false;

            if (tags != null) {

                // Get hex colour, convert to RGB, then get nearest Minecraft colour code
                Color userColor = Color.decode(tags.get("color"));

                try {
                    format = CalculateMinecraftColor.findNearestMinecraftColor(userColor);
                } catch (Exception e) {
                    Main.logger.info("No valid user colour");
                }

                if (tags.get("badges").contains("broadcaster/1")) {
                    ChatPicker.forceCommands = true; // Force commands to execute instantly for broadcaster testing
                    role = "Broadcaster";
                } else if (tags.get("badges").contains("subscriber/1")) {
                    role = "Subscriber";
                } else if (tags.get("badges").contains("moderator/1")) {
                    role = "Moderator";
                }

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

            event.respond(I18n.get("gui.twitchmod.commands_link"));

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "modlink")) {

            event.respond(I18n.get("gui.twitchmod.mod_link"));

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
                    event.respond(I18n.get("gui.twitchmod.blacklist_cleared"));
                }

            }
            event.respond(I18n.get("gui.twitchmod.blacklisted_commands", BlacklistSystem.getBlacklist().toString()));

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "disconnect")) {
            TwitchConnectionHelper.disconnectBot();

        } else if (message.equalsIgnoreCase(BotConfig.prefix + "reconnect")) {

            TwitchConnectionHelper.login();

        } else if (message.startsWith(BotConfig.prefix) || ChatPicker.logMessages) {

            ChatPickerHelper.checkChatThreaded(message, sender);

        }

    }

    // Prevent the bot from being kicked
    @Override
    public void onPing(PingEvent event) {
        TwitchConnectionHelper.getBot().sendRaw().rawLineNow(String.format("PONG %s\r\n", event.getPingValue()));
    }

}