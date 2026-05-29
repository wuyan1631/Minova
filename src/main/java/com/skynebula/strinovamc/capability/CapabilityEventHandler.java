package com.skynebula.strinovamc.capability;

import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
 * Capability事件处理器类
 * 处理实体能力的附加和玩家克隆事件
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID)
public class CapabilityEventHandler
{

    /*
     * 当实体附加能力时触发的事件处理方法
     * 为玩家实体附加字符串状态能力
     *
     * @param event 实体附加能力事件，包含要附加能力的实体对象
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        // 检查实体是否为玩家，如果是则附加字符串状态能力
        if (event.getObject() instanceof Player) {
            event.addCapability(
                ResourceLocation.fromNamespaceAndPath(StrinovaMc.MOD_ID, "string_state"),
                new StringStateProvider()
            );
        }
    }

    /*
     * 当玩家克隆时触发的事件处理方法
     * 在玩家死亡后保留字符串状态能力的数据
     *
     * @param event 玩家克隆事件，包含原始玩家和新玩家的引用
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        // 只在玩家死亡时处理能力数据的转移
        if (event.isWasDeath())
        {
            // 将原始玩家的字符串状态能力数据复制到新玩家
                event.getOriginal().getCapability(StringStateCapability.INSTANCE).ifPresent(oldCap -> {
                event.getEntity().getCapability(StringStateCapability.INSTANCE).ifPresent(newCap -> {
                    newCap.setStringified(oldCap.isStringified());
                    newCap.setSuperStringified(oldCap.isSuperStringified());
                });
            });
        }
    }
}

