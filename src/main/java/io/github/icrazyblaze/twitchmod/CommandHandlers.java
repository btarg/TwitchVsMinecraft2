package io.github.icrazyblaze.twitchmod;

import io.github.icrazyblaze.twitchmod.chat.ChatCommands;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import io.github.icrazyblaze.twitchmod.gui.MessageboxScreen;
import io.github.icrazyblaze.twitchmod.network.MessageboxPacket;
import io.github.icrazyblaze.twitchmod.network.PacketHandler;
import io.github.icrazyblaze.twitchmod.util.PlayerHelper;
import io.github.icrazyblaze.twitchmod.util.TimerSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.carrierbees.entities.CarrierBeeEntity;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static ResourceLocation[] getLootTables() throws NoSuchFieldException, IllegalAccessException {

        // Reflect LootTables class to get the private set that contains all the vanilla loot tables
        Class<LootTables> obj = LootTables.class;
        Field lootField = obj.getDeclaredField("LOOT_TABLES");
        lootField.setAccessible(true);

        Set<ResourceLocation> tables = (Set<ResourceLocation>) lootField.get("LOOT_TABLES");

        return tables.toArray(new ResourceLocation[0]);

    }

    public static void rollTheDice(String sender) {

        List<String> commands = ChatCommands.getRegisteredCommands();
        String randomCommand = commands.get(rand.nextInt(commands.toArray().length));
        broadcastMessage(new StringTextComponent(sender + " rolled the dice!"));
        ChatPicker.checkChat(randomCommand, sender);

    }


    public static void addPotionEffects(EffectInstance[] effectInstances) {

        ServerPlayerEntity player = player();

        for (EffectInstance effect : effectInstances) {
            player.addPotionEffect(effect);
        }

    }

    public static void setBlock(BlockPos bpos, BlockState state) {
        player().world.setBlockState(bpos, state);
    }

    public static void setBlocks(BlockPos[] positions, BlockState state) {
        for (BlockPos bpos : positions) {
            setBlock(bpos, state);
        }
    }

    public static void setOnFire() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        BlockState bposState = player.world.getBlockState(bpos);

        if (bposState == Blocks.AIR.getDefaultState()) {
            setBlock(bpos, Blocks.FIRE.getDefaultState());
        }

        player.setFire(10);

    }

    public static void setRainAndThunder() {

        ServerPlayerEntity player = player();
        player.world.getWorldInfo().setRaining(true);

        if (!player.world.isRemote) {
            player.getServerWorld().func_241113_a_(0, 6000, true, true);
        }
    }

    public static void setDifficulty(Difficulty difficulty) {

        Objects.requireNonNull(player().getServer()).setDifficultyForAllWorlds(difficulty, false);

    }

    public static void setTime(long time) {

        Iterable<ServerWorld> worlds = player().server.getWorlds();

        for (ServerWorld world : worlds) {
            world.setDayTime(time);
        }

    }

    public static void drainHealth() {

        ServerPlayerEntity player = player();

        // Half the player's health
        float halfhealth = player.getHealth() / 2;

        if (halfhealth == 0) {
            player.onKillCommand();
        } else {
            player.setHealth(halfhealth);
        }

    }


    public static void setSpawn() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
        // SetSpawn
        player.func_242111_a(player.world.getDimensionKey(), bpos, 0.0F, false, true);

    }

    public static void deathTimer() {

        if (ChatPicker.instantCommands) {
            return;
        }

        TimerSystem.deathTimerSeconds = 60;
        TimerSystem.deathTimerTicks = 0;
        TimerSystem.deathTimerEnabled = true;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.DARK_RED + "Chat has given you " + TimerSystem.deathTimerSeconds + " seconds to live."), true);

    }

    public static void frenzyTimer() {

        if (ChatPicker.instantCommands || !enableFrenzyMode) {
            return;
        }

        TimerSystem.frenzyTimerSeconds = 10;
        TimerSystem.frenzyTimerTicks = 0;
        ChatPicker.instantCommands = true;

        previousDeathTimerState = TimerSystem.deathTimerEnabled;
        TimerSystem.deathTimerEnabled = false;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.GOLD + "FRENZY MODE! All commands are executed for the next " + TimerSystem.frenzyTimerSeconds + " seconds."), true);

    }

    public static void graceTimer() {

        if (ChatPicker.instantCommands) {
            return;
        }

        ChatPicker.enabled = false;
        TimerSystem.peaceTimerSeconds = 30;
        TimerSystem.peaceTimerTicks = 0;
        TimerSystem.peaceTimerEnabled = true;

        previousDeathTimerState = TimerSystem.deathTimerEnabled;
        TimerSystem.deathTimerEnabled = false;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + "Commands are turned off for " + TimerSystem.peaceTimerSeconds + " seconds."), true);

    }

    public static void disableGraceTimer() {

        ChatPicker.enabled = true;
        TimerSystem.peaceTimerEnabled = false;
        TimerSystem.deathTimerEnabled = previousDeathTimerState;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + "Commands are now enabled!"), true);

    }

    public static void disableFrenzyTimer() {

        ChatPicker.instantCommands = false;
        TimerSystem.deathTimerEnabled = previousDeathTimerState;
        player().sendStatusMessage(new StringTextComponent(TextFormatting.GOLD + "Frenzy mode is now disabled."), true);

    }

    public static void floorIsLava() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY() - 1, player.getPosZ());
        setBlock(bpos, Blocks.LAVA.getDefaultState());

    }

    public static void placeWater() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        setBlock(bpos, Blocks.WATER.getDefaultState());

    }

    public static void placeSponge() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());

        setBlock(bpos, Blocks.SPONGE.getDefaultState());

    }

    public static void spawnAnvil() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY() + 16, player.getPosZ());

        setBlock(bpos, Blocks.ANVIL.getDefaultState());

    }

    public static void spawnCobweb() {

        ServerPlayerEntity player = player();

        setBlock(new BlockPos(player.getPosX(), player.getPosY() + 1, player.getPosZ()), Blocks.COBWEB.getDefaultState());
        setBlock(new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ()), Blocks.COBWEB.getDefaultState());

    }

    public static void spawnMob(Entity ent) {

        ServerPlayerEntity player = player();

        Vector3d lookVector = player.getLookVec();

        double dx = player.getPosX() + (lookVector.x * 4);
        double dz = player.getPosZ() + (lookVector.z * 4);

        ent.setPosition(dx, player.getPosY(), dz);

        player.world.addEntity(ent);

    }

    public static void spawnCarrierBee(String name, EntityType type) {

        ServerPlayerEntity player = player();

        CarrierBeeEntity bee = new CarrierBeeEntity(type, player.world);

        // Give it an item and name
        bee.setHeldItem(bee.getActiveHand(), Objects.requireNonNull(getRandomItemStack()));
        bee.setCustomName(new StringTextComponent(name));
        bee.setDropChance(EquipmentSlotType.MAINHAND, 1.0F);

        player.sendStatusMessage(new StringTextComponent(TextFormatting.YELLOW + name + " sent some support!"), true);
        spawnMob(bee);

    }


    public static void pigmanScare() {
        playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, SoundCategory.HOSTILE, 2.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
    }

    public static void playSound(SoundEvent sound, SoundCategory category, float volume, float pitch) {

        ServerPlayerEntity player = player();
        player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), sound, category, volume, pitch);

    }

    public static void spawnFireball() {

        ServerPlayerEntity player = player();

        Vector3d lookVector = player.getLookVec();

        double dx = player.getPosX() + (lookVector.x * 2);
        double dz = player.getPosZ() + (lookVector.z * 2);

        Entity ent = new FireballEntity(EntityType.FIREBALL, player.world);
        ent.setPosition(dx, player.getPosYEye(), dz);
        ent.setVelocity(lookVector.x * 3, lookVector.y, lookVector.z * 3);

        player.world.addEntity(ent);

    }

    public static void spawnLightning() {

        ServerPlayerEntity player = player();
        LightningBoltEntity ent = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, player.world);
        ent.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
        player.world.addEntity(ent);

    }

    public static void spawnArmorStand() {

        ServerPlayerEntity player = player();

        double d0 = player.getPosX();
        double d1 = player.getPosY();
        double d2 = player.getPosZ();

        // Face where player is looking (Modified from vanilla ArmorStandItem)
        ArmorStandEntity armorstandentity = new ArmorStandEntity(player.world, d0 + 0.5, d1 + 0.5, d2 + 0.5);
        float f = (float) MathHelper.floor((MathHelper.wrapDegrees(player.rotationYaw) + 22.5F) / 45.0F) * 45.0F;
        armorstandentity.setLocationAndAngles(d0 + 0.5, d1 + 0.5, d2 + 0.5, f, 0.0F);

        // Give the stand a custom player head
        ItemStack item = new ItemStack(Items.PLAYER_HEAD, 1);

        // Add NBT if we can
        if (BotConfig.getUsername() != null) {

            CompoundNBT nbt = item.getOrCreateTag();
            nbt.putString("SkullOwner", BotConfig.getUsername());
            item.write(nbt);

        }

        armorstandentity.replaceItemInInventory(103, item);

        // Access transformer needed for this
        armorstandentity.setShowArms(true);

        spawnMobBehind(armorstandentity);
        playSound(SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 1, 1);

    }

    public static void spawnMobBehind(Entity ent) {

        ServerPlayerEntity player = player();

        Vector3d lookVector = player.getLookVec();

        double dx = player.getPosX() - (lookVector.x * 3);
        double dz = player.getPosZ() - (lookVector.z * 3);

        ent.setPosition(dx, player.getPosY(), dz);

        player.world.addEntity(ent);

    }

    public static void breakBlock() {

        ServerPlayerEntity player = player();

        int range = 50;

        Vector3d lookVector = player.getLookVec();
        Vector3d posVector = new Vector3d(player.getPosX(), player.getPosYEye(), player.getPosZ());

        RayTraceContext context = new RayTraceContext(posVector, lookVector.scale(range).add(posVector), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult rayTrace = player.world.rayTraceBlocks(context);

        if (rayTrace == null || rayTrace.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        BlockPos bpos = new BlockPos(rayTrace.getHitVec());

        player.world.destroyBlock(bpos, false);

    }

    public static void infestBlock() {

        ServerPlayerEntity player = player();

        int range = 50;

        Vector3d lookVector = player.getLookVec();
        Vector3d posVector = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        RayTraceContext context = new RayTraceContext(posVector, lookVector.scale(range).add(posVector), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult rayTrace = player.world.rayTraceBlocks(context);

        if (rayTrace == null || rayTrace.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        BlockPos bpos = new BlockPos(rayTrace.getHitVec());

        Block thisBlock = player.world.getBlockState(bpos).getBlock();

        if (thisBlock == Blocks.COBBLESTONE) {
            setBlock(bpos, Blocks.INFESTED_COBBLESTONE.getDefaultState());
        } else if (thisBlock == Blocks.STONE) {
            setBlock(bpos, Blocks.INFESTED_STONE.getDefaultState());
        } else if (thisBlock == Blocks.STONE_BRICKS) {
            setBlock(bpos, Blocks.INFESTED_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.MOSSY_STONE_BRICKS) {
            setBlock(bpos, Blocks.INFESTED_MOSSY_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.CRACKED_STONE_BRICKS) {
            setBlock(bpos, Blocks.INFESTED_CRACKED_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.CHISELED_STONE_BRICKS) {
            setBlock(bpos, Blocks.INFESTED_CHISELED_STONE_BRICKS.getDefaultState());
        }

    }

    public static void placeGlass() {

        ServerPlayerEntity player = player();

        double dx = player.getPosX();
        double dy = player.getPosY();
        double dz = player.getPosZ();

        // TODO: This code is shit, replace it
        BlockPos[] positions = {new BlockPos(dx, dy + 2, dz), new BlockPos(dx, dy, dz - 1), new BlockPos(dx, dy + 1, dz - 1), new BlockPos(dx, dy, dz + 1), new BlockPos(dx, dy + 1, dz + 1), new BlockPos(dx - 1, dy, dz), new BlockPos(dx - 1, dy + 1, dz), new BlockPos(dx + 1, dy, dz), new BlockPos(dx + 1, dy + 1, dz), new BlockPos(dx, dy - 1, dz)};

        setBlocks(positions, Blocks.GLASS.getDefaultState());

    }

    public static void dropItem() { // Thanks Amoo!

        ServerPlayerEntity player = player();
        ItemStack currentItem = player.inventory.getCurrentItem();

        if (currentItem != ItemStack.EMPTY) {

            player.dropItem(currentItem, false, true);
            player.inventory.deleteStack(currentItem);

        }

    }

    public static void changeDurability(boolean repairItem) {

        ServerPlayerEntity player = player();
        ItemStack currentItem = player.inventory.getCurrentItem();

        int damageAmount = rand.nextInt(currentItem.getMaxDamage() / 3);

        if (currentItem != ItemStack.EMPTY) {

            if (repairItem) {
                currentItem.attemptDamageItem(damageAmount * -1, rand, player);
            } else {
                currentItem.attemptDamageItem(damageAmount, rand, player);
            }


        }

    }

    public static void removeRandomItemStack() {

        ServerPlayerEntity player = player();

        // Prevent loop
        if (player.inventory.isEmpty()) {
            return;
        }

        // Delete a random item
        int r = rand.nextInt(player.inventory.getSizeInventory());

        ItemStack randomItem = player.inventory.getStackInSlot(r);

        if (randomItem != ItemStack.EMPTY) {

            player.inventory.deleteStack(randomItem);

        } else {

            removeRandomItemStack();

        }

    }

    public static ItemStack getRandomItemStack() {

        int length = ForgeRegistries.ITEMS.getKeys().toArray().length;
        int r = rand.nextInt(length);

        Item select = Item.getItemById(r);

        if (select != null) {

            ItemStack stack = new ItemStack(select);
            stack.setCount(rand.nextInt(stack.getMaxStackSize()));
            return stack;

        }
        return null;

    }

    public static void giveAndRemoveRandom() {

        ItemStack stack = getRandomItemStack();

        // Remove the random item here to prevent an item being removed and no item being given to the player
        removeRandomItemStack();

        player().addItemStackToInventory(stack);

    }

    public static void itemRoulette(String sender) {

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            giveAndRemoveRandom();

            // Show chat message
            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + sender + " giveth, and " + sender + " taketh away."), true);

        }

    }

    // Thank you to ChiKitsune for writing this code!
    // https://github.com/ChiKitsune/SwapThings/blob/master/src/main/java/chikitsune/swap_things/commands/ShuffleInventory.java
    public static void shuffleInventory(String sender) {

        ServerPlayerEntity player = player();

        ItemStack tempItem;
        int tempRandNum;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {

            tempRandNum = i;

            while (tempRandNum == i) {
                tempRandNum = rand.nextInt(player.inventory.getSizeInventory());
            }

            tempItem = player.inventory.getStackInSlot(i).copy();
            player.inventory.setInventorySlotContents(i, player.inventory.getStackInSlot(tempRandNum).copy());
            player.inventory.setInventorySlotContents(tempRandNum, tempItem);
        }

        // Show chat message
        player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + sender + " rearranged your inventory."), true);

    }

    public static void renameItem(String name) {

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            // Limit custom rename to 32 characters (FIXED: use StringUtils)
            String newname = StringUtils.left(name, 32);

            ItemStack currentitem = player.inventory.getCurrentItem();

            if (currentitem == ItemStack.EMPTY || currentitem.getDisplayName().getUnformattedComponentText().equals(newname)) {

                int tries = 0;

                // Rename a random item in the player's inventory when the player isn't holding anything
                while (currentitem == ItemStack.EMPTY || currentitem.getDisplayName().getUnformattedComponentText().equals(newname) && !player.inventory.isEmpty()) {

                    if (tries < player.inventory.getSizeInventory()) {

                        int r = rand.nextInt(player.inventory.getSizeInventory());
                        currentitem = player.inventory.getStackInSlot(r);
                        tries++;

                    } else {
                        return;
                    }

                }

            }

            currentitem.setDisplayName(new StringTextComponent(newname));

        }

    }

    public static void enchantItem() {

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            // Get random enchantment from list
            int length = ForgeRegistries.ENCHANTMENTS.getKeys().toArray().length;
            int r = rand.nextInt(1, length + 1);
            Enchantment enchantment = Enchantment.getEnchantmentByID(r);

            // Set enchantment level (random level from 1 to enchantment max level)
            int level = 1;
            if (enchantment != null) {
                level = rand.nextInt(1, enchantment.getMaxLevel() + 1);
            } else {
                return;
            }

            ItemStack currentitem = player.inventory.getCurrentItem();

            if (currentitem == ItemStack.EMPTY) {

                // Enchant a random item in the player's inventory when the player isn't holding anything
                while (currentitem == ItemStack.EMPTY && !player.inventory.isEmpty()) {

                    r = rand.nextInt(player.inventory.getSizeInventory());
                    currentitem = player.inventory.getStackInSlot(r);

                }

            }

            currentitem.addEnchantment(enchantment, level);

        }

    }

    public static void curseArmour() {

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            for (int i = 0; i < player.inventory.armorInventory.size(); i++) {

                ItemStack armourItem = player.inventory.armorItemInSlot(i);

                if (armourItem != ItemStack.EMPTY) {

                    armourItem.addEnchantment(Enchantments.BINDING_CURSE, 1);
                    player.inventory.armorInventory.set(i, armourItem);

                }

            }

        }

    }

    public static void pumpkin() {

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            // Armour index 3 is helmet
            ItemStack helmet = player.inventory.armorInventory.get(3);

            if (helmet.getItem() == Items.CARVED_PUMPKIN) {
                return;
            }

            if (helmet != ItemStack.EMPTY) {
                player.dropItem(helmet, false, true);
                player.inventory.deleteStack(helmet);
            }

            player.inventory.armorInventory.set(3, new ItemStack(Items.CARVED_PUMPKIN));

        }


    }

    public static void toggleCrouch() {
        ServerPlayerEntity player = player();
        player.setSneaking(!player.isCrouching());
    }

    public static void toggleSprint() {
        ServerPlayerEntity player = player();
        player.setSprinting(!player.isSprinting());
    }

    public static void dismount() {

        ServerPlayerEntity player = player();

        if (player.isPassenger()) {
            player.stopRiding();
        }

    }

    public static void chorusTeleport() {

        ServerPlayerEntity player = player();
        World world = player.world;

        // Code taken from ChorusFruitItem in vanilla
        if (!world.isRemote) {
            double d0 = player.getPosX();
            double d1 = player.getPosY();
            double d2 = player.getPosZ();

            for (int i = 0; i < 16; ++i) {
                double d3 = player.getPosX() + (player.getRNG().nextDouble() - 0.5D) * 16.0D;
                double d4 = MathHelper.clamp(player.getPosY() + (double) (player.getRNG().nextInt(16) - 8), 0.0D, world.func_234938_ad_() - 1);
                double d5 = player.getPosZ() + (player.getRNG().nextDouble() - 0.5D) * 16.0D;
                if (player.isPassenger()) {
                    player.stopRiding();
                }

                if (player.attemptTeleport(d3, d4, d5, true)) {
                    SoundEvent soundevent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    world.playSound(null, d0, d1, d2, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    player.playSound(soundevent, 1.0F, 1.0F);
                    break;
                }
            }

        }
    }

    public static void showMessagebox(String message) {

        // Then trim the string to the proper length (324 chars max)
        message = message.substring(0, Math.min(message.length(), 324));

        PacketHandler.INSTANCE.sendTo(new MessageboxPacket(message), player().connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);

    }

    /*
    This code is run on the client when the GuiMessage packet is received.
    */
    @OnlyIn(Dist.CLIENT)
    public static void showMessageBoxClient(String message) {

        Minecraft.getInstance().displayGuiScreen(new MessageboxScreen(message));

    }

    public static void startWritingBook() {

        ChatPicker.tempChatLog.clear();
        ChatPicker.logMessages = true;
        player().sendStatusMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Chat has started writing a book."), true);

    }

    public static void createBook(List<String> text) {

        try {

            Main.logger.info("Creating book");
            ServerPlayerEntity player = player();

            ItemStack itemStack = new ItemStack(Items.WRITTEN_BOOK, 1);
            CompoundNBT nbt = itemStack.getOrCreateTag();

            ListNBT pages = new ListNBT();

            nbt.putString("author", BotConfig.getUsername());
            nbt.putString("title", "Chat Log " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            for (String str : text) {
                pages.add(StringNBT.valueOf(str));
            }

            nbt.put("pages", pages);
            itemStack.write(nbt);

            player.addItemStackToInventory(itemStack);
            player.sendStatusMessage(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Chat has written you a book."), true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void placeSign(String message) {

        ServerPlayerEntity player = player();

        // Split every 15 characters
        int maxlength = 15;
        String[] splitMessage = message.split("(?<=\\G.{" + maxlength + "})");

        BlockPos bpos = player.getPosition();
        BlockPos bposBelow = new BlockPos(bpos.getX(), bpos.getY() - 1, bpos.getZ());

        // Rotate the sign to face the player
        int playerFace = MathHelper.floor((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;

        // Set block state to air before placing sign
        setBlock(bpos, Blocks.AIR.getDefaultState());

        // Place the sign with rotation
        player.world.setBlockState(bpos, Blocks.OAK_SIGN.getDefaultState().with(BlockStateProperties.ROTATION_0_15, playerFace), 11);

        TileEntity tileEntity = player.world.getTileEntity(bpos);

        // Thanks for the new code Commoble!
        if (tileEntity instanceof SignTileEntity) {

            SignTileEntity sign = (SignTileEntity) tileEntity;

            int lines = splitMessage.length;

            for (int i = 0; i < lines; i++) {
                sign.setText(i, new StringTextComponent(splitMessage[i]));
            }

        }

        // Add a light source below the sign for reading at night (thanks Gaiet)
        setBlock(bposBelow, Blocks.GLOWSTONE.getDefaultState());

    }


    public static void placeChest() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        Block bposBlock = player.world.getBlockState(bpos).getBlock();

        // Make sure we don't replace any chests
        if (bposBlock != Blocks.CHEST && bposBlock != Blocks.TRAPPED_CHEST) {

            setBlock(bpos, Blocks.CHEST.getDefaultState());

            TileEntity tileEntity = player.world.getTileEntity(bpos);

            if (tileEntity instanceof ChestTileEntity) {

                ((ChestTileEntity) tileEntity).setLootTable(lootArray[rand.nextInt(lootArray.length)], rand.nextLong());
                ((ChestTileEntity) tileEntity).fillWithLoot(player);

            }

        }

    }


    public static void chooseRandomMessage() {

        if (!messagesList.isEmpty()) {

            int r = rand.nextInt(messagesList.size());

            // Get random message
            String message = messagesList.get(r);
            messagesList.remove(r);

            // Get random colour
            TextFormatting format = TextFormatting.values()[1 + rand.nextInt(TextFormatting.values().length - 1)];

            broadcastMessage(new StringTextComponent(format + message));

        }

    }

    /**
     * This method sends a message to everyone on a server.
     */
    public static void broadcastMessage(ITextComponent message) {

        ServerPlayerEntity player = player();

        try {
            player.sendMessage(message, player.getUniqueID());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SubscribeEvent
    public static void explodeOnBreak(BreakEvent event) {

        Block thisBlock = event.getState().getBlock();

        if (Tags.Blocks.ORES.contains(thisBlock) && oresExplode && !event.getWorld().isRemote()) {

            double dx = event.getPos().getX();
            double dy = event.getPos().getY();
            double dz = event.getPos().getZ();

            player().world.createExplosion(null, dx, dy, dz, 4.0F, Explosion.Mode.BREAK);

            oresExplode = false;

        }

    }

    @SubscribeEvent
    public static void bedrockOnBreak(BreakEvent event) {

        BlockPos bpos = event.getPos();

        if (placeBedrockOnBreak && !event.getWorld().isRemote()) {

            event.setCanceled(true);
            event.getWorld().setBlockState(bpos, Blocks.BEDROCK.getDefaultState(), 2);
            placeBedrockOnBreak = false;

        }

    }

    @SubscribeEvent
    public static void villagersDie(PlayerInteractEvent.EntityInteract event) {

        if (event.getTarget() instanceof VillagerEntity && burnVillagersOnInteract && !event.getWorld().isRemote) {

            ((VillagerEntity) event.getTarget()).addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1));
            event.getTarget().setFire(10);
            burnVillagersOnInteract = false;

        }

    }

    @SubscribeEvent
    public static void workbenchesBreak(PlayerInteractEvent.RightClickBlock event) {

        World world = event.getWorld();
        Block block = world.getBlockState(event.getPos()).getBlock();

        if (destroyWorkbenchesOnInteract && !world.isRemote) {

            if (block == Blocks.CRAFTING_TABLE || block == Blocks.FURNACE) {

                event.setCanceled(true);
                world.destroyBlock(event.getPos(), false);
                destroyWorkbenchesOnInteract = false;

            }

        }

    }


}