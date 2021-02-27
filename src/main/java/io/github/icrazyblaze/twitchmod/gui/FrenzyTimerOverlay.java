package io.github.icrazyblaze.twitchmod.gui;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.chat.ChatPicker;
import io.github.icrazyblaze.twitchmod.util.TimerSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
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

            Minecraft mc = Minecraft.getInstance();
            String text = "FRENZY MODE: " + TimerSystem.frenzyTimerSeconds;

            mc.fontRenderer.drawWithShadow(event.getMatrixStack(), text, 4, 4, TextFormatting.GOLD.getColor());

        }

    }

}