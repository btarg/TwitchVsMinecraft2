package io.github.icrazyblaze.twitchmod.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.icrazyblaze.twitchmod.Main;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public class MessageboxScreen extends Screen {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/messagebox_background.png");
    public static String message = null;

    public MessageboxScreen(String message) {
        super(new StringTextComponent("Message Box"));
        MessageboxScreen.message = message;
    }

    @Override
    public void render(@NotNull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {

        renderBackground(stack);

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        assert minecraft != null;
        minecraft.getTextureManager().bind(BG_TEXTURE);

        // Show the background
        blit(stack, (width / 2) - 87, (height / 2) - 83, 0, 0, 256, 256);


        // Draw title
        minecraft.font.draw(stack, "Message Box", width / 2f - 32, height / 2f - 78, 4210752);

        // Draw wrapped text
        List<IReorderingProcessor> text = minecraft.font.split(new StringTextComponent(message), 165);
        for (int i = 0; i < text.size(); i++) {
            minecraft.font.draw(stack, text.get(i), (width / 2f) - (minecraft.font.width(text.get(i)) / 2f), (height / 2f - 60) + (minecraft.font.lineHeight * i), 4210752);
        }

        super.render(stack, mouseX, mouseY, partialTicks);

    }

    @Override
    public void init() {

        Button btn = new Button(width / 2 - 75, height / 2 + 55, 150, 20, new StringTextComponent(I18n.get("gui.done")), button -> minecraft.player.closeContainer());
        addButton(btn);

    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

}