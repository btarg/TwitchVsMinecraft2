/**
 * This code was written by Pablo Perez Rodriguez, modified to work with Forge.
 * <a href="https://github.com/PabloPerezRodriguez/twitch-chat/blob/master/src/main/java/to/pabli/twitchchat/twitch_integration/CalculateMinecraftColor.java">Original code</a>
 */

package io.github.icrazyblaze.twitchmod.util;

import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public class CalculateMinecraftColor {

    /**
     * Takes a Color object and returns the closest Minecraft colour code.
     *
     * @param color
     * @return
     * @See https://minecraft.gamepedia.com/Formatting_codes#Color_codes
     */
    public static TextFormatting findNearestMinecraftColor(Color color) {
        return Arrays.stream(TextFormatting.values())
                .filter(TextFormatting::isColor)
                .map(formatting -> {
                    Color formattingColor = new Color(formatting.getColor());

                    int distance = Math.abs(color.getRed() - formattingColor.getRed()) +
                            Math.abs(color.getGreen() - formattingColor.getGreen()) +
                            Math.abs(color.getBlue() - formattingColor.getBlue());
                    return new FormattingAndDistance(formatting, distance);
                })
                .sorted(Comparator.comparing(FormattingAndDistance::getDistance))
                .map(FormattingAndDistance::getFormatting)
                .findFirst()
                .orElse(TextFormatting.WHITE);
    }


    private static class FormattingAndDistance {
        private final TextFormatting formatting;
        private final int distance;

        public FormattingAndDistance(TextFormatting formatting, int distance) {
            this.formatting = formatting;
            this.distance = distance;
        }

        public TextFormatting getFormatting() {
            return formatting;
        }

        public int getDistance() {
            return distance;
        }
    }
}