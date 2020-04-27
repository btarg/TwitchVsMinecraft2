package com.icrazyblaze.twitchmod;

import com.icrazyblaze.twitchmod.gui.MessageboxGui;
import com.icrazyblaze.twitchmod.network.GuiMessage;
import com.icrazyblaze.twitchmod.network.PacketHandler;
import com.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;


/**
 * This class contains every method used by commands registered in the ChatPicker class.
 *
 * @see com.icrazyblaze.twitchmod.chat.ChatPicker
 */
public class BotCommands {

    public static final Block[] oresArray = {Blocks.DIAMOND_ORE, Blocks.REDSTONE_ORE, Blocks.REDSTONE_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE, Blocks.EMERALD_ORE, Blocks.COAL_ORE};
    public static final ResourceLocation[] lootArray = {LootTables.CHESTS_SIMPLE_DUNGEON, LootTables.CHESTS_ABANDONED_MINESHAFT, LootTables.CHESTS_SPAWN_BONUS_CHEST};
    public static final List<Block> oresList = Arrays.asList(oresArray);
    public static final List<ResourceLocation> lootlist = Arrays.asList(lootArray);
    public static String username = null;
    public static boolean oresExplode = false;
    public static boolean placeBedrock = false;
    public static ArrayList<String> messagesList = new ArrayList<>();

    public static MinecraftServer defaultServer = null;


    public static ServerPlayerEntity player() {

        PlayerList playerList = defaultServer.getPlayerList();
        ServerPlayerEntity player = playerList.getPlayerByUsername(username);

        if (player == null) {
            player = playerList.getPlayers().get(0);
        }

        return player;

    }


    public static void addSlowness() {
        player().addPotionEffect(new EffectInstance(Effects.SLOWNESS, 400, 5));
    }

    public static void addHunger() {
        player().addPotionEffect(new EffectInstance(Effects.HUNGER, 800, 255));
    }

    public static void addSpeed() {
        player().addPotionEffect(new EffectInstance(Effects.SPEED, 400, 10));
    }

    public static void addPoison() {
        player().addPotionEffect(new EffectInstance(Effects.POISON, 400, 0));
    }

    public static void addNausea() {
        player().addPotionEffect(new EffectInstance(Effects.NAUSEA, 400, 0));
    }

    public static void addWeakness() {
        player().addPotionEffect(new EffectInstance(Effects.WEAKNESS, 400, 1));
    }

