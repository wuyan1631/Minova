package com.skynebula.strinovamc.block;

import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.item.Moditems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    // 创建方块的延迟注册器，用于在适当的时候注册方块
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, StrinovaMc.MOD_ID);

    // 注册 sidestep_block 方块，复制铁块的属性
    public static final RegistryObject<Block> BABLO_CRYSTALS_BLOCK_ORE = registryBlock("bablo_crystals_block_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 2.0F)
                    .sound(SoundType.STONE)
                    .lightLevel((state) -> 7)));

    public static final RegistryObject<Block> BABLO_CRYSTALS_BLOCK = registryBlock("bablo_crystals_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 2.0F)
                    .sound(SoundType.METAL)
                    .lightLevel((state) -> 0)));

    /*
     * 通用方块注册方法
     * @param name 方块名称
     * @param block 方块供应器
     * @return 注册的方块对象
     * @param <T> 方块类型
     */

    public static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block) {
        // 注册方块
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        // 同时注册方块对应的物品
        registryBlock(name, toReturn);
        return toReturn;
    }

    /*
     * 为方块注册对应的物品（BlockItem）
     * @param name 物品名称（通常与方块名称相同）
     * @param block 对应的方块注册对象
     * @return 注册的物品对象
     * @param <T> 方块类型
     */
    public static <T extends Block> RegistryObject<Item> registryBlock(String name, RegistryObject<T> block)
    {
        return Moditems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /*
     * 将方块注册器注册到事件总线
     * @param eventBus Forge事件总线
     */

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}