package com.skynebula.strinovamc.network;

import com.skynebula.strinovamc.capability.StringStateCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/*
 * 客户端 -> 服务器: 同步Strinova模式开关(L键)
 */
public class StrinovaModeC2SPacket
{
    private final boolean enabled;

    public StrinovaModeC2SPacket(boolean enabled)
    {
        this.enabled = enabled;
    }

    public static void encode(StrinovaModeC2SPacket msg, FriendlyByteBuf buf)
    {
        buf.writeBoolean(msg.enabled);
    }

    public static StrinovaModeC2SPacket decode(FriendlyByteBuf buf)
    {
        return new StrinovaModeC2SPacket(buf.readBoolean());
    }

    public static void handle(StrinovaModeC2SPacket msg, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() ->
        {
            ServerPlayer player = ctx.getSender();
            if (player != null)
            {
                player.getCapability(StringStateCapability.INSTANCE)
                        .ifPresent(cap -> cap.setStrinovaMode(msg.enabled));
            }
        });
        ctx.setPacketHandled(true);
    }
}