    public static void addFatigue() {
        player().addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 400, 0));
    }

    public static void addLevitation() {
        player().addPotionEffect(new EffectInstance(Effects.LEVITATION, 200, 1));
    }

    public static void addHaste() {
        player().addPotionEffect(new EffectInstance(Effects.HASTE, 400, 2));
    }

    public static void noFall() {
        player().addPotionEffect(new EffectInstance(Effects.LEVITATION, 400, 255));
    }

    public static void addRegen() {

        ServerPlayerEntity player = player();

        player.addPotionEffect(new EffectInstance(Effects.HEALTH_BOOST, 400, 1));
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 400, 1));

    }

    public static void addJumpBoost() {
        player().addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 400, 2));
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

    public static void heavyRain() {

        ServerPlayerEntity player = player();

        player.world.getWorldInfo().setRaining(true);
        player.world.getWorldInfo().setThundering(true);

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
            killPlayer();
        } else {
            player.setHealth(halfhealth);
        }

    }

    public static void setSpawn() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
        player.setSpawnPoint(bpos, true, false, player().getSpawnDimension());

    }

    public static void killPlayer() {

        player().onKillCommand();

    }

    public static void deathTimer() {

        TickHandler.timerSeconds = 60;
        TickHandler.timerTicks = 0;
        TickHandler.killTimer = true;

    }


    public static void floorIsLava() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY() - 1, player.getPosZ());
        player.world.setBlockState(bpos, Blocks.LAVA.getDefaultState());

    }

    public static void waterBucket() {

        ServerPlayerEntity player = player();

        BlockPos bpos = player.getPosition();
        player.world.setBlockState(bpos, Blocks.WATER.getDefaultState());

    }

    public static void spawnAnvil() {

        ServerPlayerEntity player = player();

        BlockPos bpos = new BlockPos(player.getPosX(), player.getPosY() + 16, player.getPosZ());

        player.world.setBlockState(bpos, Blocks.ANVIL.getDefaultState());

    }

    public static void spawnCobweb() {

        ServerPlayerEntity player = player();

        player.world.setBlockState(new BlockPos(player().getPosX(), player().getPosY() + 1, player().getPosZ()), Blocks.COBWEB.getDefaultState());
        player.world.setBlockState(new BlockPos(player().getPosX(), player().getPosY() - 1, player().getPosZ()), Blocks.COBWEB.getDefaultState());

    }

    public static void spawnMobBehind(Entity ent) {

        ServerPlayerEntity player = player();

        Vec3d lookVector = player.getLookVec();

        double dx = player.getPosX() - (lookVector.x * 4);
        double dz = player.getPosZ() - (lookVector.z * 4);

        ent.setPosition(dx, player.getPosY(), dz);

        player.world.addEntity(ent);

    }

    public static void spawnMob(Entity ent) {

        ServerPlayerEntity player = player();

        Vec3d lookVector = player.getLookVec();

        double dx = player.getPosX() + (lookVector.x * 4);
        double dz = player.getPosZ() + (lookVector.z * 4);

        ent.setPosition(dx, player.getPosY(), dz);

        player.world.addEntity(ent);

    }

    public static void creeperScare() {
        playSound(SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.0F);
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

    public static void anvilScare() {
        playSound(SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static void playSound(SoundEvent sound, SoundCategory category, float volume, float pitch) {

        ServerPlayerEntity player = player();
        player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), sound, category, volume, pitch);

    }

    public static void spawnFireball() {

        ServerPlayerEntity player = player();

        Vec3d lookVector = player.getLookVec();

        double dx = player.getPosX() + (lookVector.x * 2);
        double dz = player.getPosZ() + (lookVector.z * 2);

        Entity ent = new FireballEntity(EntityType.FIREBALL, player.world);
        ent.setPosition(dx, player.getPosY() + player.getEyeHeight(), dz);

        ent.setVelocity(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);

        player.world.addEntity(ent);

    }

    public static void spawnLightning() {

        player().world.addEntity(new LightningBoltEntity(player().world, player().getPosX(), player().getPosY(), player().getPosZ(), false));

    }


    public static void breakBlock() {

        ServerPlayerEntity player = player();

        int range = 50;
        BlockPos bpos;

        Vec3d lookVector = player.getLookVec();
        Vec3d posVector = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        RayTraceContext context = new RayTraceContext(posVector, lookVector.scale(range).add(posVector), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult rayTrace = player.world.rayTraceBlocks(context);

        if (rayTrace == null || rayTrace.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        bpos = new BlockPos(rayTrace.getHitVec());

        player.world.destroyBlock(bpos, false);

    }

    public static void monsterEgg() {

        ServerPlayerEntity player = player();

        int range = 50;
        BlockPos bpos;

        Vec3d lookVector = player.getLookVec();
        Vec3d posVector = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());

        RayTraceContext context = new RayTraceContext(posVector, lookVector.scale(range).add(posVector), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult rayTrace = player.world.rayTraceBlocks(context);

        if (rayTrace == null || rayTrace.getType() == RayTraceResult.Type.MISS) {
            return;
        }

        bpos = new BlockPos(rayTrace.getHitVec());

        BlockState thisBlock = player().world.getBlockState(bpos);

        if (thisBlock.getBlock() == Blocks.STONE) {
            player.world.setBlockState(bpos, Blocks.INFESTED_COBBLESTONE.getDefaultState());
        } else if (thisBlock.getBlock() == Blocks.STONE_BRICKS) {
            player.world.setBlockState(bpos, Blocks.INFESTED_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.MOSSY_STONE_BRICKS.getDefaultState()) {
            player.world.setBlockState(bpos, Blocks.INFESTED_MOSSY_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.CRACKED_STONE_BRICKS.getDefaultState()) {
            player.world.setBlockState(bpos, Blocks.INFESTED_CRACKED_STONE_BRICKS.getDefaultState());
        } else if (thisBlock == Blocks.CHISELED_STONE_BRICKS.getDefaultState()) {
            player.world.setBlockState(bpos, Blocks.INFESTED_CHISELED_STONE_BRICKS.getDefaultState());
        }

    }

    public static void spawnGlass() {

        ServerPlayerEntity player = player();

        double dx = player.getPosX();
        double dy = player.getPosY();
        double dz = player.getPosZ();

        BlockPos[] positions = {new BlockPos(dx, dy + 2, dz), new BlockPos(dx, dy, dz - 1), new BlockPos(dx, dy + 1, dz - 1), new BlockPos(dx, dy, dz + 1), new BlockPos(dx, dy + 1, dz + 1), new BlockPos(dx - 1, dy, dz), new BlockPos(dx - 1, dy + 1, dz), new BlockPos(dx + 1, dy, dz), new BlockPos(dx + 1, dy + 1, dz), new BlockPos(dx, dy - 2, dz)};

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

        Random rand = new Random();

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

        Random rand = new Random();

        // Give the player a random item
        int length = ForgeRegistries.ITEMS.getKeys().toArray().length;
        int r = 0;

        while (r == 0) {
            r = rand.nextInt(length);
        }

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
            player.sendMessage(new StringTextComponent(TextFormatting.RED + sender + " giveth, and " + sender + " taketh away."));

        }

    }

    public static void renameItem(String name) {

        ServerPlayerEntity player = player();

        Random rand = new Random();

        if (!player.inventory.isEmpty()) {

            String newname = name.substring(7);

            ItemStack currentitem = player.inventory.getCurrentItem();

            if (currentitem != ItemStack.EMPTY) {

                currentitem.setDisplayName(new StringTextComponent(newname));

            } else {

                // Rename a random item in the player's inventory when the player isn't holding anything
                int r = rand.nextInt(player.inventory.getSizeInventory());
                ItemStack randomItem = player.inventory.getStackInSlot(r);

                if (randomItem != ItemStack.EMPTY && !randomItem.getDisplayName().toString().equals(newname)) {

                    randomItem.setDisplayName(new StringTextComponent(newname));

                } else {
                    // Try again
                    renameItem(name);
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

        // Cut off the command
        message = message.substring(11);

        // Then trim the string to the proper length (324 chars max)
        message = message.substring(0, Math.min(message.length(), 324));

        //PacketHandler.INSTANCE.sendToServer(new GuiMessage(message));
        PacketHandler.INSTANCE.sendTo(new GuiMessage(message), player().connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);

    }

    /*
    This code is run on the client when the GuiMessage packet is received.
    */
    @OnlyIn(Dist.CLIENT)
    public static void showMessageBoxClient(String message) {

        Minecraft.getInstance().displayGuiScreen(new MessageboxGui(message));

    }

    public static void placeSign(String message) {

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

            for (int i = 0; i < lines; i++)
            {
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

                Random rand = new Random();

                ((ChestTileEntity) tileEntity).setLootTable(lootlist.get(rand.nextInt(lootlist.size())), rand.nextLong());
                ((ChestTileEntity) tileEntity).fillWithLoot(null);

            }

        }

    }

    public static void addToMessages(String message) {

        String newmsg = message.substring(11);
        messagesList.add(newmsg);

    }

    public static void chooseRandomMessage() {

        if (!messagesList.isEmpty()) {

            Random rand = new Random();
            int r = rand.nextInt(messagesList.size());
            String message = messagesList.get(r);

            messagesList.remove(r);

            r = rand.nextInt(TextFormatting.values().length);

            player().sendMessage(new StringTextComponent(TextFormatting.fromColorIndex(r) + message));

        }

    }


    @SubscribeEvent
    public void explodeOnBreak(BreakEvent event) {

        Block thisBlock = event.getState().getBlock();

        if (!oresList.contains(thisBlock)) {
            return;
        } else if (oresExplode) {

            event.getWorld().getWorld().createExplosion(null, player().getPosX(), player().getPosY(), player().getPosZ(), 4.0F, Explosion.Mode.BREAK);

            oresExplode = false;

        }

    }

    @SubscribeEvent
    public void bedrockOnBreak(BreakEvent event) {

        BlockPos bpos = event.getPos();

        if (placeBedrock) {

            event.setCanceled(true);
            event.getWorld().setBlockState(bpos, Blocks.BEDROCK.getDefaultState(), 2);
            placeBedrock = false;

        }

    }

}