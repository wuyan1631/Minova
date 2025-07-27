package com.skynebula.strinovamc.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * 二维化渲染处理器
 * 处理玩家在二维化状态下的渲染效果
 */
@Mod.EventBusSubscriber
public class StringifiedRenderHandler {

    // 添加朝向状态跟踪变量
    private static final Map<UUID, FacingDirection> lastDirectionMap = new HashMap<>();
    private static final Map<UUID, Float> transitionProgressMap = new HashMap<>();

    // 调整过渡速度（值越小过渡越慢）
    private static final float TRANSITION_SPEED = 0.05f;

    // 添加朝向枚举
    enum FacingDirection {
        FORWARD_BACK, LEFT_RIGHT
    }

    /*
     * 玩家渲染前事件处理
     * 在玩家渲染之前应用二维化效果
     */
    @SubscribeEvent
    public static void onPlayerPreRender(RenderPlayerEvent.Pre event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap -> {
            if (cap.isStringified()) {
                // 应用二维化渲染效果
                applyStringifiedRenderEffect(event, player);
            } else {
                // 如果玩家不在弦化状态，清理其数据防止内存泄漏
                cleanUpPlayerData(player.getUUID());
            }
        });
    }

    /*
     * 应用二维化渲染效果
     * 缩放玩家模型使其看起来扁平化
     */
    private static void applyStringifiedRenderEffect(RenderPlayerEvent.Pre event, Player player)
    {
        PoseStack poseStack = event.getPoseStack();

        // 获取玩家的朝向，决定扁平化的方向
        float yaw = player.getYRot();
        UUID playerId = player.getUUID();

        // 获取当前朝向状态
        FacingDirection currentDirection = isFacingForwardOrBack(yaw) ? FacingDirection.FORWARD_BACK : FacingDirection.LEFT_RIGHT;
        FacingDirection lastDirection = lastDirectionMap.getOrDefault(playerId, currentDirection);

        // 检查是否需要开始过渡
        if (lastDirection != currentDirection) {
            // 方向发生变化，开始过渡
            transitionProgressMap.putIfAbsent(playerId, 0f);
        }

        // 更新最后的朝向记录
        lastDirectionMap.put(playerId, currentDirection);

        // 检查是否正在进行过渡动画
        Float transitionProgress = transitionProgressMap.get(playerId);
        if (transitionProgress != null && transitionProgress < 1.0f) {
            // 正在过渡中，应用过渡效果
            applyTransitionEffect(poseStack, lastDirection, currentDirection, transitionProgress);

            // 更新过渡进度
            transitionProgressMap.put(playerId, Math.min(transitionProgress + 0.1f, 1.0f));
        } else {
            // 没有过渡动画，应用正常的扁平化效果
            if (currentDirection == FacingDirection.FORWARD_BACK) {
                // 面向前后时，沿Z轴扁平化
                poseStack.scale(1.0f, 1.0f, 0.1f);
            } else {
                // 面向左右时，沿X轴扁平化
                poseStack.scale(0.1f, 1.0f, 1.0f);
            }

            // 如果过渡已完成，移除过渡进度记录
            if (transitionProgress != null && transitionProgress >= 1.0f) {
                transitionProgressMap.remove(playerId);
            }
        }
    }

    /*
     * 应用过渡效果
     * 在玩家朝向改变时平滑地过渡扁平化方向
     */
    private static void applyTransitionEffect(PoseStack poseStack, FacingDirection from, FacingDirection to, float progress) {
        float scale_x, scale_y, scale_z;

        if (from == FacingDirection.FORWARD_BACK && to == FacingDirection.LEFT_RIGHT) {
            // 从前后向左右过渡
            scale_x = 1.0f - 0.9f * progress;
            scale_y = 1.0f;
            scale_z = 0.1f + 0.9f * progress;
        } else if (from == FacingDirection.LEFT_RIGHT && to == FacingDirection.FORWARD_BACK) {
            // 从左右向前后过渡
            scale_x = 0.1f + 0.9f * progress;
            scale_y = 1.0f;
            scale_z = 1.0f - 0.9f * progress;
        } else {
            // 默认情况（不应该发生）
            scale_x = 0.1f;
            scale_y = 1.0f;
            scale_z = 0.1f;
        }

        poseStack.scale(scale_x, scale_y, scale_z);
    }

    /*
     * 清理指定玩家的数据
     * 防止内存泄漏
     */
    private static void cleanUpPlayerData(UUID playerId) {
        lastDirectionMap.remove(playerId);
        transitionProgressMap.remove(playerId);
    }

    /*
     * 判断玩家是否面向前后方向
     * @param yaw 玩家的偏航角
     * @return 如果面向前后返回true，否则返回false
     */
    private static boolean isFacingForwardOrBack(float yaw)
    {
        // 将角度标准化到0-360范围内
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        // 面向前后：角度在45-135度和225-315度之间
        return (yaw >= 45 && yaw <= 135) || (yaw >= 225 && yaw <= 315);
    }
}
