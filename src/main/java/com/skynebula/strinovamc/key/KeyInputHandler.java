// 更新 C:\Modtext\stva\src\main\java\com\skynebula\strinovamc\key\KeyInputHandler.java
package com.skynebula.strinovamc.key;

import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
 * 按键输入处理类
 * 用于处理客户端的按键输入事件，监听并响应自定义键位绑定
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler
{
    /*
     * 客户端tick事件处理方法
     * 在每个客户端tick周期末尾检查按键输入状态，如果检测到特定按键被按下则执行相应逻辑
     *
     * @param event 客户端tick事件对象，包含当前tick阶段等信息
     */
    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && Minecraft.getInstance().player != null) {
            var player = Minecraft.getInstance().player;

            if (StrinovamcKeybinds.STRING_TRANSFORMATION_KEY.consumeClick())
            {
                handleStringTransformation();
            }

            if (StrinovamcKeybinds.SKILL_KEY.consumeClick())
            {
                player.displayClientMessage(
                    Component.translatable("message.strinovamc.skill_activated"),
                    true
                );
                System.out.println("Skill Key Pressed!");
            }


        }
    }

    /*
     * 处理弦化功能
     * 切换玩家的二维化状态
     */
    private static void handleStringTransformation()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 检查是否有不能弦化的状态或效果
        if (mc.player.hasEffect(MobEffects.WEAKNESS))
        {
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
        mc.player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            boolean newState = !cap.isStringified();
            cap.setStringified(newState);

            if (newState)
            {
                triggerStringifiedEffects(mc.player);
            }
            else
            {
                cap.setSuperStringified(false);
                restoreNormalEffects(mc.player);
            }
        });
    }

    /*
     * 触发二维化效果
     * @param player 目标玩家
     */
    private static void triggerStringifiedEffects(net.minecraft.world.entity.player.Player player)
    {
        Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
        System.out.println("Stringified effects applied to player");
    }

    private static void restoreNormalEffects(net.minecraft.world.entity.player.Player player)
    {
        Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
        System.out.println("Normal effects restored to player");
    }
}
