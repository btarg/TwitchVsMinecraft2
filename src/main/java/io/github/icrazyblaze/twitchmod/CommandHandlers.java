package io.github.icrazyblaze.twitchmod;

import io.github.icrazyblaze.twitchmod.chat.ChatCommands;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.network.MessageboxPacket;
import io.github.icrazyblaze.twitchmod.network.PacketHandler;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.icrazyblaze.twitchmod.util.PlayerHelper.player;

/**
 * This class contains every method used by commands registered in the ChatPicker class.
 *
 * @see io.github.icrazyblaze.twitchmod.chat.ChatPicker
 */
@SuppressWarnings("unchecked")
public class CommandHandlers {

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
    public static boolean oresExplode = false;
    public static boolean placeBedrockOnBreak = false;
    public static boolean burnVillagersOnInteract = false;
    public static boolean destroyWorkbenchesOnInteract = false;
    public static ArrayList<String> messagesList = new ArrayList<>();
    public static boolean enableFrenzyMode = true;
    private static ResourceLocation[] lootArray = new ResourceLocation[0];
    private static boolean previousDeathTimerState = false;

    static {
        try {
            lootArray = getLootTables();
        } catch (Exception e) {
            Main.logger.error(e);
        }
    }

    public static ResourceLocation[] getLootTables() {

        LootTables obj = player().getServer().getLootTables();
        Set<ResourceLocation> tables = obj.getIds();

        List<ResourceLocation> tablesCopy = new ArrayList<>(tables);
        tablesCopy.removeIf(table -> table.toString().contains("blocks") || table.toString().equalsIgnoreCase("minecraft:empty"));

        return tablesCopy.toArray(new ResourceLocation[0]);

    }


    public static void rollTheDice(String sender) {

        List<String> commands = ChatCommands.getRegisteredCommands();
        String randomCommand = commands.get(rand.nextInt(commands.toArray().length));
        broadcastMessage(new TextComponent(sender + " rolled the dice!"));
        ChatPicker.checkChat(randomCommand, sender);

    }


    public static void addPotionEffects(MobEffectInstance... effectInstances) {

        ServerPlayer player = player();

        for (MobEffectInstance effect : effectInstances) {
            player.addEffect(effect);
        }

    }

    public static void setBlock(BlockPos bpos, BlockState state) {
        player().level.setBlockAndUpdate(bpos, state);
    }


    public static void setOnFire() {

        ServerPlayer player = player();

        BlockPos bpos = player.blockPosition();

        BlockState bposState = player.level.getBlockState(bpos);

        if (bposState == Blocks.AIR.defaultBlockState()) {
            setBlock(bpos, Blocks.FIRE.defaultBlockState());
        }

        player.setSecondsOnFire(10);

    }

    public static void setRainAndThunder() {

        ServerPlayer player = player();
        player.level.getLevelData().setRaining(true);

        if (!player.level.isClientSide()) {
            player.getLevel().setWeatherParameters(0, 6000, true, true);
        }
    }

    public static void setDifficulty(Difficulty difficulty) {

        Objects.requireNonNull(player().getServer()).setDifficulty(difficulty, false);

    }

    public static void setTime(long time) {

        Iterable<ServerLevel> worlds = player().server.getAllLevels();

        for (ServerLevel world : worlds) {
            world.setDayTime(time);
        }

    }

    public static void drainHealth() {

        ServerPlayer player = player();

        // Half the player's health
        float halfhealth = player.getHealth() / 2;

        if (halfhealth == 0) {
            player.kill();
        } else {
            player.setHealth(halfhealth);
        }

    }


    public static void setSpawn() {

        ServerPlayer player = player();

        BlockPos bpos = new BlockPos(player.getX(), player.getY(), player.getZ());
        // SetSpawn
        player.setRespawnPosition(player.level.dimension(), bpos, 0.0F, false, true);

    }

    public static void deathTimer() {

        if (ChatPicker.instantCommands) {
            return;
        }

        TimerSystem.deathTimerSeconds = 60;
        TimerSystem.deathTimerEnabled = true;

        player().displayClientMessage(new TextComponent(ChatFormatting.DARK_RED + "Chat has given you " + TimerSystem.deathTimerSeconds + " seconds to live."), true);

    }

