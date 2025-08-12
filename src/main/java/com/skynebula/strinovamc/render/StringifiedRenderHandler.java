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

    // 存储每个玩家最后的移动方向
    private static final java.util.Map<Player, String> lastDirectionMap = new java.util.HashMap<>();

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
     * 缩放玩家模型使其看起来扁平化
     */
    private static void applyStringifiedRenderEffect(RenderPlayerEvent.Pre event, Player player)
    {
        PoseStack poseStack = event.getPoseStack();

        // 获取玩家的移动方向，决定扁平化的方向
        String direction = getPlayerMovementDirection(player);

        // 如果有移动输入，更新最后的方向
        if (!"none".equals(direction)) {
            lastDirectionMap.put(player, direction);
        }
        // 如果没有移动输入，使用最后记录的方向
        else if (lastDirectionMap.containsKey(player)) {
            direction = lastDirectionMap.get(player);
        }
        // 如果是第一次且没有移动输入，使用默认方向
        else {
            direction = "north"; // 默认方向
        }

        // 根据玩家移动方向应用不同的扁平化效果
        switch (direction) {
            case "north":
            case "south":
                // 面向南北时，沿Z轴扁平化
                poseStack.scale(1.0f, 1.0f, 0.1f);
                break;
            case "east":
            case "west":
                // 面向东西时，沿X轴扁平化
                poseStack.scale(0.1f, 1.0f, 1.0f);
                break;
            default:
                // 默认情况下，沿Z轴扁平化
                poseStack.scale(1.0f, 1.0f, 0.1f);
                break;
        }
    }

    /*
     * 获取玩家的移动方向
     * @param player 玩家实体
     * @return 玩家的主要移动方向 ("north", "south", "east", "west", "none")
     */
    private static String getPlayerMovementDirection(Player player)
    {
        double forward = player.zza; // 前后移动 (W/S)
        double strafe = player.xxa;  // 左右移动 (A/D)

        // 如果没有移动输入，则返回none
        if (Math.abs(forward) < 0.1 && Math.abs(strafe) < 0.1) {
            return "none";
        }

        // 确定主要移动方向
        if (Math.abs(forward) > Math.abs(strafe)) {
            // 主要为前后移动
            return forward > 0 ? "south" : "north";
        } else {
            // 主要为左右移动
            return strafe > 0 ? "east" : "west";
        }
    }
}
