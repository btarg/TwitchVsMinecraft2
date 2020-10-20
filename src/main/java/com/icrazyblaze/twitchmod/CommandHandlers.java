package com.icrazyblaze.twitchmod;

import com.icrazyblaze.twitchmod.chat.ChatPicker;
import com.icrazyblaze.twitchmod.gui.MessageboxScreen;
import com.icrazyblaze.twitchmod.network.MessageboxPacket;
import com.icrazyblaze.twitchmod.network.PacketHandler;
import com.icrazyblaze.twitchmod.util.BotConfig;
import com.icrazyblaze.twitchmod.util.PlayerHelper;
import com.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


/**
 * This class contains every method used by commands registered in the ChatPicker class.
 *
 * @see com.icrazyblaze.twitchmod.chat.ChatPicker
 */
public class CommandHandlers {

    private static final ResourceLocation[] lootArray = {LootTables.CHESTS_SIMPLE_DUNGEON, LootTables.CHESTS_ABANDONED_MINESHAFT, LootTables.CHESTS_SPAWN_BONUS_CHEST};
    private static final List<ResourceLocation> lootlist = Arrays.asList(lootArray);
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
    public static boolean oresExplode = false;
    public static boolean placeBedrock = false;
    public static boolean killVillagers = false;
    public static boolean destroyWorkbenches = false;
    public static ArrayList<String> messagesList = new ArrayList<>();
    private static boolean previousDeathTimerState = false;

    // UPDATE: moved potions into one function
    public static void addPotionEffects(EffectInstance[] effectInstances) {

        ServerPlayerEntity player = player();

        for (EffectInstance effect : effectInstances) {
            player.addPotionEffect(effect);
        }

    }

    // UPDATE: moved player getter into its own class
    public static ServerPlayerEntity player() {
        return PlayerHelper.player();
    }

    public static void clearEffects() {
        player().clearActivePotions();
    }

    public static void setOnFire() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        BlockState bposState = player.world.getBlockState(bpos);