    public static void frenzyTimer() {

        if (ChatPicker.instantCommands || !enableFrenzyMode) {
            return;
        }

        TimerSystem.frenzyTimerSeconds = 10;
        ChatPicker.instantCommands = true;

        previousDeathTimerState = TimerSystem.deathTimerEnabled;
        TimerSystem.deathTimerEnabled = false;

        player().displayClientMessage(new TextComponent(ChatFormatting.GOLD + "FRENZY MODE! All commands are executed for the next " + TimerSystem.frenzyTimerSeconds + " seconds."), true);

    }

    public static void graceTimer() {

        if (ChatPicker.instantCommands) {
            return;
        }

        ChatPicker.enabled = false;
        TimerSystem.peaceTimerSeconds = 30;
        TimerSystem.peaceTimerEnabled = true;

        previousDeathTimerState = TimerSystem.deathTimerEnabled;
        TimerSystem.deathTimerEnabled = false;

        player().displayClientMessage(new TextComponent(ChatFormatting.AQUA + "Commands are turned off for " + TimerSystem.peaceTimerSeconds + " seconds."), true);

    }

    public static void disableGraceTimer() {

        ChatPicker.enabled = true;
        TimerSystem.peaceTimerEnabled = false;
        TimerSystem.deathTimerEnabled = previousDeathTimerState;

        player().displayClientMessage(new TextComponent(ChatFormatting.AQUA + "Commands are now enabled!"), true);

    }

    public static void disableFrenzyTimer() {

        ChatPicker.instantCommands = false;
        TimerSystem.deathTimerEnabled = previousDeathTimerState;
        player().displayClientMessage(new TextComponent(ChatFormatting.GOLD + "Frenzy mode is now disabled."), true);

    }

    public static void floorIsLava() {

        ServerPlayer player = player();

        BlockPos bpos = new BlockPos(player.getX(), player.getY() - 1, player.getZ());
        setBlock(bpos, Blocks.LAVA.defaultBlockState());

    }

    public static void placeWater() {

        ServerPlayer player = player();

        BlockPos bpos = player.blockPosition();
        setBlock(bpos, Blocks.WATER.defaultBlockState());

    }

    public static void placeSponge() {

        ServerPlayer player = player();

        BlockPos bpos = new BlockPos(player.getX(), player.getY(), player.getZ());

        setBlock(bpos, Blocks.SPONGE.defaultBlockState());

    }

    public static void spawnAnvil() {

        ServerPlayer player = player();

        BlockPos bpos = new BlockPos(player.getX(), player.getY() + 16, player.getZ());

        setBlock(bpos, Blocks.ANVIL.defaultBlockState());

    }

    public static void placeCobweb() {

        ServerPlayer player = player();

        setBlock(player.blockPosition().above(), Blocks.COBWEB.defaultBlockState());
        setBlock(player.blockPosition(), Blocks.COBWEB.defaultBlockState());

    }

    public static void spawnMob(Entity ent) {

        ServerPlayer player = player();

        Vec3 lookVector = player.getLookAngle();

        double dx = player.getX() + (lookVector.x * 4);
        double dz = player.getZ() + (lookVector.z * 4);

        ent.setPos(dx, player.getY(), dz);

        player.level.addFreshEntity(ent);

    }


    public static void pigmanScare() {
        playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundSource.HOSTILE, 2.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
    }

