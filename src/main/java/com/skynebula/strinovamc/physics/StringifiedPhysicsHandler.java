// C:\Modtext\stva\src\main\java\com\skynebula\strinovamc\physics\StringifiedPhysicsHandler.java
package com.skynebula.strinovamc.physics;

import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
 * 二维化物理处理器
 * 处理玩家在二维化状态下的物理特性，如碰撞体积调整
 */
@Mod.EventBusSubscriber
public class StringifiedPhysicsHandler {

    /*
     * 玩家加入世界事件处理
     * 确保玩家的碰撞体积正确设置
     */
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 检查玩家是否处于二维化状态
            player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap -> {
                if (cap.isStringified()) {
                    // 应用二维化物理效果
                    applyStringifiedPhysics(player);
                }
            });
        }
    }

    /*
     * 玩家tick事件处理
     * 每个tick检查并更新玩家的物理状态
     */
    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player && event.getEntity().level().isClientSide() == false) {
            if (event.getEntity().tickCount % 5 == 0) { // 每5个tick更新一次以提高性能
                // 检查玩家是否处于二维化状态
                player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap -> {
                    if (cap.isStringified()) {
                        // 应用二维化物理效果
                        applyStringifiedPhysics(player);
                    } else {
                        // 恢复正常的碰撞体积
                        restoreNormalPhysics(player);
                    }
                });
            }
        }
    }

    /*
     * 应用二维化物理效果
     * 调整玩家的碰撞体积使其扁平化
     */
    private static void applyStringifiedPhysics(Player player) {
        // 获取玩家当前的朝向
        float yaw = player.getYRot();

        // 根据朝向调整碰撞体积
        if (isFacingForwardOrBack(yaw)) {
            // 面向前后时，沿Z轴扁平化
            player.setBoundingBox(player.getBoundingBox().inflate(
                0, 0, -0.4 // 减少Z轴的一半尺寸
            ));
        } else {
            // 面向左右时，沿X轴扁平化
            player.setBoundingBox(player.getBoundingBox().inflate(
                -0.4, 0, 0 // 减少X轴的一半尺寸
            ));
        }
    }

    /*
     * 恢复正常的物理效果
     * 将玩家的碰撞体积恢复到正常状态
     */
    private static void restoreNormalPhysics(Player player) {
        // 注意：这里需要保存原始的碰撞体积或者重新计算
        // 简单实现：重新设置为默认的玩家碰撞体积
        player.refreshDimensions();
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
