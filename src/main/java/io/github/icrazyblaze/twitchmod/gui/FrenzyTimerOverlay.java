package io.github.icrazyblaze.twitchmod.gui;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Thanks Silk!
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Main.MOD_ID)
public class FrenzyTimerOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Post event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        if (ChatPicker.instantCommands) {

            MutableComponent text = new TranslatableComponent("gui.twitchmod.frenzy_timer", TimerSystem.frenzyTimerSeconds);
            Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), text, 4, 4, ChatFormatting.GOLD.getColor());

        }
    }
}