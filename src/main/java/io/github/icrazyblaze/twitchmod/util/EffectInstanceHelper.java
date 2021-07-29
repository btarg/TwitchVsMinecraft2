package io.github.icrazyblaze.twitchmod.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.ThreadLocalRandom;

public class EffectInstanceHelper {

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * This method generates a random potion effect with a randomised duration and amplifier.
     *
     * @return The random effect
     */
    public static MobEffectInstance getRandomEffect() {

        int length = ForgeRegistries.POTIONS.getKeys().toArray().length;
        int r = rand.nextInt(length);

        MobEffect effect = MobEffect.byId(r);

        if (effect != null) {
            return new MobEffectInstance(effect, rand.nextInt(100, 1200), rand.nextInt(1, 5));
        }
        return new MobEffectInstance(MobEffects.REGENERATION, 100, 0);

    }

    /**
     * @return a potion effect
     */
    public static MobEffectInstance effect(MobEffect type, int duration, int amplifier) {
        return new MobEffectInstance(type, duration, amplifier);
    }
}
