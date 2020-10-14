package com.icrazyblaze.twitchmod.chat;

import com.icrazyblaze.twitchmod.BotCommands;
import com.icrazyblaze.twitchmod.Main;
import com.icrazyblaze.twitchmod.util.BotConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Difficulty;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;


/**
 * This class is responsible for picking commands from chat and running them.
 */
public class ChatPicker {

    private static final Supplier<Path> path = () -> FMLPaths.CONFIGDIR.get().resolve("twitch-blacklist.txt");
    private static final Map<String, Runnable> commands = new HashMap<>();
    public static List<String> blacklist;
    public static ArrayList<String> newChats = new ArrayList<>();
    public static ArrayList<String> newChatSenders = new ArrayList<>();
    public static boolean cooldownEnabled = false;
    public static boolean forceCommands = false;
    public static boolean enabled = true;
    private static File textfile;
    private static boolean hasExecuted = false;
    private static String lastCommand = null;

    /**
     * Loads the blacklist file, or creates the file if it doesn't already exist.
     */
    public static void loadBlacklistFile() {

        textfile = path.get().toFile();
        try {

            textfile.createNewFile(); // Create file if it doesn't already exist
            blacklist = Files.readAllLines(path.get()); // Read into list

            // Remove all empty objects
            blacklist.removeAll(Arrays.asList("", null));

        } catch (IOException e) {
            Main.logger.error(e);
        }

        // Fix for blacklist being null - set to empty instead
        if (blacklist == null) {
            blacklist = Collections.emptyList();
        }

    }

