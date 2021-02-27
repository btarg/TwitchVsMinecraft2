package io.github.icrazyblaze.twitchmod.integration;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.icrazyblaze.twitchmod.chat.ChatCommands.registerCommand;
import static io.github.icrazyblaze.twitchmod.util.PlayerHelper.player;

public class ChanceCubesIntegration {

    private final RegistryObject<Block> CHANCE_CUBE = RegistryObject.of(new ResourceLocation("chancecubes", "chance_cube"), ForgeRegistries.BLOCKS);
    private final RegistryObject<Block> GIANT_CHANCE_CUBE = RegistryObject.of(new ResourceLocation("chancecubes", "compact_giant_chance_cube"), ForgeRegistries.BLOCKS);


    public static void initCommands() {
        ModProxy.chanceCubesProxy.ifPresent(proxy -> {

            registerCommand(() -> CommandHandlers.setBlock(player().getBlockPos(), proxy.CHANCE_CUBE.get().getDefaultState()), "chancecube", "cube");
            registerCommand((ChanceCubesIntegration::placeGiantChanceCube), "giantchancecube", "giantcube");

        });
    }

    public static void placeGiantChanceCube() {

        ServerPlayerEntity player = player();
        Block cube = ModProxy.chanceCubesProxy.get().GIANT_CHANCE_CUBE.get();

        Vector3d lookVector = player.getLookVec();

        double dx = player.getX() + (lookVector.x * 4);
        double dz = player.getZ() + (lookVector.z * 4);

        BlockPos bpos = new BlockPos(dx, player.getY(), dz);

        CommandHandlers.setBlock(bpos, cube.getDefaultState());
        cube.onBlockPlacedBy(player.world, bpos, cube.getDefaultState(), player, ItemStack.EMPTY);

    }

}
