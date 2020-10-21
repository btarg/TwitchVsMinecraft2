package com.icrazyblaze.twitchmod.chat;

import com.icrazyblaze.twitchmod.CommandHandlers;
import com.icrazyblaze.twitchmod.Main;
import com.icrazyblaze.twitchmod.util.BotConfig;
import com.icrazyblaze.twitchmod.util.PlayerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
    public static boolean instantCommands = false;
    public static boolean enabled = true;
    private static File textfile;
    private static boolean hasExecuted = false;
    private static String lastCommand = null;

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
        if (forceCommands || instantCommands) {

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
                    Main.logger.info(String.format("Command not executed: cooldown is active for this command (%s).", message));
                }

            } else {

                newChats.add(message);
                newChatSenders.add(sender);

            }

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
            // If we find the command in the map, then we pass it the full message with the arguments.
            String finalmessage;
            if (message.contains(" ")) {

                // Get everything before space (e.g. "messagebox")
                String[] split = message.split("\\s+");
                finalmessage = split[0];

            } else {
                finalmessage = message;
            }

            // Special commands below have extra arguments, so they are registered here.
            registerCommand(() -> CommandHandlers.messWithInventory(sender), "itemroulette", "roulette");
            registerCommand(() -> CommandHandlers.shuffleInventory(sender), "shuffle");
            // UPDATE: moved here from if-else block
            registerCommand(() -> CommandHandlers.showMessagebox(message), "messagebox");
            registerCommand(() -> CommandHandlers.addToMessages(message), "addmessage");
            registerCommand(() -> CommandHandlers.placeSign(message), "sign");
            registerCommand(() -> CommandHandlers.renameItem(message), "rename");

            try {
                // Invoke command from command map
                commands.get(finalmessage).run();

                if (BotConfig.showChatMessages && BotConfig.showCommands) {
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
     * Adds a command to a list that ChatPicker checks.
     * The registerCommand method takes two arguments: a runnable, and any number of command aliases.
     * <pre>
     * {@code
     *     registerCommand(() -> BotCommands.myCommand(), "mycommand", "mycommandalias");
     * }
     * </pre>
     * If an entry with the same runnable or alias already exists, it will be replaced.
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
            if (commands.containsKey(key) && commands.containsValue(runnable)) {
                commands.replace(key, runnable);
            } else {
                commands.put(key, runnable);
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
     * Commands are registered here from doCommands.
     */
    public static void initCommands() {

        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.POISON, 400, 0)}), "poison");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.HUNGER, 800, 255)}), "hunger");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.SLOWNESS, 400, 5)}), "slowness");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.BLINDNESS, 400, 0)}), "blindness", "jinkies");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.SPEED, 400, 10)}), "speed", "gottagofast");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.NAUSEA, 400, 0)}), "nausea", "dontfeelsogood");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.MINING_FATIGUE, 400, 0)}), "fatigue");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.LEVITATION, 200, 1)}), "levitate", "fly");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.LEVITATION, 400, 255)}), "nofall", "float");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.LEVITATION, 200, 1)}), "levitate", "fly");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.HEALTH_BOOST, 400, 1), new EffectInstance(Effects.REGENERATION, 400, 1)}), "regen", "heal", "health");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.SATURATION, 200, 255)}), "saturation", "feed");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.JUMP_BOOST, 400, 2)}), "jumpboost", "yeet");
        registerCommand(() -> CommandHandlers.addPotionEffects(new EffectInstance[]{new EffectInstance(Effects.HASTE, 400, 2)}), "haste", "diggydiggy");
        registerCommand(CommandHandlers::clearEffects, "cleareffects", "milk");
        registerCommand(CommandHandlers::setOnFire, "fire", "burn");
        registerCommand(CommandHandlers::floorIsLava, "lava", "floorislava");
        registerCommand(CommandHandlers::placeWater, "water", "watersbroke");
        registerCommand(CommandHandlers::placeSponge, "sponge");
        registerCommand(CommandHandlers::deathTimer, "timer", "deathtimer");
        registerCommand(CommandHandlers::graceTimer, "peacetimer", "timeout");
        registerCommand(CommandHandlers::frenzyTimer, "frenzy", "frenzymode", "suddendeath");
        registerCommand(CommandHandlers::drainHealth, "drain", "halfhealth");
        registerCommand(CommandHandlers::spawnAnvil, "anvil"); // Gaiet's favourite command <3
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.CREEPER.create(PlayerHelper.player().world)), "creeper", "awman");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.ZOMBIE.create(PlayerHelper.player().world)), "zombie");
        registerCommand(() -> CommandHandlers.spawnMob(EntityType.ENDERMAN.create(PlayerHelper.player().world)), "enderman");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.WITCH.create(PlayerHelper.player().world)), "witch");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.SKELETON.create(PlayerHelper.player().world)), "skeleton");
        registerCommand(CommandHandlers::spawnArmorStand, "armorstand", "armourstand", "boo");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.0F), "creeperscare", "behindyou");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F), "zombiescare", "bruh");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F), "skeletonscare", "spook");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_WITCH_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F), "witchscare", "hehe");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, 1.0F), "ghastscare", "yikes");
        registerCommand(CommandHandlers::pigmanScare, "pigmanscare", "aggro");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F), "anvilscare");
        registerCommand(CommandHandlers::spawnLightning, "lightning");
        registerCommand(CommandHandlers::spawnFireball, "fireball");
        registerCommand(() -> CommandHandlers.oresExplode = true, "oresexplode");
        registerCommand(() -> CommandHandlers.placeBedrock = true, "bedrock");
        registerCommand(() -> CommandHandlers.killVillagers = true, "villagersburn", "burnthemall");
        registerCommand(() -> CommandHandlers.destroyWorkbenches = true, "nocrafting", "breakworkbench");
        registerCommand(CommandHandlers::breakBlock, "break");
        registerCommand(CommandHandlers::dismount, "dismount", "getoff");
        registerCommand(CommandHandlers::dropItem, "drop", "throw");
        registerCommand(CommandHandlers::dropAll, "dropall");
        registerCommand(CommandHandlers::infestBlock, "silverfish");
        registerCommand(CommandHandlers::setRainAndThunder, "rain", "shaun");
        registerCommand(() -> CommandHandlers.setDifficulty(Difficulty.HARD), "hardmode", "isthiseasymode");
        registerCommand(() -> CommandHandlers.setDifficulty(Difficulty.PEACEFUL), "peaceful", "peacefulmode");
        registerCommand(CommandHandlers::placeChest, "chest", "lootbox");
        registerCommand(() -> CommandHandlers.setTime(1000), "day", "setday");
        registerCommand(() -> CommandHandlers.setTime(13000), "night", "setnight");
        registerCommand(CommandHandlers::spawnCobweb, "cobweb", "stuck", "gbj");
        registerCommand(CommandHandlers::setSpawn, "spawnpoint", "setspawn");
        registerCommand(CommandHandlers::placeGlass, "glass");
        registerCommand(CommandHandlers::enchantItem, "enchant");
        registerCommand(CommandHandlers::curseItem, "bind", "curse");

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