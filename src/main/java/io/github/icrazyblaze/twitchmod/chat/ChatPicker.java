package io.github.icrazyblaze.twitchmod.chat;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.bots.BotCommon;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.util.files.BlacklistSystem;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * This class is responsible for picking commands from chat and running them.
 *
 * @see io.github.icrazyblaze.twitchmod.chat.ChatCommands
 */
public class ChatPicker {

    private static final String[] blankMessagePlaceholders = {" says hello!", " was here"};
    public static ArrayList<String> chatBuffer = new ArrayList<>();
    public static ArrayList<String> chatSenderBuffer = new ArrayList<>();
    public static boolean cooldownEnabled = false;
    public static boolean forceCommands = false;
    public static boolean instantCommands = false;
    public static boolean enabled = true;
    public static boolean logMessages = false;
    public static ArrayList<String> tempChatLog = new ArrayList<>();
    public static int chatLogLength = 10;
    private static String lastCommand = null;

    /**
     * Checks the command against the blacklist, unless force commands is enabled.
     * If the chat should be logged for writing into a book then that is also done here.
     *
     * @param message The chat message
     * @param sender  The sender's name
     */
    public static void checkChat(String message, String sender) {

        if (!enabled)
            return;

        // Remove the prefix
        if (message.startsWith(BotConfig.prefix)) {
            message = message.substring(BotConfig.prefix.length());

            if (!ChatCommands.commandMap.containsKey(message))
                return;

        } else if (logMessages) {

            // If a message is not a command and temp logging is enabled, log the message
            String timeStamp = new SimpleDateFormat("[HH:mm:ss] ").format(new Date());
            tempChatLog.add(timeStamp + sender + ": " + message);

            // Add messages to book when there are enough
            if (tempChatLog.size() == chatLogLength) {

                // Add the chat messages to the book then stop recording chat
                CommandHandlers.createBook(tempChatLog);
                tempChatLog.clear();
                logMessages = false;

            }
            return;
        }

        // Skip checking if force commands is enabled
        if (forceCommands || instantCommands) {

            doCommandMultiplayer(message, sender);
            return;

        }


        // Only add the message if it is not blacklisted, and if the command isn't the same as the last
        if (BlacklistSystem.isBlacklisted(message)) {
            Main.logger.info("Command not executed: command is blacklisted.");
            return;
        }
        if (lastCommand != null && cooldownEnabled) {

            if (!message.equalsIgnoreCase(lastCommand)) {

                chatBuffer.add(message);
                chatSenderBuffer.add(sender);

            } else {
                Main.logger.info(String.format("Command not executed: cooldown is active for this command (%s).", message));
            }

        } else {

            chatBuffer.add(message);
            chatSenderBuffer.add(sender);

        }

    }


    /**
     * Attempts to run a command for every player in the affected players list.
     *
     * @param message The chat command, e.g. "!creeper"
     * @param sender  The sender's name, which is used in some commands.
     * @return If the command doesn't run, then this method returns false.
     * @since 3.5.0
     */
    public static boolean doCommandMultiplayer(String message, String sender) {

        // Get all of the players from a list and set the player's username before executing.
        // This means we can have multiple players affected!

        if (PlayerHelper.defaultServer.getPlayerList().getPlayers().size() < 2 || PlayerHelper.affectedPlayers.size() > 2) {
            return doCommand(message, sender);
        }

        try {
            for (String playername : PlayerHelper.affectedPlayers) {

                PlayerHelper.setUsername(playername);

                if (!doCommand(message, sender)) {
                    return false;
                }

            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Attempts to parse and then execute a command.
     *
     * @param message The chat command, e.g. "!creeper"
     * @param sender  The sender's name, which is used in some commands.
     * @return If the command doesn't run, then this method returns false.
     */
    public static boolean doCommand(String message, String sender) {

        if (!PlayerHelper.player().level.isClientSide()) {

            // If the command contains a space, everything after the space is treated like an argument.
            // We chop of the arguments, and check the map for the command.

            // UPDATE: we now use the second part of this split to avoid cutting the commands off the beginning in actual functions.
            // e.g. before, showMessageBox() would be sent the whole message from twitch chat, and then substring the word "messagebox".

            // Trim to avoid splitting on accidental spaces
            message = message.trim();

            String commandString;
            String argString;
            if (message.contains(" ")) {

                // Split at the space
                String[] split = message.split("\\s+");

                commandString = split[0]; // Before space (e.g. "messagebox")
                argString = message.substring(commandString.length()).trim();

            } else {
                commandString = message;
                argString = sender + blankMessagePlaceholders[PlayerHelper.player().getRandom().nextInt(blankMessagePlaceholders.length)];
            }

            // Special commands below have extra arguments, so they are registered here.
            ChatCommands.initDynamicCommands(argString, sender);

            try {
                // Invoke command from command map
                ChatCommands.commandMap.get(commandString).run();

                if (BotConfig.showCommandsInChat) {
                    if (BotConfig.showChatMessages) {
                        CommandHandlers.broadcastMessage(new TextComponent(ChatFormatting.AQUA + "Command Chosen: " + BotConfig.prefix + message));
                    }
                    BotCommon.sendBotMessage("Command Chosen: " + BotConfig.prefix + message);
                }

                // Below will not be executed if the command does not run
                lastCommand = message;
                return true;

            } catch (Exception e) {
                commandFailed();
            }

        }

        return false;

    }

    /**
     * Picks a random chat message, and checks if it is a command.
     * If the chat message is a command, it will be run. Otherwise, a new message is picked.
     */
    public static void pickRandomChat() {

        if (!chatBuffer.isEmpty()) {

            String message;
            String sender;
            Random rand = new Random();
            int listRandom = rand.nextInt(chatBuffer.size());

            message = chatBuffer.get(listRandom);
            sender = chatSenderBuffer.get(listRandom);

            ChatCommands.commandHasExecuted = doCommandMultiplayer(message, sender);

            // If command is invalid
            if (!ChatCommands.commandHasExecuted) {

                chatBuffer.remove(listRandom);
                commandFailed();

            }

            chatBuffer.clear();

        }

    }

    public static void commandFailed() {

        if (!ChatCommands.commandHasExecuted) {
            if (!chatBuffer.isEmpty()) {
                // Choose another if the list is big enough
                pickRandomChat();
            } else {
                Main.logger.error("Failed to execute a command.");
            }
        }

    }
}
