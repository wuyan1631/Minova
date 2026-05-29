package com.skynebula.strinovamc.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class KaQiuBodyEffect extends MobEffect
{
    public KaQiuBodyEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier)
    {
        return true;
    }
}
