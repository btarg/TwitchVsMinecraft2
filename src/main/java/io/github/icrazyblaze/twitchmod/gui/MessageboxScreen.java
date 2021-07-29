package io.github.icrazyblaze.twitchmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.icrazyblaze.twitchmod.Main;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageboxScreen extends Screen {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/messagebox_background.png");
    public static String message = null;

    public MessageboxScreen(String message) {
        super(new TextComponent("Message Box"));
        MessageboxScreen.message = message;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {

        renderBackground(stack);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, (BG_TEXTURE));

        // Show the background
        this.blit(stack, (width / 2) - 87, (height / 2) - 83, 0, 0, 256, 256);

        // Draw title
        this.font.draw(stack, "Message Box", width / 2f - 32, height / 2f - 78, 4210752);

        // Draw wrapped text
        List<FormattedCharSequence> text = this.font.split(new TextComponent(message), 165);
        for (int i = 0; i < text.size(); i++) {
            this.font.draw(stack, text.get(i), (width / 2f) - (this.font.width(text.get(i)) / 2f), (height / 2f - 60) + (this.font.lineHeight * i), 4210752);
        }

        super.render(stack, mouseX, mouseY, partialTicks);

    }

    @Override
    public void init() {

        Button btn = new Button(width / 2 - 75, height / 2 + 55, 150, 20, new TextComponent(I18n.get("gui.done")), button -> onClose());
        addRenderableWidget(btn);

    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}