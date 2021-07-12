package io.github.icrazyblaze.twitchmod.chat;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.integration.IntegrationWrapper;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Difficulty;

import java.util.*;

import static io.github.icrazyblaze.twitchmod.util.EffectInstanceHelper.effect;

/**
 * This class is where all the commands are registered for use in ChatPicker.
 *
 * @see io.github.icrazyblaze.twitchmod.chat.ChatPicker
 */
public class ChatCommands {
    static final Map<String, Runnable> commandMap = new HashMap<>();
    static boolean commandHasExecuted = false;

    /**
     * Adds a command to a list that ChatPicker checks.
     * The registerCommand method takes two arguments: a runnable, and any number of command aliases.
     * <pre>
     * {@code
     *     registerCommand(() -> CommandHandlers.myCommand(), "mycommand", "mycommandalias");
     * }
     * </pre>
     * If an entry with the same runnable or alias already exists, it will be replaced.
     * IDEA will swap the lambda for a method reference wherever possible.
     *
     * @param runnable The function linked to the command
     * @param keys     Aliases for the command
     * @see ChatPicker
     */
    public static void registerCommand(Runnable runnable, String... keys) {

        /*
        This code is used to add multiple aliases for commands using hashmaps.
        Thank you gigaherz, very cool!
        */
        for (String key : keys) {

            // Don't register exactly the same command every time
            if (commandMap.containsKey(key) && commandMap.containsValue(runnable)) {
                commandMap.replace(key, runnable);
            } else {
                commandMap.put(key, runnable);
            }

        }

    }

