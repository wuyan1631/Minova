// C:\Modtext\stva\src\main\java\com\skynebula\strinovamc\render\StringifiedRenderHandler.java
package com.skynebula.strinovamc.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
 * 二维化渲染处理器
 * 处理玩家在二维化状态下的渲染效果
 */
@Mod.EventBusSubscriber
public class StringifiedRenderHandler {

    /*
     * 玩家渲染前事件处理
     * 在玩家渲染之前应用二维化效果
     */
    @SubscribeEvent
    public static void onPlayerPreRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap -> {
            if (cap.isStringified()) {
                // 应用二维化渲染效果
                applyStringifiedRenderEffect(event, player);
            }
        });
    }

    /*
     * 应用二维化渲染效果
     * 缩放玩家模型使其看起来扁平化
     */
    private static void applyStringifiedRenderEffect(RenderPlayerEvent.Pre event, Player player) {
        PoseStack poseStack = event.getPoseStack();

        // 获取玩家的朝向，决定扁平化的方向
        float yaw = player.getYRot();

        // 根据玩家朝向应用不同的扁平化效果
        if (isFacingForwardOrBack(yaw)) {
            // 面向前后时，沿Z轴扁平化
            poseStack.scale(1.0f, 1.0f, 0.1f);
        } else {
            // 面向左右时，沿X轴扁平化
            poseStack.scale(0.1f, 1.0f, 1.0f);
        }
    }

    /*
     * 判断玩家是否面向前后方向
     * @param yaw 玩家的偏航角
     * @return 如果面向前后返回true，否则返回false
     */
    private static boolean isFacingForwardOrBack(float yaw) {
        // 将角度标准化到0-360范围内
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        // 面向前后：角度在45-135度和225-315度之间
        return (yaw >= 45 && yaw <= 135) || (yaw >= 225 && yaw <= 315);
    }
}
