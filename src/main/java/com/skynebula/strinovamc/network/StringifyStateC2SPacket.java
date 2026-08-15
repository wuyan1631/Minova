package com.skynebula.strinovamc.network;

import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/*
 * 客户端 -> 服务器: 同步弦化状态(V键切换时发送)
 */
public class StringifyStateC2SPacket
{
    private final boolean stringified;

    public StringifyStateC2SPacket(boolean stringified)
    {
        this.stringified = stringified;
    }

    public static void encode(StringifyStateC2SPacket msg, FriendlyByteBuf buf)
    {
        buf.writeBoolean(msg.stringified);
    }

    public static StringifyStateC2SPacket decode(FriendlyByteBuf buf)
    {
        return new StringifyStateC2SPacket(buf.readBoolean());
    }

    public static void handle(StringifyStateC2SPacket msg, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() ->
        {
            ServerPlayer player = ctx.getSender();
            if (player != null)
            {
                player.getCapability(StringStateCapability.INSTANCE)
                        .ifPresent(cap -> cap.setStringified(msg.stringified));
            }
        });
        ctx.setPacketHandled(true);
    }
}
