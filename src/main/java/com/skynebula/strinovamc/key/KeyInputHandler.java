// 更新 C:\Modtext\stva\src\main\java\com\skynebula\strinovamc\key\KeyInputHandler.java
package com.skynebula.strinovamc.key;

import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 按键输入处理类
 * 用于处理客户端的按键输入事件，监听并响应自定义键位绑定
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {
    /**
     * 客户端tick事件处理方法
     * 在每个客户端tick周期末尾检查按键输入状态，如果检测到特定按键被按下则执行相应逻辑
     *
     * @param event 客户端tick事件对象，包含当前tick阶段等信息
     */
    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        // 只在tick阶段为END时处理，并确保玩家对象不为空
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            // 检查弦化按键是否被按下
            if (StrinovamcKeybinds.STRING_TRANSFORMATION_KEY.consumeClick()) {
                // 处理弦化按键逻辑
                handleStringTransformation();
            }

            // 检查技能按键是否被按下
            if (StrinovamcKeybinds.SKILL_KEY.consumeClick()) {
                // 处理技能按键逻辑
                Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable("message.strinovamc.skill_activated"),
                    true
                );

                // 在这里添加技能的实际功能实现
                System.out.println("Skill Key Pressed!");
            }
        }
    }

    /**
     * 处理弦化功能
     * 切换玩家的二维化状态
     */
    private static void handleStringTransformation() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 检查是否有不能弦化的状态或效果
        if (mc.player.hasEffect(MobEffects.WEAKNESS)) {
            // 如果有虚弱效果，则不能弦化
            mc.player.displayClientMessage(
                Component.translatable("message.strinovamc.cannot_stringify", "虚弱效果"),
                true
            );
            return;
        }

        // 检查其他限制条件
        // if (playerHasBuffXXX()) {
        //     mc.player.displayClientMessage(
        //         Component.translatable("message.strinovamc.cannot_stringify", "XXX状态"),
        //         true
        //     );
        //     return;
        // }

        // 切换二维化状态
        mc.player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap -> {
            boolean newState = !cap.isStringified();
            cap.setStringified(newState);

            if (newState) {
                // 进入二维化状态
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.string_transformation_activated"),
                    true
                );
                System.out.println("Player stringified!");

                // 触发二维化效果
                triggerStringifiedEffects(mc.player);
            } else {
                // 退出二维化状态
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.string_transformation_deactivated"),
                    true
                );
                System.out.println("Player de-stringified!");

                // 恢复正常状态
                restoreNormalEffects(mc.player);
            }
        });
    }

    /**
     * 触发二维化效果
     * @param player 目标玩家
     */
    private static void triggerStringifiedEffects(net.minecraft.world.entity.player.Player player) {
        // 二维化效果已经在StringifiedPhysicsHandler和StringifiedRenderHandler中处理
        // 这里可以添加粒子效果、声音等额外效果
        System.out.println("Stringified effects applied to player");
    }

    /**
     * 恢复正常效果
     * @param player 目标玩家
     */
    private static void restoreNormalEffects(net.minecraft.world.entity.player.Player player) {
        // 恢复正常效果已经在StringifiedPhysicsHandler中处理
        // 这里可以添加恢复效果、声音等额外效果
        System.out.println("Normal effects restored to player");
    }
}
