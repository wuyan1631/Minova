// C:\Modtext\stva\src\main\java\com\skynebula\strinovamc\capability\StringStateProvider.java
package com.skynebula.strinovamc.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * 字符串状态提供者类，实现ICapabilityProvider和INBTSerializable接口
 * 用于提供和管理字符串状态能力的序列化和反序列化功能
 */
public class StringStateProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private StringStateCapability stringStateCapability = null;
    private final LazyOptional<StringStateCapability> opt = LazyOptional.of(this::createCapability);

    /*
     * 创建字符串状态能力实例
     * 如果stringStateCapability为null，则创建新的StringStateCapability实例
     * @return 字符串状态能力实例
     */
    private StringStateCapability createCapability() {
        if (stringStateCapability == null) {
            stringStateCapability = new StringStateCapability();
        }
        return stringStateCapability;
    }

    /*
     * 获取指定能力的LazyOptional包装
     * @param cap 能力类型
     * @param side 方向参数，可为null
     * @return 如果请求的能力是StringStateCapability，则返回该能力的LazyOptional包装，否则返回空的LazyOptional
     */
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 检查请求的能力是否为StringStateCapability
        if (cap == StringStateCapability.INSTANCE) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    /*
     * 将能力状态序列化为NBT标签
     * @return 包含序列化数据的CompoundTag
     */
    @Override
    public CompoundTag serializeNBT() {
        return createCapability().serializeNBT();
    }

    /*
     * 从NBT标签反序列化能力状态
     * @param nbt 包含序列化数据的CompoundTag
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // 反序列化字符串状态能力
        createCapability().deserializeNBT(nbt);
    }
}

