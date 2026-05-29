package com.skynebula.strinovamc.effect;

import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, StrinovaMc.MOD_ID);

    public static final RegistryObject<MobEffect> KAQIU_BODY = EFFECTS.register("kaqiu_body",
            () -> new KaQiuBodyEffect(net.minecraft.world.effect.MobEffectCategory.BENEFICIAL, 0x3366CC));

    public static void register(IEventBus eventBus)
    {
        EFFECTS.register(eventBus);
    }
}
