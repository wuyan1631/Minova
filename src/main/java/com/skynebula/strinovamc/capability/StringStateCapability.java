package com.skynebula.strinovamc.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/*
 * 字符串状态能力类
 * 用于管理玩家的二维化状态
 */
public class StringStateCapability
{
    // 能力注册
    public static final Capability<StringStateCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    // 是否处于二维化状态
    private boolean isStringified = false;
    // 是否处于超弦体状态
    private boolean isSuperStringified = false;

    // 获取二维化状态
    public boolean isStringified() {
        return isStringified;
    }

    // 设置二维化状态
    public void setStringified(boolean stringified) {
        this.isStringified = stringified;
    }

    // 获取超弦体状态
    public boolean isSuperStringified() {
        return isSuperStringified;
    }

    // 设置超弦体状态
    public void setSuperStringified(boolean superStringified) {
        this.isSuperStringified = superStringified;
    }

    // 序列化到NBT
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("IsStringified", isStringified);
        nbt.putBoolean("IsSuperStringified", isSuperStringified);
        return nbt;
    }

    // 从NBT反序列化
    public void deserializeNBT(CompoundTag nbt)
    {
        isStringified = nbt.getBoolean("IsStringified");
        isSuperStringified = nbt.getBoolean("IsSuperStringified");
    }
}