    /**
     * Commands are registered here from doCommands.
     */
    public static void initCommands() {

        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.POISON, 400, 0)), "poison");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.HUNGER, 400, 255)), "hunger");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.SLOWNESS, 400, 5)), "slowness");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.BLINDNESS, 400, 0)), "blindness", "jinkies");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.SPEED, 400, 10)), "speed", "gottagofast");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.NAUSEA, 400, 0)), "nausea", "dontfeelsogood");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.MINING_FATIGUE, 400, 0)), "fatigue");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.LEVITATION, 200, 1)), "levitate", "fly");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.LEVITATION, 400, 255)), "nofall", "float");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.LEVITATION, 200, 1)), "levitate", "fly");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.HEALTH_BOOST, 400, 1), effect(Effects.REGENERATION, 400, 1)), "regen", "heal", "health");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.SATURATION, 200, 255)), "saturation", "feed");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.JUMP_BOOST, 400, 2)), "jumpboost", "yeet");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.HASTE, 400, 2)), "haste", "diggydiggy");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.BAD_OMEN, 400, 0)), "badomen", "pillager", "raid");
        registerCommand(() -> CommandHandlers.addPotionEffects(effect(Effects.FIRE_RESISTANCE, 800, 0), effect(Effects.RESISTANCE, 800, 4)), "resistance");
        registerCommand(() -> PlayerHelper.player().clearActivePotions(), "cleareffects", "milk");
        registerCommand(CommandHandlers::setOnFire, "fire", "burn");
        registerCommand(CommandHandlers::floorIsLava, "lava", "floorislava");
        registerCommand(CommandHandlers::placeWater, "water", "watersbroke");
        registerCommand(CommandHandlers::placeSponge, "sponge");
        registerCommand(CommandHandlers::deathTimer, "timer", "deathtimer");
        registerCommand(CommandHandlers::graceTimer, "peacetimer", "timeout");
        registerCommand(CommandHandlers::drainHealth, "drain", "halfhealth");
        registerCommand(CommandHandlers::spawnAnvil, "anvil"); // Gaiet's favourite command <3
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.CREEPER.create(PlayerHelper.player().world)), "creeper", "awman");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.ZOMBIE.create(PlayerHelper.player().world)), "zombie");
        registerCommand(() -> CommandHandlers.spawnMob(EntityType.ENDERMAN.create(PlayerHelper.player().world)), "enderman");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.WITCH.create(PlayerHelper.player().world)), "witch");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.SKELETON.create(PlayerHelper.player().world)), "skeleton");
        registerCommand(() -> CommandHandlers.spawnMobBehind(EntityType.SLIME.create(PlayerHelper.player().world)), "slime");
        registerCommand(CommandHandlers::spawnArmorStand, "armorstand", "armourstand", "boo");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.0F), "creeperscare", "behindyou");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F), "zombiescare", "bruh");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F), "skeletonscare", "spook");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_WITCH_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F), "witchscare", "hehe");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, 1.0F), "ghastscare", "yikes");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_PHANTOM_AMBIENT, SoundCategory.HOSTILE, 10.0F, 1.0F), "phantomscare", "needsleep");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.ENTITY_WITHER_AMBIENT, SoundCategory.HOSTILE, 10.0F, 1.0F), "witherscare", "wither");
        registerCommand(CommandHandlers::pigmanScare, "pigmanscare", "aggro");
        registerCommand(CommandHandlers::elderGuardianScare, "guardian", "guardianscare");
        registerCommand(() -> CommandHandlers.playSound(SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F), "anvilscare");
        registerCommand(CommandHandlers::spawnLightning, "lightning");
        registerCommand(CommandHandlers::spawnFireball, "fireball");
        registerCommand(() -> CommandHandlers.oresExplode = true, "oresexplode");
        registerCommand(() -> CommandHandlers.placeBedrockOnBreak = true, "bedrock");
        registerCommand(() -> CommandHandlers.burnVillagersOnInteract = true, "villagersburn", "burnthemall");
        registerCommand(() -> CommandHandlers.destroyWorkbenchesOnInteract = true, "nocrafting", "breakworkbench");
        registerCommand(CommandHandlers::breakBlock, "break");
        registerCommand(CommandHandlers::dismount, "dismount", "getoff");
        registerCommand(CommandHandlers::dropItem, "drop", "throw");
        registerCommand(() -> PlayerHelper.player().inventory.dropAllItems(), "dropall");
        registerCommand(CommandHandlers::infestBlock, "silverfish");
        registerCommand(CommandHandlers::setRainAndThunder, "rain", "makeitrain");
        registerCommand(() -> CommandHandlers.setDifficulty(Difficulty.HARD), "hard", "hardmode");
        registerCommand(() -> CommandHandlers.setDifficulty(Difficulty.PEACEFUL), "peaceful", "peacefulmode");
        registerCommand(CommandHandlers::placeChest, "chest", "lootbox");
        registerCommand(() -> CommandHandlers.setTime(1000), "day", "setday");
        registerCommand(() -> CommandHandlers.setTime(13000), "night", "setnight");
        registerCommand(CommandHandlers::placeCobweb, "cobweb", "stuck", "gbj");
        registerCommand(CommandHandlers::setSpawn, "spawnpoint", "setspawn");
        registerCommand(() -> CommandHandlers.surroundPlayer(Blocks.GLASS.getDefaultState()), "glass");
        registerCommand(CommandHandlers::enchantItem, "enchant");
        registerCommand(CommandHandlers::curseArmour, "bind", "curse");
        registerCommand(CommandHandlers::startWritingBook, "book", "chatlog");
        registerCommand(CommandHandlers::toggleCrouch, "togglecrouch", "crouch");
        registerCommand(CommandHandlers::toggleSprint, "togglesprint", "sprint");
        registerCommand(CommandHandlers::pumpkin, "pumpkin");
        registerCommand(CommandHandlers::chorusTeleport, "chorusfruit", "chorus", "teleport");
        registerCommand(() -> CommandHandlers.changeDurability(false), "damage", "damageitem");
        registerCommand(() -> CommandHandlers.changeDurability(true), "repair", "repairitem");

    }

    /**
     * Commands that are registered here need to be re-added to the command registry every time they run because they have changing ("dynamic") elements.
     *
     * @param argString the argument for the command
     * @param sender    the name of the command sender
     */
    public static void initDynamicCommands(String argString, String sender) {

        registerCommand(() -> CommandHandlers.itemRoulette(sender), "itemroulette", "roulette");
        registerCommand(() -> CommandHandlers.shuffleInventory(sender), "shuffle");
        registerCommand(() -> CommandHandlers.showMessagebox(argString), "messagebox");
        registerCommand(() -> CommandHandlers.messagesList.add(argString), "addmessage");
        registerCommand(() -> CommandHandlers.placeSign(argString), "sign");
        registerCommand(() -> CommandHandlers.renameItem(argString), "rename");
        registerCommand(() -> CommandHandlers.rollTheDice(sender), "rtd", "roll", "dice");
        registerCommand(() -> FrenzyVote.vote(sender), "frenzy", "frenzymode", "suddendeath");

        // Mod dynamic commands
        IntegrationWrapper.initModDynamicCommands(sender);

    }

    public static List<String> getRegisteredCommands() {

        List<String> commandList = new ArrayList<>();

        for (String key : commandMap.keySet()) {

            if (!ChatPicker.blacklist.contains(key)) {
                commandList.add(key);
            }

        }

        Collections.sort(commandList);
        return commandList;

    }

}
