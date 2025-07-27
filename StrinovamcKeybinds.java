// C:\Modtext\stva\src\main\java\com\skynebula\strinovamc\capability\StringStateCapability.java
package com.skynebula.strinovamc.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class StringStateCapability {
    // 能力注册
    public static final Capability<StringStateCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    
    // 是否处于二维化状态
    private boolean isStringified = false;
    
    // 获取二维化状态
    public boolean isStringified() {
        return isStringified;
    }
    
    // 设置二维化状态
    public void setStringified(boolean stringified) {
        this.isStringified = stringified;
    }
    
    // 序列化到NBT
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("IsStringified", isStringified);
        return nbt;
    }
    
    // 从NBT反序列化
    public void deserializeNBT(CompoundTag nbt) {
        isStringified = nbt.getBoolean("IsStringified");
    }
}
