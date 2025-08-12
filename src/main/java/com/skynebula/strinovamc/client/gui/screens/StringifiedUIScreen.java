package com.skynebula.strinovamc.client.gui.screens;

import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class StringifiedUIScreen extends Screen {

    private static final int GUI_WIDTH = 250;
    private static final int GUI_HEIGHT = 240;

    // 角色选项 - 只保留米雪儿
    private static final String[] CHARACTERS = {
        "Michelle"
    };

    // 角色阵营
    private static final String[] CHARACTER_FACTIONS = {
        "P.U.S"
    };

    private int selectedCharacter = 0;
    private Button selectButton;
    private boolean characterSelected = false;
    private int messageDisplayTime = 0;

    public StringifiedUIScreen() {
        super(Component.translatable("gui.strinovamc.character_panel_title"));
    }

    @Override
    protected void init() {
        super.init();

        // 计算GUI居中位置
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;

        // 添加选择角色按钮
        selectButton = this.addRenderableWidget(Button.builder(
            Component.translatable("gui.strinovamc.select_character"),
            button -> selectCharacter()
        ).pos(centerX + 75, centerY + 150).size(100, 20).build());

        // 添加一个关闭按钮
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.strinovamc.close"),
            button -> onClose()
        ).pos(centerX + 75, centerY + 180).size(100, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染半透明背景
        this.renderBackground(guiGraphics);

        // 计算GUI居中位置
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;

        // 绘制GUI背景
        guiGraphics.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0x80000000);

        // 绘制标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, centerY + 15, 0xFFFFFF);

        // 绘制当前选择的角色（会根据语言自动本地化）
        guiGraphics.drawCenteredString(
            this.font,
            Component.translatable("gui.strinovamc.selected_character",
                Component.translatable("gui.strinovamc.character_michelle")),
            this.width / 2,
            centerY + 40,
            0xFFFFFF
        );

        // 绘制角色阵营
        guiGraphics.drawCenteredString(
            this.font,
            Component.translatable("gui.strinovamc.character_faction", CHARACTER_FACTIONS[selectedCharacter]),
            this.width / 2,
            centerY + 55,
            0x55FF55
        );

        // 绘制角色预览区域标题
        guiGraphics.drawCenteredString(
            this.font,
            Component.translatable("gui.strinovamc.character_preview"),
            this.width / 2,
            centerY + 75,
            0xAAAAAA
        );

        // 绘制角色预览框
        guiGraphics.fill(centerX + 50, centerY + 95, centerX + 200, centerY + 140, 0x40000000);

        // 绘制角色名称（会根据语言自动本地化）
        guiGraphics.drawCenteredString(
            this.font,
            Component.translatable("gui.strinovamc.character_michelle"),
            this.width / 2,
            centerY + 115,
            0xFFFFFF
        );

        // 显示选择确认信息（如果角色已被选择）
        if (characterSelected && messageDisplayTime > 0) {
            guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("gui.strinovamc.character_selected",
                    Component.translatable("gui.strinovamc.character_michelle")),
                this.width / 2,
                centerY + 155,
                0x55FF55
            );
        }

        // 绘制UI操作提示
        guiGraphics.drawCenteredString(
            this.font,
            Component.translatable("gui.strinovamc.ui_hint"),
            this.width / 2,
            centerY + 215,
            0xFFFF55
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        // 设置为false，这样UI打开时游戏不会暂停
        return false;
    }

    /*
     * 选择角色
     */
    private void selectCharacter() {
        characterSelected = true;
        messageDisplayTime = 100; // 显示消息约5秒(在20 TPS下)
    }

    @Override
    public void tick() {
        super.tick();
        // 每tick减少显示时间
        if (messageDisplayTime > 0) {
            messageDisplayTime--;
        }
    }
}
