package com.skynebula.strinovamc.network;

import com.skynebula.strinovamc.StrinovaMc;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/*
 * 网络通道管理
 * 负责注册所有自定义数据包(弦化状态同步、角色选择落库)
 */
public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(StrinovaMc.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextId = 0;

    public static void register()
    {
        CHANNEL.registerMessage(nextId++, StringifyStateC2SPacket.class,
                StringifyStateC2SPacket::encode,
                StringifyStateC2SPacket::decode,
                StringifyStateC2SPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(nextId++, StringifyStateS2CPacket.class,
                StringifyStateS2CPacket::encode,
                StringifyStateS2CPacket::decode,
                StringifyStateS2CPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(nextId++, SelectCharacterC2SPacket.class,
                SelectCharacterC2SPacket::encode,
                SelectCharacterC2SPacket::decode,
                SelectCharacterC2SPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(nextId++, StrinovaModeC2SPacket.class,
                StrinovaModeC2SPacket::encode,
                StrinovaModeC2SPacket::decode,
                StrinovaModeC2SPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /** 客户端 -> 服务器 */
    public static void sendToServer(Object message)
    {
        CHANNEL.sendToServer(message);
    }

    /** 服务器 -> 指定玩家 */
    public static void sendToPlayer(Object message, ServerPlayer player)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
