package io.github.icrazyblaze.twitchmod.gui;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.util.timers.TimerSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Thanks Silk!
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Main.MOD_ID)
public class PeaceTimerOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Post event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        if (TimerSystem.peaceTimerEnabled) {

            Minecraft mc = Minecraft.getInstance();
            String text = "COMMANDS DISABLED: " + TimerSystem.peaceTimerSeconds;

            mc.font.drawShadow(event.getMatrixStack(), text, 4, 4, ChatFormatting.AQUA.getColor());

        }

    }

}