    public static void elderGuardianScare() {
        player().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 1.0F));
    }

    public static void playSound(SoundEvent sound, SoundSource category, float volume, float pitch) {

        ServerPlayer player = player();
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, category, volume, pitch);

    }

    public static void spawnFireball() {

        ServerPlayer player = player();

        LargeFireball ent = new LargeFireball(player.level, player, 0D, 0D, 0D, 4);

        ent.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 1.0F);
        ent.moveTo(player.getX(), player.getY(0.5), player.getZ(), 0, 0);

        player.level.addFreshEntity(ent);

    }

    public static void spawnLightning() {

        ServerPlayer player = player();
        LightningBolt ent = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level);
        ent.setPos(player.getX(), player.getY(), player.getZ());
        player.level.addFreshEntity(ent);

    }

    public static void spawnArmorStand() {

        ServerPlayer player = player();

        double d0 = player.getX();
        double d1 = player.getY();
        double d2 = player.getZ();

        // Face where player is looking (Modified from vanilla ArmorStandItem)
        ArmorStand armorstandentity = new ArmorStand(player.level, d0 + 0.5, d1 + 0.5, d2 + 0.5);
        float f = (float) Mth.floor((Mth.wrapDegrees(player.getYRot()) + 22.5F) / 45.0F) * 45.0F;
        armorstandentity.moveTo(d0 + 0.5, d1 + 0.5, d2 + 0.5, f, 0.0F);

        // Give the stand a custom player head
        ItemStack item = new ItemStack(Items.PLAYER_HEAD, 1);

        // Add NBT if we can
        if (PlayerHelper.getUsername() != null) {

            CompoundTag nbt = item.getOrCreateTag();
            nbt.putString("SkullOwner", PlayerHelper.getUsername());
            item.save(nbt);

        }

        armorstandentity.setItemSlot(EquipmentSlot.HEAD, item);

        // Access transformer needed for this
        armorstandentity.setShowArms(true);

        spawnMobBehind(armorstandentity);
        playSound(SoundEvents.AMBIENT_CAVE, SoundSource.AMBIENT, 1, 1);

    }

    public static void spawnMobBehind(Entity ent) {

        ServerPlayer player = player();

        Vec3 lookVector = player.getLookAngle();

        double dx = player.getX() - (lookVector.x * 3);
        double dz = player.getZ() - (lookVector.z * 3);

        ent.setPos(dx, player.getY(), dz);

        player.level.addFreshEntity(ent);

    }

    public static void breakBlock() {

        ServerPlayer player = player();

        int range = 64;

        Vec3 lookVector = player.getLookAngle();
        Vec3 posVector = new Vec3(player.getX(), player.getEyeY(), player.getZ());

        ClipContext context = new ClipContext(posVector, lookVector.scale(range).add(posVector), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        BlockHitResult rayTrace = player.level.clip(context);

        if (rayTrace.getType() != BlockHitResult.Type.BLOCK) {
            return;
        }

        BlockPos bpos = rayTrace.getBlockPos();

        player.level.destroyBlock(bpos, false);

    }

    public static void infestBlock() {

        ServerPlayer player = player();

        int range = 64;

        Vec3 lookVector = player.getLookAngle();
        Vec3 posVector = new Vec3(player.getX(), player.getEyeY(), player.getZ());

        ClipContext context = new ClipContext(posVector, lookVector.scale(range).add(posVector), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        BlockHitResult rayTrace = player.level.clip(context);

        if (rayTrace.getType() != BlockHitResult.Type.BLOCK) {
            return;
        }

        BlockPos bpos = rayTrace.getBlockPos();
        BlockState thisBlock = player.level.getBlockState(bpos);

        setBlock(bpos, InfestedBlock.infestedStateByHost(thisBlock));

    }

    public static void surroundPlayer(BlockState block) {

        ServerPlayer player = player();
        BlockPos playerPos = player.blockPosition();

        BlockPos[] positions = {playerPos.north(), playerPos.east(), playerPos.south(), playerPos.west()};

        for (BlockPos bpos : positions) {
            setBlock(bpos, block);
            setBlock(bpos.above(), block);
        }

        setBlock(playerPos.above().above(), block);

        if (player.level.getBlockState(playerPos.below()) == Blocks.AIR.defaultBlockState()) {
            setBlock(playerPos.below(), block);
        }

    }

    public static void dropItem() { // Thanks Amoo!

        ServerPlayer player = player();
        ItemStack currentItem = player.getInventory().getSelected();

        if (currentItem != ItemStack.EMPTY) {

            player.drop(currentItem, false, true);
            player.getInventory().removeItem(currentItem);

        }

    }

    public static void changeDurability(boolean repairItem) {

        ServerPlayer player = player();
        ItemStack currentItem = player.getInventory().getSelected();

        int damageAmount = rand.nextInt(currentItem.getMaxDamage() / 3);

        if (currentItem != ItemStack.EMPTY) {

            if (repairItem) {
                currentItem.hurt(damageAmount * -1, rand, player);
            } else {
                currentItem.hurt(damageAmount, rand, player);
            }


        }

    }

    public static void removeRandomItemStack() {

        ServerPlayer player = player();

        // Prevent loop
        if (player.getInventory().isEmpty()) {
            return;
        }

        // Delete a random item
        int r = rand.nextInt(player.getInventory().getContainerSize());

        ItemStack randomItem = player.getInventory().getItem(r);

        if (randomItem != ItemStack.EMPTY) {

            player.getInventory().removeItem(randomItem);

        } else {

            removeRandomItemStack();

        }

    }

    public static ItemStack getRandomItemStack(boolean randomCount) {

        int length = ForgeRegistries.ITEMS.getKeys().toArray().length;
        int r = rand.nextInt(length);

        Item select = Item.byId(r);

        if (select != null) {

            ItemStack stack = new ItemStack(select);

            if (randomCount) {
                stack.setCount(rand.nextInt(stack.getMaxStackSize()));
            } else {
                stack.setCount(1);
            }

            return stack;

        }
        return null;

    }

    public static void giveAndRemoveRandom() {

        ItemStack stack = getRandomItemStack(true);

        // Remove the random item here to prevent an item being removed and no item being given to the player
        removeRandomItemStack();

        player().addItem(stack);

    }

    public static void itemRoulette(String sender) {

        ServerPlayer player = player();

        if (!player.getInventory().isEmpty()) {

            giveAndRemoveRandom();

            // Show chat message
            player.displayClientMessage(new TextComponent(ChatFormatting.RED + sender + " giveth, and " + sender + " taketh away."), true);

        }

    }

    // Thank you to ChiKitsune for writing this code!
    // https://github.com/ChiKitsune/SwapThings/blob/master/src/main/java/chikitsune/swap_things/commands/ShuffleInventory.java
    public static void shuffleInventory(String sender) {

        ServerPlayer player = player();

        ItemStack tempItem;
        int tempRandNum;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {

            tempRandNum = i;

            while (tempRandNum == i) {
                tempRandNum = rand.nextInt(player.getInventory().getContainerSize());
            }

            tempItem = player.getInventory().getItem(i).copy();
            player.getInventory().setItem(i, player.getInventory().getItem(tempRandNum).copy());
            player.getInventory().setItem(tempRandNum, tempItem);
        }

        // Show chat message
        player.displayClientMessage(new TextComponent(ChatFormatting.RED + sender + " rearranged your inventory."), true);

    }

    public static void renameItem(String name) {

        ServerPlayer player = player();

        if (!player.getInventory().isEmpty()) {

            // Limit custom rename to 32 characters (FIXED: use StringUtils)
            String newname = StringUtils.left(name, 32);

            ItemStack currentitem = player.getInventory().getSelected();

            if (currentitem == ItemStack.EMPTY || currentitem.getDisplayName().getContents().equals(newname)) {

                int tries = 0;

                // Rename a random item in the player's inventory when the player isn't holding anything
                while (currentitem == ItemStack.EMPTY || currentitem.getDisplayName().getContents().equals(newname) && !player.getInventory().isEmpty()) {

                    if (tries < player.getInventory().getContainerSize()) {

                        int r = rand.nextInt(player.getInventory().getContainerSize());
                        currentitem = player.getInventory().getItem(r);
                        tries++;

                    } else {
                        return;
                    }

                }

            }

            currentitem.setHoverName(new TextComponent(newname));

        }

    }

    public static void enchantItem() {

        ServerPlayer player = player();

        if (!player.getInventory().isEmpty()) {

            // Get random enchantment from list
            int length = ForgeRegistries.ENCHANTMENTS.getKeys().toArray().length;
            int r = rand.nextInt(1, length + 1);
            Enchantment enchantment = Enchantment.byId(r);

            // Set enchantment level (random level from 1 to enchantment max level)
            int level = 1;
            if (enchantment != null) {
                level = rand.nextInt(1, enchantment.getMaxLevel() + 1);
            } else {
                return;
            }

            ItemStack currentitem = player.getInventory().getSelected();

            if (currentitem == ItemStack.EMPTY) {

                // Enchant a random item in the player's inventory when the player isn't holding anything
                while (currentitem == ItemStack.EMPTY && !player.getInventory().isEmpty()) {

                    r = rand.nextInt(player.getInventory().getContainerSize());
                    currentitem = player.getInventory().getItem(r);

                }

            }

            currentitem.enchant(enchantment, level);

        }

    }

    public static void curseArmour() {

        ServerPlayer player = player();

        if (!player.getInventory().isEmpty()) {

            for (int i = 0; i < player.getInventory().armor.size(); i++) {

                ItemStack armourItem = player.getInventory().getArmor(i);

                if (armourItem != ItemStack.EMPTY) {

                    armourItem.enchant(Enchantments.BINDING_CURSE, 1);
                    player.getInventory().armor.set(i, armourItem);

                }

            }

        }

    }

    public static void pumpkin() {

        ServerPlayer player = player();

        if (!player.getInventory().isEmpty()) {

            // Armour index 3 is helmet
            ItemStack helmet = player.getInventory().armor.get(3);

            if (helmet.getItem() == Items.CARVED_PUMPKIN) {
                return;
            }

            if (helmet != ItemStack.EMPTY) {
                player.drop(helmet, false, true);
                player.getInventory().removeItem(helmet);
            }

            player.getInventory().armor.set(3, new ItemStack(Items.CARVED_PUMPKIN));

        }


    }

    public static void toggleCrouch() {
        ServerPlayer player = player();
        player.setShiftKeyDown(!player.isCrouching());
    }

    public static void toggleSprint() {
        ServerPlayer player = player();
        player.setSprinting(!player.isSprinting());
    }

    public static void dismount() {

        ServerPlayer player = player();

        if (player.isPassenger()) {
            player.stopRiding();
        }
        if (player.isSleeping()) {
            player.stopSleeping();
        }

    }

    public static void chorusTeleport() {

        ServerPlayer player = player();
        Level world = player.level;

        // Code taken from ChorusFruitItem in vanilla
        if (!world.isClientSide()) {
            double d0 = player.getX();
            double d1 = player.getY();
            double d2 = player.getZ();

            for (int i = 0; i < 16; ++i) {
                double d3 = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
                double d4 = Mth.clamp(player.getY() + (double) (player.getRandom().nextInt(16) - 8), 0.0D, world.getHeight() - 1);
                double d5 = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
                if (player.isPassenger()) {
                    player.stopRiding();
                }

                if (player.randomTeleport(d3, d4, d5, true)) {
                    SoundEvent soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    world.playSound(null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.playSound(soundevent, 1.0F, 1.0F);
                    break;
                }
            }

        }
    }

    public static void showMessagebox(String message) {

        // Then trim the string to the proper length (324 chars max)
        message = message.substring(0, Math.min(message.length(), 324));

        PacketHandler.INSTANCE.sendTo(new MessageboxPacket(message), player().connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);

    }

    public static void startWritingBook() {

        ChatPicker.tempChatLog.clear();
        ChatPicker.logMessages = true;
        player().displayClientMessage(new TextComponent(ChatFormatting.LIGHT_PURPLE + "Chat has started writing a book."), true);

    }

    public static void createBook(List<String> text) {

        try {
            
            ServerPlayer player = player();

            ItemStack itemStack = new ItemStack(Items.WRITTEN_BOOK, 1);
            CompoundTag nbt = itemStack.getOrCreateTag();

            ListTag pages = new ListTag();

            nbt.putString("author", PlayerHelper.getUsername());
            nbt.putString("title", "Chat Log " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            for (String str : text) {
                pages.add(StringTag.valueOf(str));
            }

            nbt.put("pages", pages);
            itemStack.save(nbt);

            player.addItem(itemStack);
            player.displayClientMessage(new TextComponent(ChatFormatting.LIGHT_PURPLE + "Chat has written you a book."), true);

        } catch (Exception e) {
            Main.logger.error(e);
        }

    }

    public static void placeSign(String message) {

        ServerPlayer player = player();

        // Split every 15 characters
        int maxlength = 15;
        String[] splitMessage = message.split("(?<=\\G.{" + maxlength + "})");

        BlockPos bpos = player.blockPosition();
        BlockPos bposBelow = new BlockPos(bpos.getX(), bpos.getY() - 1, bpos.getZ());

        // Rotate the sign to face the player
        int playerFace = Mth.floor((double) ((player.getYRot() + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;

        // Set block state to air before placing sign
        setBlock(bpos, Blocks.AIR.defaultBlockState());

        // Place the sign with rotation
        player.level.setBlock(bpos, Blocks.OAK_SIGN.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, playerFace), 11);

        BlockEntity blockEntity = player.level.getBlockEntity(bpos);

        // Thanks for the new code Commoble!
        if (blockEntity instanceof SignBlockEntity sign) {

            int lines = splitMessage.length;

            for (int i = 0; i < lines; i++) {
                sign.setMessage(i, new TextComponent(splitMessage[i]));
            }

        }


        // Add a light source below the sign for reading at night (thanks Gaiet)
        setBlock(bposBelow, Blocks.GLOWSTONE.defaultBlockState());

    }


    public static void placeChest() {

        ServerPlayer player = player();

        BlockPos bpos = player.blockPosition();
        Block bposBlock = player.level.getBlockState(bpos).getBlock();

        // Make sure we don't replace any chests
        if (bposBlock != Blocks.CHEST && bposBlock != Blocks.TRAPPED_CHEST) {
            setBlock(bpos, Blocks.CHEST.defaultBlockState());
        }

        BlockEntity blockEntity = player.level.getBlockEntity(bpos);

        if (blockEntity instanceof ChestBlockEntity) {

            ((ChestBlockEntity) blockEntity).setLootTable(lootArray[rand.nextInt(lootArray.length)], rand.nextLong());
            ((ChestBlockEntity) blockEntity).unpackLootTable(player);

        }

    }


    public static void chooseRandomMessage() {

        if (!messagesList.isEmpty()) {

            int r = rand.nextInt(messagesList.size());

            // Get random message
            String message = messagesList.get(r);
            messagesList.remove(r);

            // Get random colour
            ChatFormatting format = ChatFormatting.values()[1 + rand.nextInt(ChatFormatting.values().length - 1)];

            broadcastMessage(new TextComponent(format + message));

        }

    }

    /**
     * This method sends a message to everyone on a server.
     */
    public static void broadcastMessage(MutableComponent message) {

        ServerPlayer player = player();

        try {
            player.sendMessage(message, player.getUUID());
        } catch (Exception e) {
            Main.logger.error(e);
        }

    }

    @SubscribeEvent
    public static void explodeOnBreak(BreakEvent event) {

        Block thisBlock = event.getState().getBlock();

        if (Tags.Blocks.ORES.contains(thisBlock) && oresExplode && !event.getWorld().isClientSide()) {

            double dx = event.getPos().getX();
            double dy = event.getPos().getY();
            double dz = event.getPos().getZ();

            player().level.explode(null, dx, dy, dz, 4.0F, Explosion.BlockInteraction.BREAK);

            oresExplode = false;

        }

    }

    @SubscribeEvent
    public static void bedrockOnBreak(BreakEvent event) {

        BlockPos bpos = event.getPos();

        if (placeBedrockOnBreak && !event.getWorld().isClientSide()) {

            event.setCanceled(true);
            event.getWorld().setBlock(bpos, Blocks.BEDROCK.defaultBlockState(), 2);
            placeBedrockOnBreak = false;

        }

    }

    @SubscribeEvent
    public static void villagersDie(PlayerInteractEvent.EntityInteract event) {

        if (event.getTarget() instanceof Villager && burnVillagersOnInteract && !event.getWorld().isClientSide()) {

            ((Villager) event.getTarget()).addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            event.getTarget().setSecondsOnFire(10);
            burnVillagersOnInteract = false;

        }
    }

    @SubscribeEvent
    public static void workbenchesBreak(PlayerInteractEvent.RightClickBlock event) {

        Level world = event.getWorld();
        Block block = world.getBlockState(event.getPos()).getBlock();

        if (destroyWorkbenchesOnInteract && !world.isClientSide()) {

            if (block == Blocks.CRAFTING_TABLE || block == Blocks.FURNACE) {

                event.setCanceled(true);
                world.destroyBlock(event.getPos(), false);
                destroyWorkbenchesOnInteract = false;

            }

        }

    }

}