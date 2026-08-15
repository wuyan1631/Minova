package com.skynebula.strinovamc.capability;

import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.network.NetworkHandler;
import com.skynebula.strinovamc.network.StringifyStateS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
 * Capability事件处理器类
 * 处理实体能力的附加、玩家克隆和加入世界时的状态同步
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID)
public class CapabilityEventHandler
{

    /*
     * 当实体附加能力时触发的事件处理方法
     * 为玩家实体附加字符串状态能力
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            event.addCapability(
                ResourceLocation.fromNamespaceAndPath(StrinovaMc.MOD_ID, "string_state"),
                new StringStateProvider()
            );
        }
    }

    /*
     * 当玩家克隆时触发的事件处理方法
     * 死亡后弦化状态重置(需玩家手动重新进入), Strinova模式/超弦体/已选角色数据保留
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        if (event.isWasDeath())
        {
            event.getOriginal().getCapability(StringStateCapability.INSTANCE).ifPresent(oldCap -> {
                event.getEntity().getCapability(StringStateCapability.INSTANCE).ifPresent(newCap -> {
                    // 弦化状态不复制, 新玩家的Capability默认false即重置
                    newCap.setStrinovaMode(oldCap.isStrinovaMode());
                    newCap.setSuperStringified(oldCap.isSuperStringified());
                    newCap.setSelectedCharacter(oldCap.getSelectedCharacter());
                });
            });
        }
    }

    /*
     * 玩家加入世界时, 将服务器端保存的状态同步给客户端
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
                NetworkHandler.sendToPlayer(new StringifyStateS2CPacket(
                        cap.isStrinovaMode(), cap.isStringified(), cap.isSuperStringified(), cap.getSelectedCharacter()), serverPlayer)
            );
        }
    }
}
