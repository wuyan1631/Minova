package com.skynebula.strinovamc.network;

import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/*
 * 客户端 -> 服务器: 保存玩家选择的角色(角色面板确认时发送)
 */
public class SelectCharacterC2SPacket
{
    private final int characterIndex;

    public SelectCharacterC2SPacket(int characterIndex)
    {
        this.characterIndex = characterIndex;
    }

    public static void encode(SelectCharacterC2SPacket msg, FriendlyByteBuf buf)
    {
        buf.writeInt(msg.characterIndex);
    }

    public static SelectCharacterC2SPacket decode(FriendlyByteBuf buf)
    {
        return new SelectCharacterC2SPacket(buf.readInt());
    }

    public static void handle(SelectCharacterC2SPacket msg, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() ->
        {
            ServerPlayer player = ctx.getSender();
            if (player != null)
            {
                player.getCapability(StringStateCapability.INSTANCE)
                        .ifPresent(cap -> cap.setSelectedCharacter(msg.characterIndex));
            }
        });
        ctx.setPacketHandled(true);
    }
}
