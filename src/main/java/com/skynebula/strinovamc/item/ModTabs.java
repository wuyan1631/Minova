package com.skynebula.strinovamc.item;

import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.rmi.registry.Registry;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StrinovaMc.MOD_ID);

    public static final RegistryObject<CreativeModeTab> STRINOVAMC_TAB = CREATIVE_MODE_TABS.register("strinovamc_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Moditems.BABLO_CRYSTALS.get()))
                    .title(Component.translatable("Creativetab.strinovamc_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(new ItemStack(Moditems.BASESTRINGS.get()));
                        //将基弦添加到创造模式物品栏
                        output.accept(new ItemStack(Moditems.DREAM_TOKENS.get()));
                        //将理想币添加到创造模式物品栏
                        output.accept(new ItemStack(Moditems.BABLO_CRYSTALS.get()));


                    })
                    .build());


    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
