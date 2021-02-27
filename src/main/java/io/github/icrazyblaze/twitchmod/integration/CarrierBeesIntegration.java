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

    private static final RegistryObject<EntityType<?>> CARRIER_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "carrier_bee"), ForgeRegistries.ENTITIES);
    private static final RegistryObject<EntityType<?>> BOMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "bomble_bee"), ForgeRegistries.ENTITIES);
    private static final RegistryObject<EntityType<?>> FUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "fumble_bee"), ForgeRegistries.ENTITIES);


    public static void initDynamicCommands(String sender) {

        registerCommand(() -> spawnCarrierBee(sender, CARRIER_BEE.get()), "carrierbee", "bee");
        registerCommand(() -> spawnCarrierBee(sender, BOMBLE_BEE.get()), "bomblebee", "bee2");
        registerCommand(() -> spawnCarrierBee(sender, FUMBLE_BEE.get()), "fumblebee", "bee3");

    }

    public static void spawnCarrierBee(String name, EntityType type) {

        ServerPlayerEntity player = player();

        // Get the entity without referencing CarrierBeeEntity explicitly
        MobEntity bee = (MobEntity) CARRIER_BEE.get().create(player.world);

        // Give it an item and name
        bee.setHeldItem(bee.getActiveHand(), Objects.requireNonNull(CommandHandlers.getRandomItemStack()));
        bee.setCustomName(new StringTextComponent(name));
        bee.setDropChance(EquipmentSlotType.MAINHAND, 1.0F);

        player.sendStatusMessage(new StringTextComponent(TextFormatting.YELLOW + name + " sent some support!"), true);
        CommandHandlers.spawnMob(bee);

    }

}
