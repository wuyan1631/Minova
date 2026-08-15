package com.skynebula.strinovamc.item;

import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StrinovaMc.MOD_ID);

    public static final RegistryObject<CreativeModeTab> STRINOVAMC_TAB = CREATIVE_MODE_TABS.register("strinovamc_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.KLBQLOGO.get()))
                    .title(Component.translatable("Creativetab.strinovamc_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(new ItemStack(ModItems.BASESTRINGS.get()));
                        //将基弦添加到创造模式物品栏
                        output.accept(new ItemStack(ModItems.DREAM_TOKENS.get()));
                        //将理想币添加到创造模式物品栏
                        output.accept(new ItemStack(ModItems.BABLO_CRYSTALS.get()));
                        //将巴布洛晶核添加到创造模式物品栏

                        output.accept(new ItemStack(ModBlocks.BABLO_CRYSTALS_BLOCK_ORE.get()));
                        //将巴布洛晶体矿石矿添加到创造模式物品栏
                        output.accept(new ItemStack(ModBlocks.BABLO_CRYSTALS_BLOCK.get()));



                    })
                    .build());


    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
