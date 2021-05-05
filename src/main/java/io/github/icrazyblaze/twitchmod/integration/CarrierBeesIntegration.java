package io.github.icrazyblaze.twitchmod.integration;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static io.github.icrazyblaze.twitchmod.chat.ChatCommands.registerCommand;
import static io.github.icrazyblaze.twitchmod.util.PlayerHelper.player;

public class CarrierBeesIntegration {

    private final RegistryObject<EntityType<?>> CARRIER_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "carrier_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> BOMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "bomble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> FUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "fumble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> STUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "stumble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> TUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "tumble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> CRUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "crumble_bee"), ForgeRegistries.ENTITIES);

    public static void initDynamicCommands(String sender) {
        ModProxy.carrierBeesProxy.ifPresent(proxy -> {

            registerCommand(() -> spawnCarrierBee(sender, proxy.CARRIER_BEE.get()), "carrierbee", "bee");
            registerCommand(() -> spawnCarrierBee(sender, proxy.BOMBLE_BEE.get()), "bomblebee", "bee2");
            registerCommand(() -> spawnCarrierBee(sender, proxy.FUMBLE_BEE.get()), "fumblebee", "bee3");
            registerCommand(() -> spawnCarrierBee(sender, proxy.STUMBLE_BEE.get()), "stumblebee", "bee4");
            registerCommand(() -> spawnCarrierBee(sender, proxy.TUMBLE_BEE.get()), "tumblebee", "bee5");
            registerCommand(() -> spawnCarrierBee(sender, proxy.CRUMBLE_BEE.get()), "crumblebee", "bee6");

        });
    }

    public static void spawnCarrierBee(String name, EntityType<?> type) {

        ServerPlayerEntity player = player();

        // Get the entity without referencing CarrierBeeEntity explicitly
        MobEntity bee = (MobEntity) type.create(player.world);

        // Give it an item and name
        assert bee != null;
        bee.setHeldItem(bee.getActiveHand(), Objects.requireNonNull(CommandHandlers.getRandomItemStack(true)));
        bee.setCustomName(new StringTextComponent(name));
        bee.setDropChance(EquipmentSlotType.MAINHAND, 1.0F);

        player.sendStatusMessage(new StringTextComponent(TextFormatting.YELLOW + name + " sent you a bee!"), true);
        CommandHandlers.spawnMob(bee);

    }

}
