package io.github.icrazyblaze.twitchmod.integration.mods;

import io.github.icrazyblaze.twitchmod.CommandHandlers;
import io.github.icrazyblaze.twitchmod.integration.ModProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.fmllegacy.RegistryObject;
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
    private final RegistryObject<EntityType<?>> DRUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "drumble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> JUMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "jumble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> THIMBLE_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "thimble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> BOOGER_BEE = RegistryObject.of(new ResourceLocation("carrierbees", "thimble_bee"), ForgeRegistries.ENTITIES);
    private final RegistryObject<EntityType<?>> BEEHEMOTH = RegistryObject.of(new ResourceLocation("carrierbees", "beehemoth"), ForgeRegistries.ENTITIES);


    public static void initDynamicCommands(String sender) {
        ModProxy.carrierBeesProxy.ifPresent(proxy -> {

            registerCommand(() -> spawnCarrierBee(sender, proxy.CARRIER_BEE.get()), "carrierbee", "bee");
            registerCommand(() -> spawnCarrierBee(sender, proxy.BOMBLE_BEE.get()), "bomblebee", "bee2");
            registerCommand(() -> spawnCarrierBee(sender, proxy.FUMBLE_BEE.get()), "fumblebee", "bee3");
            registerCommand(() -> spawnCarrierBee(sender, proxy.STUMBLE_BEE.get()), "stumblebee", "bee4");
            registerCommand(() -> spawnCarrierBee(sender, proxy.TUMBLE_BEE.get()), "tumblebee", "bee5");
            registerCommand(() -> spawnCarrierBee(sender, proxy.CRUMBLE_BEE.get()), "crumblebee", "bee6");
            registerCommand(() -> spawnCarrierBee(sender, proxy.DRUMBLE_BEE.get()), "drumblebee", "bee7");
            registerCommand(() -> spawnCarrierBee(sender, proxy.JUMBLE_BEE.get()), "jumblebee", "bee8");
            registerCommand(() -> spawnCarrierBee(sender, proxy.THIMBLE_BEE.get()), "thimblebee", "bee9");
            registerCommand(() -> spawnCarrierBee(sender, proxy.BOOGER_BEE.get()), "boogerbee", "bee10");
            registerCommand(() -> spawnCarrierBee(sender, proxy.BEEHEMOTH.get()), "beehemoth", "bigbee");

        });
    }

    public static void spawnCarrierBee(String name, EntityType<?> type) {

        ServerPlayer player = player();

        // Get the entity without referencing CarrierBeeEntity explicitly
        Mob bee = (Mob) type.create(player.level);

        // Give it an item and name
        assert bee != null;
        bee.setItemInHand(bee.getUsedItemHand(), Objects.requireNonNull(CommandHandlers.getRandomItemStack(true)));
        bee.setCustomName(new TextComponent(name));
        bee.setDropChance(EquipmentSlot.MAINHAND, 1.0F);

        player.displayClientMessage(new TextComponent(ChatFormatting.YELLOW + name + " sent you a bee!"), true);
        CommandHandlers.spawnMob(bee);

    }

}
