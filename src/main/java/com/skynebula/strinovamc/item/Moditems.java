package com.skynebula.strinovamc.item;

import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Moditems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, StrinovaMc.MOD_ID);

    public static final RegistryObject<Item> DREAM_TOKENS = ITEMS.register("dream_tokens",
            () -> new Item(new Item.Properties()));
    //注册理想币
    public static final RegistryObject<Item> BASESTRINGS = ITEMS.register("basestrings",
            () -> new Item(new Item.Properties()));
    //注册基弦

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
        // 可以在此处使用 BASESTRINGS，例如：
        // System.out.println("Registered item: " + BASESTRINGS.getId());
    }
}
