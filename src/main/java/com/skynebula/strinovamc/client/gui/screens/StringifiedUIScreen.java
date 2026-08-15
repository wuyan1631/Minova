package com.skynebula.strinovamc.client.gui.screens;

import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.network.NetworkHandler;
import com.skynebula.strinovamc.network.SelectCharacterC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class StringifiedUIScreen extends Screen {

    private static final int GUI_WIDTH = 250;
    private static final int GUI_HEIGHT = 240;
    private static final int FULLSCREEN_GUI_WIDTH = 300;
    private static final int FULLSCREEN_GUI_HEIGHT = 200;
    private static final int CHARACTERS_PER_PAGE = 6;

    // 角色选项 - 将所有P.U.S角色放在前面，SCISSORS角色放在后面
    private static final String[] CHARACTERS = {
        "Michelle",
        "Shin",
        "Kokona",
        "Yvette",
        "Flavia",
        "Yu Wu",
        "Leona",
        "Chiyoko",
        "Ming",
        "Lawi",
        "Meredith",
        "Shinomi",
        "Ling",
        "Aika",
        "Fragrance",
        "Mara"
    };

    // 角色阵营 - 与角色顺序对应
    private static final String[] CHARACTER_FACTIONS = {
        "P.U.S",
        "P.U.S",
        "P.U.S",
        "P.U.S",
        "P.U.S",
        "P.U.S",
        "P.U.S",
        "P.U.S",
        "SCISSORS",
        "SCISSORS",
        "SCISSORS",
        "SCISSORS",
        "SCISSORS",
        "SCISSORS",
        "SCISSORS",
        "SCISSORS"
    };

    private int selectedCharacter = 0;
    private Button selectButton;
    private Button closeButton;
    private Button characterSelectButton;
    private boolean characterSelected = false;
    private int messageDisplayTime = 0;
    private boolean showCharacterSelection = false;
    private int currentPage = 0;

    public StringifiedUIScreen() {
        super(Component.translatable("gui.strinovamc.character_panel_title"));

        // 读取服务器保存的已选角色(加入世界时通过数据包同步到本地Capability)
        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
                this.selectedCharacter = Math.max(0, Math.min(cap.getSelectedCharacter(), CHARACTERS.length - 1))
            );
        }
    }

    @Override
    protected void init() {
        super.init();

        // 计算GUI居中位置
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;
        int fullscreenCenterX = (this.width - FULLSCREEN_GUI_WIDTH) / 2;
        int fullscreenCenterY = (this.height - FULLSCREEN_GUI_HEIGHT) / 2;

        // 清除旧按钮
        this.clearWidgets();

        if (showCharacterSelection) {
            // 显示全屏角色选择菜单

            // 添加返回按钮（移到上方）
            this.addRenderableWidget(Button.builder(
                Component.translatable("gui.strinovamc.back"),
                button -> {
                    showCharacterSelection = false;
                    currentPage = 0; // 重置页面
                    this.init(); // 重新初始化界面
                }
            ).pos(fullscreenCenterX + FULLSCREEN_GUI_WIDTH - 110, fullscreenCenterY + 10).size(100, 20).build());

            // 计算分页信息
            int totalPages = (CHARACTERS.length + CHARACTERS_PER_PAGE - 1) / CHARACTERS_PER_PAGE;
            int startIndex = currentPage * CHARACTERS_PER_PAGE;
            int endIndex = Math.min(startIndex + CHARACTERS_PER_PAGE, CHARACTERS.length);

            // 为当前页的每个角色添加方格按钮
            int gridStartX = fullscreenCenterX + 25;
            int gridStartY = fullscreenCenterY + 40;
            int gridSpacingX = 125;
            int gridSpacingY = 55;
            int maxColumns = 2;

            for (int i = startIndex; i < endIndex; i++) {
                final int characterIndex = i;
                int pageIndex = i - startIndex;
                int column = pageIndex % maxColumns;
                int row = pageIndex / maxColumns;
                int gridX = gridStartX + column * gridSpacingX;
                int gridY = gridStartY + row * gridSpacingY;

                // 添加角色方格按钮（调整按钮大小以适应框体）
                this.addRenderableWidget(Button.builder(
                    Component.translatable("gui.strinovamc.character_" + CHARACTERS[i].toLowerCase().replace(" ", "_")),
                    button -> selectSpecificCharacter(characterIndex)
                ).pos(gridX, gridY).size(110, 50).build());
            }

            // 添加分页控制按钮
            if (currentPage > 0) {
                // 上一页按钮
                this.addRenderableWidget(Button.builder(
                    Component.literal("<"),
                    button -> {
                        currentPage--;
                        this.init();
                    }
                ).pos(fullscreenCenterX + 10, fullscreenCenterY + FULLSCREEN_GUI_HEIGHT - 30).size(20, 20).build());
            }

            if (currentPage < totalPages - 1) {
                // 下一页按钮
                this.addRenderableWidget(Button.builder(
                    Component.literal(">"),
                    button -> {
                        currentPage++;
                        this.init();
                    }
                ).pos(fullscreenCenterX + FULLSCREEN_GUI_WIDTH - 30, fullscreenCenterY + FULLSCREEN_GUI_HEIGHT - 30).size(20, 20).build());
            }

            // 显示页码
            this.addRenderableWidget(Button.builder(
                Component.literal((currentPage + 1) + "/" + totalPages),
                button -> {}
            ).pos(fullscreenCenterX + FULLSCREEN_GUI_WIDTH / 2 - 20, fullscreenCenterY + FULLSCREEN_GUI_HEIGHT - 30).size(40, 20).build());

        } else {
            // 添加选择角色按钮（打开角色选择菜单）
            characterSelectButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.strinovamc.select_character"),
                button -> {
                    showCharacterSelection = true;
                    this.init(); // 重新初始化界面
                }
            ).pos(centerX + 75, centerY + 150).size(100, 20).build());

            // 添加确认选择按钮
            selectButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.strinovamc.confirm_selection"),
                button -> confirmCharacterSelection()
            ).pos(centerX + 75, centerY + 175).size(100, 20).build());

            // 添加关闭按钮
            closeButton = this.addRenderableWidget(Button.builder(
                Component.translatable("gui.strinovamc.close"),
                button -> onClose()
            ).pos(centerX + 75, centerY + 200).size(100, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        // 计算GUI居中位置
        int centerX = (this.width - GUI_WIDTH) / 2;
        int centerY = (this.height - GUI_HEIGHT) / 2;
        int fullscreenCenterX = (this.width - FULLSCREEN_GUI_WIDTH) / 2;
        int fullscreenCenterY = (this.height - FULLSCREEN_GUI_HEIGHT) / 2;

        if (showCharacterSelection) {
            // 绘制全屏GUI背景
            guiGraphics.fill(fullscreenCenterX, fullscreenCenterY,
                fullscreenCenterX + FULLSCREEN_GUI_WIDTH,
                fullscreenCenterY + FULLSCREEN_GUI_HEIGHT, 0x80000000);

            // 绘制角色选择菜单标题
            guiGraphics.drawCenteredString(this.font,
                Component.translatable("gui.strinovamc.character_selection"),
                this.width / 2, fullscreenCenterY + 15, 0xFFFFFF);

            // 绘制当前页的角色方格
            int startIndex = currentPage * CHARACTERS_PER_PAGE;
            int endIndex = Math.min(startIndex + CHARACTERS_PER_PAGE, CHARACTERS.length);

            int gridStartX = fullscreenCenterX + 25;
            int gridStartY = fullscreenCenterY + 40;
            int gridSpacingX = 125;
            int gridSpacingY = 55;
            int maxColumns = 2;

            for (int i = startIndex; i < endIndex; i++) {
                int pageIndex = i - startIndex;
                int column = pageIndex % maxColumns;
                int row = pageIndex / maxColumns;
                int gridX = gridStartX + column * gridSpacingX;
                int gridY = gridStartY + row * gridSpacingY;

                // 绘制角色方格背景（调整方格大小以匹配按钮）
                guiGraphics.fill(gridX, gridY, gridX + 110, gridY + 50, 0x60000000);

                // 绘制角色头像占位区域
                guiGraphics.fill(gridX + 5, gridY + 5, gridX + 45, gridY + 45, 0x40FFFFFF);

                // 绘制角色名称
                guiGraphics.drawCenteredString(
                    this.font,
                    Component.translatable("gui.strinovamc.character_" + CHARACTERS[i].toLowerCase().replace(" ", "_")),
                    gridX + 75,
                    gridY + 10,
                    0xFFFFFF
                );

                // 绘制角色阵营
                int factionColor = CHARACTER_FACTIONS[i].equals("SCISSORS") ? 0xFF5555 : 0x55FF55;
                guiGraphics.drawCenteredString(
                    this.font,
                    Component.literal(CHARACTER_FACTIONS[i]),
                    gridX + 75,
                    gridY + 28,
                    factionColor
                );
            }
        } else {
            // 绘制GUI背景
            guiGraphics.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0x80000000);

            // 绘制标题
            guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, centerY + 15, 0xFFFFFF);

            // 绘制当前选择的角色（会根据语言自动本地化）
            guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("gui.strinovamc.selected_character",
                    Component.translatable("gui.strinovamc.character_" + CHARACTERS[selectedCharacter].toLowerCase().replace(" ", "_"))),
                this.width / 2,
                centerY + 40,
                0xFFFFFF
            );

            // 根据阵营确定颜色：SCISSORS为红色，其他为绿色
            int factionColor = CHARACTER_FACTIONS[selectedCharacter].equals("SCISSORS") ? 0xFF5555 : 0x55FF55;

            // 绘制角色阵营
            guiGraphics.drawCenteredString(
                this.font,
                Component.translatable("gui.strinovamc.character_faction", CHARACTER_FACTIONS[selectedCharacter]),
                this.width / 2,
                centerY + 55,
                factionColor
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
                Component.translatable("gui.strinovamc.character_" + CHARACTERS[selectedCharacter].toLowerCase().replace(" ", "_")),
                this.width / 2,
                centerY + 115,
                0xFFFFFF
            );

            // 显示选择确认信息（如果角色已被选择）
            if (characterSelected && messageDisplayTime > 0) {
                guiGraphics.drawCenteredString(
                    this.font,
                    Component.translatable("gui.strinovamc.character_selected",
                        Component.translatable("gui.strinovamc.character_" + CHARACTERS[selectedCharacter].toLowerCase().replace(" ", "_"))),
                    this.width / 2,
                    centerY + 215,
                    0x55FF55
                );
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        // 设置为false，这样UI打开时游戏不会暂停
        return false;
    }

    /*
     * 选择特定角色
     */
    private void selectSpecificCharacter(int characterIndex) {
        this.selectedCharacter = characterIndex;
        // 选择后返回主菜单
        showCharacterSelection = false;
        currentPage = 0; // 重置页面
        this.init(); // 重新初始化界面
    }

    /*
     * 确认角色选择
     */
    private void confirmCharacterSelection() {
        characterSelected = true;
        messageDisplayTime = 100; // 显示消息约5秒(在20 TPS下)

        // 将所选角色发送到服务器保存(随玩家NBT持久化)
        NetworkHandler.sendToServer(new SelectCharacterC2SPacket(selectedCharacter));
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
