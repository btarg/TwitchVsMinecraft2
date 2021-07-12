package io.github.icrazyblaze.twitchmod.util;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class EffectInstanceHelper {
    public static EffectInstance effect(Effect type, int duration, int amplifier) {
        return new EffectInstance(type, duration, amplifier);
    }
}
