package com.skynebula.strinovamc.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/*
 * 字符串状态能力类
 * 管理玩家的Strinova模式总开关、弦化(二维化)状态、超弦体状态与已选角色
 */
public class StringStateCapability
{
    // 能力注册
    public static final Capability<StringStateCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    // 是否开启Strinova模式(L键总开关, 未开启时模组内容不可用)
    private boolean isStrinovaMode = false;
    // 是否处于弦化(二维化)状态
    private boolean isStringified = false;
    // 是否处于超弦体状态
    private boolean isSuperStringified = false;
    // 角色面板中已选择的角色索引(默认0, 随玩家NBT持久化)
    private int selectedCharacter = 0;

    public boolean isStrinovaMode() {
        return isStrinovaMode;
    }

    public void setStrinovaMode(boolean strinovaMode) {
        this.isStrinovaMode = strinovaMode;
    }

    public boolean isStringified() {
        return isStringified;
    }

    public void setStringified(boolean stringified) {
        this.isStringified = stringified;
    }

    public boolean isSuperStringified() {
        return isSuperStringified;
    }

    public void setSuperStringified(boolean superStringified) {
        this.isSuperStringified = superStringified;
    }

    public int getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(int selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
    }

    // 序列化到NBT
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        // 弦化状态不持久化: 退出游戏/重新进入世界后重置, 需玩家手动重新进入;
        // 超弦体(卡丘身)与已选角色随玩家数据保留
        nbt.putBoolean("IsSuperStringified", isSuperStringified);
        nbt.putInt("SelectedCharacter", selectedCharacter);
        return nbt;
    }

    // 从NBT反序列化
    public void deserializeNBT(CompoundTag nbt)
    {
        // 弦化状态不读取, 新会话默认false
        isSuperStringified = nbt.getBoolean("IsSuperStringified");
        selectedCharacter = nbt.getInt("SelectedCharacter");
    }
}
