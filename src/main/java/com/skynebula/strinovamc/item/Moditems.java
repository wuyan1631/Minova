package com.skynebula.strinovamc.item;

import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, StrinovaMc.MOD_ID);

    // 理想币
    public static final RegistryObject<Item> DREAM_TOKENS = ITEMS.register("dream_tokens",
            () -> new Item(new Item.Properties()));

    // 基弦
    public static final RegistryObject<Item> BASESTRINGS = ITEMS.register("basestrings",
            () -> new Item(new Item.Properties()));

    // 巴布洛晶体
    public static final RegistryObject<Item> BABLO_CRYSTALS = ITEMS.register("bablo_crystals",
            () -> new Item(new Item.Properties()));

    // KLBQ图标(创造模式物品栏图标)
    public static final RegistryObject<Item> KLBQLOGO = ITEMS.register("klbqlogo",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
