package io.github.icrazyblaze.twitchmod.gui;

import io.github.icrazyblaze.twitchmod.util.Reference;
import io.github.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Thanks Silk!
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID)
public class PeaceTimerOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGameOverlayEvent.Post event) {

        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        if (TickHandler.peaceTimer) {

            Minecraft mc = Minecraft.getInstance();
            String text = "COMMANDS DISABLED: " + TickHandler.peaceTimerSeconds;

            mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), text, 4, 4, TextFormatting.AQUA.getColor());

        }

    }

}