package io.github.icrazyblaze.twitchmod.util.files;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is responsible for reading from and writing to the blacklist file.
 *
 * @see io.github.icrazyblaze.twitchmod.chat.ChatPicker
 */
public class BlacklistSystem {

    private static final Supplier<Path> blacklistPath = () -> FMLPaths.CONFIGDIR.get().resolve("command-blacklist.txt");
    private static final File blacklistTextFile = blacklistPath.get().toFile();
    private static List<String> blacklist;

    public static List<String> getBlacklist() {
        return blacklist;
    }

    /**
     * Adds a chat command to the blacklist.
     *
     * @param toAdd The string to add to the blacklist and its text file.
     */
    public static void addToBlacklist(String toAdd) {
        if (blacklist.contains(toAdd)) {
            return;
        }
        blacklist.add(toAdd);
        writeBlacklistToFile();
    }

    /**
     * Removes a chat command from the blacklist.
     *
     * @param toRemove The string to remove from the blacklist and its text file.
     */
    public static void removeFromBlacklist(String toRemove) {
        if (!blacklist.contains(toRemove)) {
            return;
        }
        blacklist.removeAll(Collections.singleton(toRemove));
        writeBlacklistToFile();
    }

    /**
     * Writes the contents of the blacklist to a text file.
     */
    public static void writeBlacklistToFile() {

        try {
            FileWriter writer = new FileWriter(blacklistTextFile);

            for (String str : blacklist) {

                // Remove prefixes when writing to the file for consistency
                if (str.startsWith(BotConfig.prefix)) {
                    str = str.substring(BotConfig.prefix.length());
                }

                writer.write(str + System.lineSeparator());
            }
            writer.close();

        } catch (IOException e) {
            Main.logger.error(new TranslatableComponent("exception.twitchmod.blacklist_load_exception", e));
        }

    }

    /**
     * Loads the blacklist file, or creates the file if it doesn't already exist.
     */
    public static void loadBlacklistFile() {

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
            Main.logger.error(new TranslatableComponent("exception.twitchmod.blacklist_load_exception", e));
        }

        // Fix for blacklist being null - set to empty instead
        if (blacklist == null) {
            blacklist = Collections.emptyList();
        }

    }

    /**
     * Clears the blacklist and its text file.
     */
    public static void clearBlacklist() {

        try {

            // Clear text file using PrintWriter
            PrintWriter pr = new PrintWriter(blacklistTextFile);
            pr.close();

            blacklist.clear();

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
}
