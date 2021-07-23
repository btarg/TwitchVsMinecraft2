package io.github.icrazyblaze.twitchmod.integration.mods;

import io.github.icrazyblaze.twitchmod.integration.ModProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.icrazyblaze.twitchmod.chat.ChatCommands.registerCommand;
import static io.github.icrazyblaze.twitchmod.util.PlayerHelper.player;

public class MimicModIntegration {

    private static final RegistryObject<EntityType<?>> MIMIC = RegistryObject.of(new ResourceLocation("mimic", "mimic"), ForgeRegistries.ENTITIES);

    public static void initCommands() {
        ModProxy.mimicModProxy.ifPresent(proxy -> {

            registerCommand((MimicModIntegration::spawnMimic), "mimic", "fakelootbox");

        });
    }

    public static void spawnMimic() {

        EntityType<?> type = MIMIC.get();
        ServerPlayer player = player();
        Mob mimic = (Mob) type.create(player.level);

        assert mimic != null;
        mimic.setPos(player.getX(), player.getY(), player.getZ());

        player.level.addFreshEntity(mimic);

    }

}
