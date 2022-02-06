package io.github.icrazyblaze.twitchmod.integration.mods;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.integration.ModProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.icrazyblaze.twitchmod.chat.ChatCommands.registerCommand;
import static io.github.icrazyblaze.twitchmod.util.PlayerHelper.player;

public class ChanceCubesIntegration {

    private final RegistryObject<Block> CHANCE_CUBE = RegistryObject.of(new ResourceLocation("chancecubes", "chance_cube"), ForgeRegistries.BLOCKS);
    private final RegistryObject<Block> GIANT_CHANCE_CUBE = RegistryObject.of(new ResourceLocation("chancecubes", "compact_giant_chance_cube"), ForgeRegistries.BLOCKS);


    public static void initCommands() {
        ModProxy.chanceCubesProxy.ifPresent(proxy -> {

            registerCommand(() -> CommandHandlers.setBlock(player().blockPosition(), proxy.CHANCE_CUBE.get().defaultBlockState()), "chancecube", "cube");
            registerCommand((ChanceCubesIntegration::placeGiantChanceCube), "giantchancecube", "giantcube");

        });
    }

    public static void placeGiantChanceCube() {

        ServerPlayer player = player();
        Block cube = ModProxy.chanceCubesProxy.get().GIANT_CHANCE_CUBE.get();

        Vec3 lookVector = player.getLookAngle();

        double dx = player.getX() + (lookVector.x * 4);
        double dz = player.getZ() + (lookVector.z * 4);

        BlockPos bpos = new BlockPos(dx, player.getY(), dz);

        CommandHandlers.setBlock(bpos, cube.defaultBlockState());
        cube.setPlacedBy(player.level, bpos, cube.defaultBlockState(), player, ItemStack.EMPTY);

    }

}
