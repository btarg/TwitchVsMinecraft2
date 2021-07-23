package io.github.icrazyblaze.twitchmod.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectInstanceHelper {
    public static MobEffectInstance effect(MobEffect type, int duration, int amplifier) {
        return new MobEffectInstance(type, duration, amplifier);
    }
}
