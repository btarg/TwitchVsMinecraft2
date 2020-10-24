/**
 * This code was written by Pablo Perez Rodriguez, modified to work with Forge.
 * <a href="https://github.com/PabloPerezRodriguez/twitch-chat/blob/master/src/main/java/to/pabli/twitchchat/twitch_integration/CalculateMinecraftColor.java">Original code</a>
 */

package com.icrazyblaze.twitchmod.util;

import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CalculateMinecraftColor {
    public static final TextFormatting[] MINECRAFT_COLORS = Arrays.stream(TextFormatting.values()).filter(TextFormatting::isColor).toArray(TextFormatting[]::new);
    // Code from here https://discuss.dev.twitch.tv/t/default-user-color-in-chat/385/2 but a little bit adjusted.
    public static Map<String, TextFormatting> cachedNames = new HashMap<>();

    /**
     * Takes a Color object and returns the closest Minecraft colour code.
     * @See https://minecraft.gamepedia.com/Formatting_codes#Color_codes
     * @param color
     * @return
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

    public static TextFormatting getDefaultUserColor(String username) {
        if (cachedNames.containsKey(username)) {
            return cachedNames.get(username);
        } else {
            // If we don't have the color cached, calculate it.
            char firstChar = username.charAt(0);
            char lastChar = username.charAt(username.length() - 1);

            int n = ((int) firstChar) + ((int) lastChar);
            return MINECRAFT_COLORS[n % MINECRAFT_COLORS.length];
        }
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