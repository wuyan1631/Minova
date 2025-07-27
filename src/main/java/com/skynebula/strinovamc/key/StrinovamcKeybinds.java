package com.skynebula.strinovamc.key;

import com.mojang.blaze3d.platform.InputConstants;
import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/*
 * Strinovamc模组的按键绑定管理类
 * 负责注册和管理模组中使用的自定义按键绑定
 */
@Mod.EventBusSubscriber(modid = StrinovaMc.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StrinovamcKeybinds
{
    public static final String KEY_CATEGORY_STRINOVA = "key.category.strinovamc.strinova";
    public static final String KEY_STRING_TRANSFORMATION = "key.strinovamc.string_transformation";
    public static final String KEY_SKILL = "key.strinovamc.skill";

    /*
     * 字符串转换功能的按键映射
     * 默认绑定到V键，用于在游戏中触发字符串转换操作
     */
    public static final KeyMapping STRING_TRANSFORMATION_KEY = new KeyMapping(
            KEY_STRING_TRANSFORMATION,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V, // 默认按键为V键
            KEY_CATEGORY_STRINOVA
    );

    /*
     * 技能功能的按键映射
     * 默认绑定到C键，用于在游戏中触发技能操作
     */
    public static final KeyMapping SKILL_KEY = new KeyMapping(
            KEY_SKILL,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C, // 默认按键为C键
            KEY_CATEGORY_STRINOVA
    );

    /*
     * 注册按键绑定的事件处理方法
     * 当Forge注册按键映射事件触发时，将自定义按键绑定注册到游戏中
     *
     * @param event 按键映射注册事件对象，用于注册自定义按键
     */
    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        // 注册字符串转换按键绑定
        event.register(STRING_TRANSFORMATION_KEY);
        // 注册技能按键绑定
        event.register(SKILL_KEY);
        System.out.println("Registered String Transformation Key and Skill Key");
    }
}
