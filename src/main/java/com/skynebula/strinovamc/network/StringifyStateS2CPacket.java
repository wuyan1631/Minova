package com.skynebula.strinovamc.network;

import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/*
 * 服务器 -> 客户端: 同步完整弦化状态(弦化/超弦体/已选角色)
 * 用于玩家加入世界、超弦体激活等服务器端状态变化
 */
public class StringifyStateS2CPacket
{
    private final boolean strinovaMode;
    private final boolean stringified;
    private final boolean superStringified;
    private final int selectedCharacter;

    public StringifyStateS2CPacket(boolean strinovaMode, boolean stringified, boolean superStringified, int selectedCharacter)
    {
        this.strinovaMode = strinovaMode;
        this.stringified = stringified;
        this.superStringified = superStringified;
        this.selectedCharacter = selectedCharacter;
    }

    public static void encode(StringifyStateS2CPacket msg, FriendlyByteBuf buf)
    {
        buf.writeBoolean(msg.strinovaMode);
        buf.writeBoolean(msg.stringified);
        buf.writeBoolean(msg.superStringified);
        buf.writeInt(msg.selectedCharacter);
    }

    public static StringifyStateS2CPacket decode(FriendlyByteBuf buf)
    {
        return new StringifyStateS2CPacket(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
    }

    public static void handle(StringifyStateS2CPacket msg, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() ->
        {
            if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            {
                Player player = Minecraft.getInstance().player;
                if (player != null)
                {
                    player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
                    {
                        cap.setStrinovaMode(msg.strinovaMode);
                        cap.setStringified(msg.stringified);
                        cap.setSuperStringified(msg.superStringified);
                        cap.setSelectedCharacter(msg.selectedCharacter);
                        // 退出弦化时不强制切换相机视角, 保持当前(越肩)视角
                    });
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
