package com.skynebula.strinovamc.key;

import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.Perspective;
import com.mojang.logging.LogUtils;
import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.network.NetworkHandler;
import com.skynebula.strinovamc.network.StringifyStateC2SPacket;
import com.skynebula.strinovamc.network.StrinovaModeC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/*
 * 按键输入处理类
 * 处理客户端的按键输入事件, 监听并响应自定义键位绑定
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler
{
    private static final Logger LOGGER = LogUtils.getLogger();

    /*
     * 客户端tick事件处理方法
     * 在每个客户端tick周期末尾检查按键输入状态
     */
    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null)
        {
            Minecraft mc = Minecraft.getInstance();

            // Strinova模式总开关(L键)
            if (StrinovaKeybinds.STRINOVA_MODE_KEY.consumeClick())
            {
                handleStrinovaModeToggle();
            }

            // 弦化按键(V键)
            if (StrinovaKeybinds.STRING_TRANSFORMATION_KEY.consumeClick())
            {
                handleStringTransformation();
            }

            // 技能按键(C键)
            if (StrinovaKeybinds.SKILL_KEY.consumeClick())
            {
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.skill_activated"),
                    true
                );
                LOGGER.info("Skill key pressed");
            }

            // Strinova模式下强制锁定越肩视角(按F5也会被拉回)
            mc.player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
            {
                if (cap.isStrinovaMode() && Perspective.current() != Perspective.SHOULDER_SURFING)
                {
                    IShoulderSurfing.getInstance().changePerspective(Perspective.SHOULDER_SURFING);
                }
            });
        }
    }

    /*
     * 处理弦化功能
     * 切换玩家的弦化状态: 本地先乐观更新保证手感, 再通过数据包同步到服务器
     */
    private static void handleStringTransformation()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 有虚弱效果时不能弦化
        if (mc.player.hasEffect(MobEffects.WEAKNESS))
        {
            mc.player.displayClientMessage(
                Component.translatable("message.strinovamc.cannot_stringify",
                    Component.translatable(MobEffects.WEAKNESS.getDescriptionId())),
                true
            );
            return;
        }

        mc.player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            // Strinova模式前置: 未开启时不能进入弦化(已弦化时可以退出)
            if (!cap.isStrinovaMode() && !cap.isStringified())
            {
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.require_strinova_mode"),
                    true
                );
                return;
            }

            boolean newState = !cap.isStringified();
            cap.setStringified(newState);
            NetworkHandler.sendToServer(new StringifyStateC2SPacket(newState));

            if (newState)
            {
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.string_transformation_activated"),
                    true
                );
                triggerStringifiedEffects(mc.player);
            }
            else
            {
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.string_transformation_deactivated"),
                    true
                );
                // 解除弦化时保持当前相机视角(越肩), 不强制切回第一人称
            }
        });
    }

    /*
     * 处理Strinova模式总开关(L键)
     * 开启时锁定越肩视角并解锁全部模组内容; 关闭时相机保持现状
     */
    private static void handleStrinovaModeToggle()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            boolean newState = !cap.isStrinovaMode();
            cap.setStrinovaMode(newState);
            NetworkHandler.sendToServer(new StrinovaModeC2SPacket(newState));

            if (newState)
            {
                // 进入模式: 立即锁定越肩视角
                IShoulderSurfing.getInstance().changePerspective(Perspective.SHOULDER_SURFING);
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.mode_activated"),
                    true
                );
            }
            else
            {
                // 退出模式: 相机保持现状, 不再锁定
                mc.player.displayClientMessage(
                    Component.translatable("message.strinovamc.mode_deactivated"),
                    true
                );
            }
        });
    }

    /*
     * 触发弦化效果: 切换到越肩视角(Shoulder Surfing Reloaded)
     */
    private static void triggerStringifiedEffects(net.minecraft.world.entity.player.Player player)
    {
        IShoulderSurfing.getInstance().changePerspective(Perspective.SHOULDER_SURFING);
        LOGGER.info("Stringified effects applied to player");
    }
}
