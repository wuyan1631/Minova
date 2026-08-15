package com.skynebula.strinovamc.interaction;

import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.item.ModItems;
import com.skynebula.strinovamc.network.NetworkHandler;
import com.skynebula.strinovamc.network.StringifyStateS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
 * 二维化交互处理器
 * 处理玩家在二维化状态下禁用所有交互行为
 */
@Mod.EventBusSubscriber
public class StringifiedInteractionHandler
{

    /*
     * 处理右键方块事件
     * 在二维化状态下阻止玩家与方块交互
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified())
            {
                // 取消交互事件
                event.setCanceled(true);
                // 向玩家显示提示信息（仅在客户端显示）
                if (!player.level().isClientSide())
                {
                    player.displayClientMessage(
                        Component.translatable("message.strinovamc.cannot_interact_while_stringified"),
                        true
                    );
                }
            }
        });
    }

    /*
     * 处理右键空气事件
     * 在二维化状态下阻止玩家使用物品
     */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (stack.getItem() == ModItems.BABLO_CRYSTALS.get() && stack.getCount() >= 64)
        {
            if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
                {
                    // Strinova模式前置: 未开启时不能激活超弦体
                    if (!cap.isStrinovaMode())
                    {
                        player.displayClientMessage(
                            Component.translatable("message.strinovamc.require_strinova_mode"),
                            true
                        );
                        return;
                    }

                    if (!cap.isSuperStringified())
                    {
                        // 只激活超弦体(卡丘身), 弦化状态由玩家手动V键进入/退出
                        cap.setSuperStringified(true);
                        stack.shrink(64);

                        // 将最新状态同步给客户端
                        NetworkHandler.sendToPlayer(
                            new StringifyStateS2CPacket(cap.isStrinovaMode(), cap.isStringified(), cap.isSuperStringified(), cap.getSelectedCharacter()),
                            serverPlayer
                        );

                        player.displayClientMessage(
                            Component.translatable("message.strinovamc.super_stringified_activated"),
                            true
                        );
                    }
                });
            }
            event.setCanceled(true);
            return;
        }

        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified())
            {
                event.setCanceled(true);
                if (!player.level().isClientSide())
                {
                    player.displayClientMessage(
                        Component.translatable("message.strinovamc.cannot_use_item_while_stringified"),
                        true
                    );
                }
            }
        });
    }

    /*
     * 处理左键方块事件
     * 在二维化状态下阻止玩家破坏方块
     */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified())
            {
                // 取消交互事件
                event.setCanceled(true);
                // 向玩家显示提示信息（仅在客户端显示）
                if (!player.level().isClientSide())
                {
                    player.displayClientMessage(
                        Component.translatable("message.strinovamc.cannot_break_block_while_stringified"),
                        true
                    );
                }
            }
        });
    }

    /*
     * 处理实体交互事件
     * 在二维化状态下阻止玩家与实体交互
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified()) {
                // 取消交互事件
                event.setCanceled(true);
                // 向玩家显示提示信息（仅在客户端显示）
                if (!player.level().isClientSide()) {
                    player.displayClientMessage(
                        Component.translatable("message.strinovamc.cannot_interact_entity_while_stringified"),
                        true
                    );
                }
            }
        });
    }

    /*
     * 处理实体攻击事件
     * 在二维化状态下阻止玩家攻击实体
     */
    @SubscribeEvent
    public static void onEntityAttack(PlayerInteractEvent.EntityInteractSpecific event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified()) {
                // 取消交互事件
                event.setCanceled(true);
                // 向玩家显示提示信息（仅在客户端显示）
                if (!player.level().isClientSide())
                {
                    player.displayClientMessage(
                        Component.translatable("message.strinovamc.cannot_attack_entity_while_stringified"),
                        true
                    );
                }
            }
        });
    }

    /*
     * 处理经验拾取事件
     * 在二维化状态下阻止玩家拾取经验
     */
    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap -> {
            if (cap.isStringified())
            {
                // 取消经验拾取事件
                event.setCanceled(true);
            }
        });
    }
}