    /**
     * @param toAdd The string to add to the blacklist file.
     */
    public static void addToBlacklist(String toAdd) {

        try {

            // Append to file
            FileWriter fr = new FileWriter(textfile, true);

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
     * Clears the blacklist file.
     */
    public static void clearBlacklist() {

        try {

            // Clear text file using PrintWriter
            PrintWriter pr = new PrintWriter(textfile);
            pr.close();

            // Update from file
            loadBlacklistFile();

        } catch (IOException e) {
            Main.logger.error(e);
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

        // Skip checking if force commands is enabled
        if (forceCommands) {

            doCommand(message, sender);
            return;

        }

        // Only add the message if it is not blacklisted, and if the command isn't the same as the last
        loadBlacklistFile();

        if (!blacklist.isEmpty()) {

            for (String str : blacklist) {

                if (message.contains(str)) {
                    Main.logger.info("Command not executed: command is blacklisted.");
                    break;
                } else {

                    if (lastCommand != null && cooldownEnabled) {

                        if (!message.equalsIgnoreCase(lastCommand)) {

                            newChats.add(message);
                            newChatSenders.add(sender);

                        } else {
                            Main.logger.info("Command not executed: cooldown is active for this command.");
                        }

                    } else {

                        newChats.add(message);
                        newChatSenders.add(sender);

                    }
                    break;
                }
            }

        }
        // Fix for empty blacklist bug: accept any message (also runs cooldown check)
        else {

            if (lastCommand != null && cooldownEnabled) {

                if (!message.equalsIgnoreCase(lastCommand)) {

                    newChats.add(message);
                    newChatSenders.add(sender);

                } else {
                    Main.logger.info("Command not executed: cooldown is active for this command.");
                }

            } else {

                newChats.add(message);
                newChatSenders.add(sender);

            }

        }

    }


    /**
     * Picks a random chat message, and checks if it is a command.
     * If the chat message is a command, it will be run. Otherwise, a new message is picked.
     */
    public static void pickRandomChat() {

        if (!newChats.isEmpty()) {

            String message;
            String sender;
            Random rand = new Random();
            int listRandom = rand.nextInt(newChats.size());

            message = newChats.get(listRandom);
            sender = newChatSenders.get(listRandom);

            hasExecuted = doCommand(message, sender);

            // If command is invalid
            if (!hasExecuted) {

                newChats.remove(listRandom);
                commandFailed();

            }

            newChats.clear();

        }

    }

    /**
     * Adds a command to a list that ChatPicker checks.
     * The registerCommand method takes two arguments: a runnable, and any number of command aliases.
     * <pre>
     * {@code
     *     registerCommand(() -> BotCommands.myCommand(), "mycommand", "mycommandalias");
     * }
     * </pre>
     * IDEA will swap the lambda for a method reference wherever possible.
     *
     * @param runnable The function linked to the command
     * @param keys     Aliases for the command
     */
    public static void registerCommand(Runnable runnable, String... keys) {

        /*
        This code is used to add multiple aliases for commands using hashmaps.
        Thank you gigaherz, very cool!
        */
        for (String key : keys) {

            // Don't register exactly the same command every time
            // UPDATE: if a command with the same key already exists, replace it.
            if (commands.containsKey(key) && commands.containsValue(runnable)) {
                commands.replace(key, runnable);
            } else {
                commands.put(key, runnable);
            }

        }

    }

    /**
     * Commands are registered here from doCommands.
     */
    public static void initCommands() {

        registerCommand(BotCommands::addPoison, "poison");
        registerCommand(BotCommands::addHunger, "hunger");
        registerCommand(BotCommands::addSlowness, "slowness");
        registerCommand(BotCommands::addBlindness, "blindness", "jinkies");
        registerCommand(BotCommands::addSpeed, "speed", "gottagofast");
        registerCommand(BotCommands::addNausea, "nausea", "dontfeelsogood");
        registerCommand(BotCommands::addFatigue, "fatigue");
        registerCommand(BotCommands::addLevitation, "levitate", "fly");
        registerCommand(BotCommands::noFall, "nofall", "float");
        registerCommand(BotCommands::addWeakness, "weakness");
        registerCommand(BotCommands::addRegen, "regen", "heal", "health");
        registerCommand(BotCommands::addSaturation, "saturation", "feed");
        registerCommand(BotCommands::addJumpBoost, "jumpboost", "yeet");
        registerCommand(BotCommands::addHaste, "haste", "diggydiggy");
        registerCommand(BotCommands::clearEffects, "cleareffects", "milk");
        registerCommand(BotCommands::setOnFire, "fire", "burn");
        registerCommand(BotCommands::floorIsLava, "lava", "floorislava");
        registerCommand(BotCommands::waterBucket, "water", "watersbroke");
        registerCommand(BotCommands::placeSponge, "sponge");
        registerCommand(BotCommands::deathTimer, "timer", "deathtimer");
        registerCommand(BotCommands::graceTimer, "peacetimer", "timeout");
        registerCommand(BotCommands::drainHealth, "drain", "halfhealth");
        registerCommand(BotCommands::spawnAnvil, "anvil"); // Gaiet's favourite command <3
        registerCommand(() -> BotCommands.spawnMobBehind(EntityType.CREEPER.create(BotCommands.player().world)), "creeper", "awman");
        registerCommand(() -> BotCommands.spawnMobBehind(EntityType.ZOMBIE.create(BotCommands.player().world)), "zombie");
        registerCommand(() -> BotCommands.spawnMob(EntityType.ENDERMAN.create(BotCommands.player().world)), "enderman");
        registerCommand(() -> BotCommands.spawnMobBehind(EntityType.WITCH.create(BotCommands.player().world)), "witch");
        registerCommand(() -> BotCommands.spawnMobBehind(EntityType.SKELETON.create(BotCommands.player().world)), "skeleton");
        registerCommand(BotCommands::spawnArmorStand, "armorstand", "armourstand", "boo");
        registerCommand(BotCommands::creeperScare, "creeperscare", "behindyou");
        registerCommand(BotCommands::zombieScare, "zombiescare", "bruh");
        registerCommand(BotCommands::skeletonScare, "skeletonscare", "spook");
        registerCommand(BotCommands::witchScare, "witchscare", "hehe");
        registerCommand(BotCommands::ghastScare, "ghastscare", "yikes");
        registerCommand(BotCommands::pigmanScare, "pigmanscare", "aggro");
        registerCommand(BotCommands::anvilScare, "anvilscare");
        registerCommand(BotCommands::spawnLightning, "lightning");
        registerCommand(BotCommands::spawnFireball, "fireball");
        registerCommand(() -> BotCommands.oresExplode = true, "oresexplode");
        registerCommand(() -> BotCommands.placeBedrock = true, "bedrock");
        registerCommand(() -> BotCommands.killVillagers = true, "villagersburn", "burnthemall");
        registerCommand(() -> BotCommands.destroyWorkbenches = true, "nocrafting", "breakworkbench");
        registerCommand(BotCommands::breakBlock, "break");
        registerCommand(BotCommands::dismount, "dismount", "getoff");
        registerCommand(BotCommands::dropItem, "drop", "throw");
        registerCommand(BotCommands::dropAll, "dropall");
        registerCommand(BotCommands::monsterEgg, "silverfish");
        registerCommand(BotCommands::heavyRain, "rain", "shaun");
        registerCommand(() -> BotCommands.setDifficulty(Difficulty.HARD), "hardmode", "isthiseasymode");
        registerCommand(() -> BotCommands.setDifficulty(Difficulty.PEACEFUL), "peaceful", "peacefulmode");
        registerCommand(BotCommands::placeChest, "chest", "lootbox");
        registerCommand(() -> BotCommands.setTime(1000), "day", "setday");
        registerCommand(() -> BotCommands.setTime(13000), "night", "setnight");
        registerCommand(BotCommands::spawnCobweb, "cobweb", "stuck", "gbj");
        registerCommand(BotCommands::setSpawn, "spawnpoint", "setspawn");
        registerCommand(BotCommands::spawnGlass, "glass");
        registerCommand(BotCommands::enchantItem, "enchant");
        registerCommand(BotCommands::curseItem, "bind", "curse");

    }

    public static List<String> getRegisteredCommands() {

        List<String> commandList = new ArrayList<>();

        for (String key : commands.keySet()) {

            if (!blacklist.contains(key))
                commandList.add(key);

        }

        Collections.sort(commandList);
        return commandList;

    }

    /**
     * Attempts to run a command.
     *
     * @param message The actual command, e.g. "!creeper"
     * @param sender  The sender's name, which is used in some commands.
     * @return If the command doesn't run, then this method returns false.
     */
    public static boolean doCommand(String message, String sender) {

        if (!BotCommands.player().world.isRemote()) {

            // UPDATE: init no longer needed, everything is initialised properly now
            //initCommands();

            // Special commands below have extra arguments, so they are registered here.
            registerCommand(() -> BotCommands.messWithInventory(sender), "itemroulette", "roulette");

            try {

                if (message.startsWith("messagebox ") && message.length() > 11) {
                    BotCommands.showMessagebox(message);
                } else if (message.startsWith("addmessage ") && message.length() > 11) {
                    BotCommands.addToMessages(message);
                } else if (message.startsWith("sign ") && message.length() > 5) {
                    BotCommands.placeSign(message);
                } else if (message.startsWith("rename ") && message.length() > 7) {
                    BotCommands.renameItem(message);
                } else {

                    // Invoke command from message
                    commands.get(message).run();

                }

                if (BotConfig.showChatMessages && BotConfig.showCommands) {
                    BotCommands.broadcastMessage(new StringTextComponent(TextFormatting.AQUA + "Command Chosen: " + BotConfig.prefix + message));
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

    public static void commandFailed() {

        if (!hasExecuted) {
            if (!newChats.isEmpty()) {
                // Choose another if the list is big enough
                pickRandomChat();
            } else {
                newChats.clear();
                Main.logger.info("Failed to execute a command.");
                return;
            }
        }

    }

}