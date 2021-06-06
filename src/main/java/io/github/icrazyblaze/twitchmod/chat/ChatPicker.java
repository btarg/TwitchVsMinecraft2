package io.github.icrazyblaze.twitchmod.chat;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is responsible for picking commands from chat and running them.
 * Blacklist operations are done in this class.
 * <p>
 * As of version 3.4.0, command registering is now done in a separate class.
 *
 * @see io.github.icrazyblaze.twitchmod.chat.ChatCommands
 */
public class ChatPicker {

    private static final Supplier<Path> blacklistPath = () -> FMLPaths.CONFIGDIR.get().resolve("twitch-blacklist.txt");
    public static List<String> blacklist;
    public static ArrayList<String> chatBuffer = new ArrayList<>();
    public static ArrayList<String> chatSenderBuffer = new ArrayList<>();
    public static boolean cooldownEnabled = false;
    public static boolean forceCommands = false;
    public static boolean instantCommands = false;
    public static boolean enabled = true;
    public static boolean logMessages = false;
    public static ArrayList<String> tempChatLog = new ArrayList<>();
    public static int chatLogLength = 10;
    private static File blacklistTextFile;
    private static String lastCommand = null;

    /**
     * @param toAdd The string to add to the blacklist file.
     */
    public static void addToBlacklist(String toAdd) {

        if (blacklist.contains(toAdd)) {
            return;
        }

        try {

            // Append to file
            FileWriter fr = new FileWriter(blacklistTextFile, true);

            // New line fix
            fr.write(System.lineSeparator() + toAdd);

            fr.close();

            // Update from file
            loadBlacklistFile();

        } catch (IOException e) {
            Main.logger.error(e);
        }

    }

    /**
     * Loads the blacklist file, or creates the file if it doesn't already exist.
     */
    public static void loadBlacklistFile() {

        blacklistTextFile = blacklistPath.get().toFile();
        try {

            blacklistTextFile.createNewFile(); // Create file if it doesn't already exist
            blacklist = Files.readAllLines(blacklistPath.get()); // Read into list

            // Remove all empty objects
            blacklist.removeAll(Arrays.asList("", null));

            // Remove prefixes from the start of commands in the blacklist
            for (int i = 0; i < blacklist.size(); i++) {
                if (blacklist.get(i).startsWith(BotConfig.prefix)) {

                    blacklist.set(i, blacklist.get(i).substring(BotConfig.prefix.length()));

                }
            }

        } catch (IOException e) {
            Main.logger.error(e);
        }

        // Fix for blacklist being null - set to empty instead
        if (blacklist == null) {
            blacklist = Collections.emptyList();
        }

    }

    /**
     * Clears the blacklist file.
     */
    public static void clearBlacklist() {

        try {

            // Clear text file using PrintWriter
            PrintWriter pr = new PrintWriter(blacklistTextFile);
            pr.close();

            // Update from file
            loadBlacklistFile();

        } catch (IOException e) {
            Main.logger.error(e);
        }

    }

    public static boolean isBlacklisted(String command) {

        if (!blacklist.isEmpty()) {
            return blacklist.contains(command);
        } else {
            return false;
        }

    }

    /**
     * Checks the command against the blacklist, unless force commands is enabled.
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

            doCommand(message, sender);
            return;

        }


        // Only add the message if it is not blacklisted, and if the command isn't the same as the last
        if (isBlacklisted(message)) {
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
     * Attempts to parse and then execute a command.
     *
     * @param message The chat command, e.g. "!creeper"
     * @param sender  The sender's name, which is used in some commands.
     * @return If the command doesn't run, then this method returns false.
     */
    public static boolean doCommand(String message, String sender) {

        if (!PlayerHelper.player().world.isRemote()) {

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
                argString = message.substring(commandString.length());

            } else {
                commandString = message;
                argString = sender + " says hello!";
            }

            // Special commands below have extra arguments, so they are registered here.
            ChatCommands.initDynamicCommands(argString, sender);

            try {
                // Invoke command from command map
                ChatCommands.commandMap.get(commandString).run();

                if (BotConfig.showChatMessages && BotConfig.showCommandsInChat) {
                    CommandHandlers.broadcastMessage(new StringTextComponent(TextFormatting.AQUA + "Command Chosen: " + BotConfig.prefix + message));
                }

                // Below will not be executed if the command does not run
                lastCommand = message;
                return true;

            } catch (Exception e) {
                e.printStackTrace();
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

            ChatCommands.commandHasExecuted = doCommand(message, sender);

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
