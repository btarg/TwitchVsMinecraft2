package com.icrazyblaze.twitchmod.gui;

import com.icrazyblaze.twitchmod.util.Reference;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;


public class MessageboxGui extends Screen {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/messagebox_background.png");
    public static String message = null;
    private boolean displayGUI = true;
    private static Minecraft mc;

    public MessageboxGui(String message) {
        super(new StringTextComponent("Message Box"));
        MessageboxGui.message = message;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void init() {

        Button btn = new Button(200, width / 2 - 75, height / 2 + 55, 150, I18n.format("gui.done"), button -> stopDisplay());
        addButton(btn);

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        this.renderBackground();

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        int x = (width / 2) - 88;
        int y = (height / 2) - 83;

        this.blit(x, y, 0, 0, 256, 256);
        mc.fontRenderer.drawString("Message Box", width / 2f - 32, height / 2f - 78, 4210752);
        mc.fontRenderer.drawSplitString(message, x + 7, height / 2 - 60, 165, 4210752);

        if (displayGUI) {
            super.render(mouseX, mouseY, partialTicks);
        } else {
            mc.player.closeScreen();
        }

    }

    private void stopDisplay() {
        this.displayGUI = false;
    }
}
