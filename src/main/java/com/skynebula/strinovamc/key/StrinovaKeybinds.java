package com.skynebula.strinovamc.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.skynebula.strinovamc.StrinovaMc;
import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.client.gui.screens.StringifiedUIScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

/*
 * Strinovamc模组的按键绑定管理类
 * 负责注册和管理模组中使用的自定义按键绑定
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrinovaKeybinds
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String KEY_CATEGORY_STRINOVA = "key.category.strinovamc.strinova";
    public static final String KEY_STRINOVA_MODE = "key.strinovamc.strinova_mode";
    public static final String KEY_STRING_TRANSFORMATION = "key.strinovamc.string_transformation";
    public static final String KEY_SKILL = "key.strinovamc.skill";
    public static final String KEY_OPEN_UI = "key.strinovamc.open_ui";

    // L键: Strinova模式总开关(未开启时模组内容不可用, 开启时锁定越肩视角)
    public static final KeyMapping STRINOVA_MODE_KEY = new KeyMapping(
            KEY_STRINOVA_MODE,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            KEY_CATEGORY_STRINOVA
    );

    // V键: 弦化
    public static final KeyMapping STRING_TRANSFORMATION_KEY = new KeyMapping(
            KEY_STRING_TRANSFORMATION,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            KEY_CATEGORY_STRINOVA
    );

    // C键: 技能
    public static final KeyMapping SKILL_KEY = new KeyMapping(
            KEY_SKILL,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_CATEGORY_STRINOVA
    );

    // K键: 打开角色面板
    public static final KeyMapping OPEN_UI_KEY = new KeyMapping(
            KEY_OPEN_UI,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            KEY_CATEGORY_STRINOVA
    );

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event)
    {
        event.register(STRINOVA_MODE_KEY);
        event.register(STRING_TRANSFORMATION_KEY);
        event.register(SKILL_KEY);
        event.register(OPEN_UI_KEY);
        LOGGER.info("Registered strinova mode, string transformation, skill and open UI keys");
    }

    @Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID, value = Dist.CLIENT)
    public static class OpenUIKeyHandler
    {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event)
        {
            if (OPEN_UI_KEY.consumeClick())
            {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return;

                // 角色面板也需要Strinova模式前置
                mc.player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
                {
                    if (cap.isStrinovaMode())
                    {
                        mc.setScreen(new StringifiedUIScreen());
                    }
                    else
                    {
                        mc.player.displayClientMessage(
                            Component.translatable("message.strinovamc.require_strinova_mode"),
                            true
                        );
                    }
                });
            }
        }
    }
}
