package com.skynebula.strinovamc.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.Minecraft;

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
    public static void onPlayerPreRender(RenderPlayerEvent.Pre event)
    {
        Player player = event.getEntity();

        // 检查玩家是否处于二维化状态
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified())
            {
                // 应用二维化渲染效果
                applyStringifiedRenderEffect(event, player);
            }
        });
    }

    /*
     * 应用二维化渲染效果
     * 像卡拉彼丘一样把模型压成纸片, 且纸片正面始终朝向相机
     * (先按相机与玩家朝向的差旋转, 再沿玩家朝向轴压扁, 抵消越肩相机的侧向偏移)
     */
    private static void applyStringifiedRenderEffect(RenderPlayerEvent.Pre event, Player player)
    {
        PoseStack poseStack = event.getPoseStack();

        // 根据玩家视角方向（yaw）决定扁平化方向
        String direction = getViewDirection(player);

        // 让纸片正面朝向相机: 旋转量 = 相机朝向 - 玩家朝向
        float cameraYaw = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(cameraYaw - player.getYRot()));

        // 根据视角方向应用不同的扁平化效果
        switch (direction) {
            case "north":
            case "south":
                // 看向南北时，沿Z轴扁平化
                poseStack.scale(1.0f, 1.0f, 0.1f);
                break;
            case "east":
            case "west":
                // 看向东西时，沿X轴扁平化
                poseStack.scale(0.1f, 1.0f, 1.0f);
                break;
            default:
                // 默认情况下，沿Z轴扁平化
                poseStack.scale(1.0f, 1.0f, 0.1f);
                break;
        }
    }

    /*
     * 获取玩家的视角方向（基于 yaw 角度）
     * @param player 玩家实体
     * @return 玩家的视角方向 ("north", "south", "east", "west")
     */
    private static String getViewDirection(Player player)
    {
        float yaw = player.getYRot() % 360;
        if (yaw < 0) yaw += 360;

        // 将 yaw 角度映射到四个方向
        // yaw: 0 = south, 90 = west, 180 = north, 270 = east
        if (yaw >= 45 && yaw < 135) {
            return "west";   // 实际面向西
        } else if (yaw >= 135 && yaw < 225) {
            return "north";  // 实际面向北
        } else if (yaw >= 225 && yaw < 315) {
            return "east";   // 实际面向东
        } else {
            return "south";  // 实际面向南
        }
    }
}