        if (bposState == Blocks.AIR.getDefaultState()) {
            player.world.setBlockState(bpos, Blocks.FIRE.getDefaultState());
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
            world.func_241114_a_(time);
        }

    }

    public static void drainHealth() {

        ServerPlayerEntity player = player();

        // Half the player's health
        float halfhealth = player.getHealth() / 2;

        if (halfhealth == 0) {
            killPlayer();
        } else {
            player.setHealth(halfhealth);
        }

    }

    public static void killPlayer() {

        player().onKillCommand();

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

        TickHandler.deathTimerSeconds = 60;
        TickHandler.deathTimerTicks = 0;
        TickHandler.deathTimer = true;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.DARK_RED + "Chat has given you " + TickHandler.deathTimerSeconds + " seconds to live."), true);

    }

    public static void frenzyTimer() {

        // Prevent spamming
        if (ChatPicker.instantCommands) {
            return;
        }

        TickHandler.frenzyTimerSeconds = 10;
        TickHandler.frenzyTimerTicks = 0;
        ChatPicker.instantCommands = true;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.GOLD + "FRENZY MODE! All commands are executed for the next " + TickHandler.frenzyTimerSeconds + " seconds."), true);

    }

    public static void graceTimer() {

        if (ChatPicker.instantCommands) {
            return;
        }

        ChatPicker.enabled = false;
        TickHandler.peaceTimerSeconds = 30;
        TickHandler.peaceTimerTicks = 0;
        TickHandler.peaceTimer = true;

        previousDeathTimerState = TickHandler.deathTimer;
        TickHandler.deathTimer = false;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + "Commands are turned off for " + TickHandler.peaceTimerSeconds + " seconds."), true);

    }

    public static void disableGraceTimer() {

        ChatPicker.enabled = true;
        TickHandler.peaceTimer = false;
        TickHandler.deathTimer = previousDeathTimerState;

        player().sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + "Commands are now enabled!"), true);

    }

    public static void disableFrenzyTimer() {

        ChatPicker.instantCommands = false;
        player().sendStatusMessage(new StringTextComponent(TextFormatting.GOLD + "Frenzy mode is now disabled."), true);

    }

    public static void floorIsLava() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY() - 1, player.getPosZ());
        player.world.setBlockState(bpos, Blocks.LAVA.getDefaultState());

    }

    public static void placeWater() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        player.world.setBlockState(bpos, Blocks.WATER.getDefaultState());

    }

    public static void placeSponge() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());

        player.world.setBlockState(bpos, Blocks.SPONGE.getDefaultState());

    }

    public static void spawnAnvil() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY() + 16, player.getPosZ());

        player.world.setBlockState(bpos, Blocks.ANVIL.getDefaultState());

    }

    public static void spawnCobweb() {

        ServerPlayerEntity player = player();

        player.world.setBlockState(new BlockPos(player.getPosX(), player.getPosY() + 1, player.getPosZ()), Blocks.COBWEB.getDefaultState());
        player.world.setBlockState(new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ()), Blocks.COBWEB.getDefaultState());

    }

    public static void spawnMob(Entity ent) {

        ServerPlayerEntity player = player();

        Vector3d lookVector = player.getLookVec();

        double dx = player.getPosX() + (lookVector.x * 4);
        double dz = player.getPosZ() + (lookVector.z * 4);

        ent.setPosition(dx, player.getPosY(), dz);

        player.world.addEntity(ent);

    }

    public static void creeperScare() {
        playSound(SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    public static void playSound(SoundEvent sound, SoundCategory category, float volume, float pitch) {

        ServerPlayerEntity player = player();
        player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), sound, category, volume, pitch);

    }

    public static void zombieScare() {
        playSound(SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    public static void skeletonScare() {
        playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    public static void witchScare() {
        playSound(SoundEvents.ENTITY_WITCH_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    public static void ghastScare() {
        playSound(SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    public static void anvilScare() {
        playSound(SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static void pigmanScare() {
        playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, SoundCategory.HOSTILE, 2.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
    }

    public static void spawnFireball() {

        ServerPlayerEntity player = player();

        Vector3d lookVector = player.getLookVec();

        double dx = player.getPosX() + (lookVector.x * 2);
        double dz = player.getPosZ() + (lookVector.z * 2);

        Entity ent = new FireballEntity(EntityType.FIREBALL, player.world);
        ent.setPosition(dx, player.getPosY(), dz);
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
        BlockPos bpos;

        Vector3d lookVector = player.getLookVec();
        Vector3d posVector = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        RayTraceContext context = new RayTraceContext(posVector, lookVector.scale(range).add(posVector), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult rayTrace = player.world.rayTraceBlocks(context);

        if (rayTrace == null || rayTrace.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        bpos = new BlockPos(rayTrace.getHitVec());

        player.world.destroyBlock(bpos, false);

    }

    public static void infestBlock() {

        ServerPlayerEntity player = player();

        int range = 50;
        BlockPos bpos;

        Vector3d lookVector = player.getLookVec();
        Vector3d posVector = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        RayTraceContext context = new RayTraceContext(posVector, lookVector.scale(range).add(posVector), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult rayTrace = player.world.rayTraceBlocks(context);

        if (rayTrace == null || rayTrace.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        bpos = new BlockPos(rayTrace.getHitVec());

        Block thisBlock = player.world.getBlockState(bpos).getBlock();

        if (thisBlock == Blocks.COBBLESTONE) {
            player.world.setBlockState(bpos, Blocks.INFESTED_COBBLESTONE.getDefaultState());
        } else if (thisBlock == Blocks.STONE) {
            player.world.setBlockState(bpos, Blocks.INFESTED_STONE.getDefaultState());
        } else if (thisBlock == Blocks.STONE_BRICKS) {
            player.world.setBlockState(bpos, Blocks.INFESTED_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.MOSSY_STONE_BRICKS) {
            player.world.setBlockState(bpos, Blocks.INFESTED_MOSSY_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.CRACKED_STONE_BRICKS) {
            player.world.setBlockState(bpos, Blocks.INFESTED_CRACKED_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.CHISELED_STONE_BRICKS) {
            player.world.setBlockState(bpos, Blocks.INFESTED_CHISELED_STONE_BRICKS.getDefaultState());
        }

    }

    public static void placeGlass() {

        ServerPlayerEntity player = player();

        double dx = player.getPosX();
        double dy = player.getPosY();
        double dz = player.getPosZ();

        // TODO: This code is shit, replace it
        BlockPos[] positions = {new BlockPos(dx, dy + 2, dz), new BlockPos(dx, dy, dz - 1), new BlockPos(dx, dy + 1, dz - 1), new BlockPos(dx, dy, dz + 1), new BlockPos(dx, dy + 1, dz + 1), new BlockPos(dx - 1, dy, dz), new BlockPos(dx - 1, dy + 1, dz), new BlockPos(dx + 1, dy, dz), new BlockPos(dx + 1, dy + 1, dz), new BlockPos(dx, dy - 1, dz)};

        for (BlockPos bpos : positions) {
            player.world.setBlockState(bpos, Blocks.GLASS.getDefaultState());
        }


    }

    public static void dropItem() { // Thanks Amoo!

        ServerPlayerEntity player = player();

        ItemStack currentItem = player.inventory.getCurrentItem();

        if (currentItem != ItemStack.EMPTY) {

            player.dropItem(currentItem, false, true);
            player.inventory.deleteStack(currentItem);

        }

    }

    public static void removeRandom() {

        ServerPlayerEntity player = player();

        // Delete a random item
        int r = rand.nextInt(player.inventory.getSizeInventory());

        ItemStack randomItem = player.inventory.getStackInSlot(r);

        if (randomItem != ItemStack.EMPTY) {

            player.inventory.deleteStack(randomItem);

        } else {

            removeRandom();

        }

    }

    public static void giveRandom() {

        // Give the player a random item
        int length = ForgeRegistries.ITEMS.getKeys().toArray().length;
        int r = rand.nextInt(length);

        Item select = Item.getItemById(r);

        if (select != null) {

            ItemStack stack = new ItemStack(select);
            stack.setCount(rand.nextInt(stack.getMaxStackSize()));

            // Remove the random item here to prevent an item being removed and no item being given to the player
            removeRandom();

            player().addItemStackToInventory(stack);

        }

    }

    public static void messWithInventory(String sender) {

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            giveRandom();

            // Show chat message
            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + sender + " giveth, and " + sender + " taketh away."), true);

        }

    }

    // Thank you to ChiKitsune for writing this code!
    // https://github.com/ChiKitsune/SwapThings/blob/master/src/main/java/chikitsune/swap_things/commands/ShuffleInventory.java
    public static void shuffleInventory(String sender) {

        ServerPlayerEntity player = player();

        ItemStack tempItem = ItemStack.EMPTY;
        int tempRandNum = 0;

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {

            tempItem = ItemStack.EMPTY;
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

        if (!(name.length() > 7))
            return;

        ServerPlayerEntity player = player();

        if (!player.inventory.isEmpty()) {

            // Limit custom rename to 32 characters
            String newname = name.substring(7).substring(0, 32);

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
            int level = rand.nextInt(1, enchantment.getMaxLevel() + 1);

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

    public static void curseItem() {

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

    public static void dropAll() {

        player().inventory.dropAllItems();

    }

    public static void dismount() {

        ServerPlayerEntity player = player();

        if (player.isOnePlayerRiding()) {
            player.stopRiding();
        }

    }

    public static void showMessagebox(String message) {

        if (!(message.length() > 11))
            return;

        // Cut off the command
        message = message.substring(11);

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

    public static void placeSign(String message) {

        if (!(message.length() > 5))
            return;

        ServerPlayerEntity player = player();

        // Cut off the command
        message = message.substring(5);

        // Split every 15 characters
        int maxlength = 15;
        String[] splitMessage = message.split("(?<=\\G.{" + maxlength + "})");


        BlockPos bpos = player.getPosition();

        double xpos = player.getPosX();
        double ypos = player.getPosY();
        double zpos = player.getPosZ();

        BlockPos bposBelow = new BlockPos(xpos, ypos - 1, zpos);


        // Rotate the sign to face the player
        int playerFace = MathHelper.floor((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;

        // Set block state to air before placing sign
        player.world.setBlockState(bpos, Blocks.AIR.getDefaultState());

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
        player.world.setBlockState(bposBelow, Blocks.GLOWSTONE.getDefaultState());

    }

    public static void placeChest() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        Block bposBlock = player.world.getBlockState(bpos).getBlock();

        // Make sure we don't replace any chests
        if (bposBlock != Blocks.CHEST || bposBlock != Blocks.TRAPPED_CHEST) {

            player.world.setBlockState(bpos, Blocks.CHEST.getDefaultState());

            TileEntity tileEntity = player.world.getTileEntity(bpos);

            if (tileEntity instanceof ChestTileEntity) {

                ((ChestTileEntity) tileEntity).setLootTable(lootlist.get(rand.nextInt(lootlist.size())), rand.nextLong());
                ((ChestTileEntity) tileEntity).fillWithLoot(player);

            }

        }

    }

    public static void addToMessages(String message) {

        if (!(message.length() > 11))
            return;

        String newmsg = message.substring(11);
        messagesList.add(newmsg);

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

        player().sendMessage(message, player().getUniqueID());

    }

    @SubscribeEvent
    public static void explodeOnBreak(BreakEvent event) {

        Block thisBlock = event.getState().getBlock();

        if (!(thisBlock instanceof OreBlock)) {
            return;
        } else if (oresExplode && !event.getWorld().isRemote()) {

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

        if (placeBedrock && !event.getWorld().isRemote()) {

            event.setCanceled(true);
            event.getWorld().setBlockState(bpos, Blocks.BEDROCK.getDefaultState(), 2);
            placeBedrock = false;

        }

    }

    @SubscribeEvent
    public static void villagersDie(PlayerInteractEvent.EntityInteract event) {

        if (event.getTarget() instanceof VillagerEntity && killVillagers && !event.getWorld().isRemote) {

            ((VillagerEntity) event.getTarget()).addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE, 1, 1));
            event.getTarget().setFire(10);
            killVillagers = false;

        }

    }

    @SubscribeEvent
    public static void workbenchesBreak(PlayerInteractEvent.RightClickBlock event) {

        World world = event.getWorld();
        Block block = world.getBlockState(event.getPos()).getBlock();

        if (destroyWorkbenches && !world.isRemote) {

            if (block == Blocks.CRAFTING_TABLE || block == Blocks.FURNACE) {

                event.setCanceled(true);
                world.destroyBlock(event.getPos(), false);
                destroyWorkbenches = false;

            }

        }

    }


}