package io.github.icrazyblaze.twitchmod.integration.mods;

import io.github.icrazyblaze.twitchmod.integration.ModProxy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
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
        ServerPlayerEntity player = player();
        MobEntity mimic = (MobEntity) type.create(player.level);

        assert mimic != null;
        mimic.setPos(player.getX(), player.getY(), player.getZ());

        player.level.addFreshEntity(mimic);

    }

}
