package com.skynebula.strinovamc.physics;

import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.effect.ModEffects;
import com.skynebula.strinovamc.network.NetworkHandler;
import com.skynebula.strinovamc.network.StringifyStateS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * 弦化/超弦化物理处理器
 * 弦化: 压扁碰撞箱, 空中弦化可飘飞/贴墙/免疫摔落,
 *       离地掉落自动退出, 快落地时自动解除弦化与飘飞
 * 超弦化(卡丘身): 跳跃增强
 */
@Mod.EventBusSubscriber
public class StringifiedPhysicsHandler
{
    // 弦化飘飞时的目标下落速度(方块/刻, 负值; 约 6 格/秒)
    private static final double GLIDE_FALL_SPEED = -0.3;
    // 弦化贴墙时的目标下滑速度(约 0.8 格/秒, 几乎贴住墙壁缓慢下滑)
    private static final double WALL_STICK_FALL_SPEED = -0.04;
    // 贴墙检测距离(方块): 视线前方多远内的墙壁会被吸附
    private static final double WALL_DETECT_DISTANCE = 1.5;
    // 快落地自动解除弦化/飘飞的判定距离(方块, 脚底到地面)
    private static final double LANDING_EXIT_DISTANCE = 1.0;

    private static final Map<UUID, Double> lastMotionY = new HashMap<>();
    // 记录每个玩家上一tick是否在地面, 用于"离地自动退出弦化"的检测
    private static final Map<UUID, Boolean> lastOnGround = new HashMap<>();

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event)
    {
        if (event.getEntity() instanceof Player player)
        {
            player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
            {
                if (cap.isStringified())
                {
                    applyStringifiedPhysics(player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean isServerSide = !player.level().isClientSide();
        boolean hasKaQiuBody = player.hasEffect(ModEffects.KAQIU_BODY.get());
        boolean isStrinovaMode = player.getCapability(StringStateCapability.INSTANCE)
                .map(StringStateCapability::isStrinovaMode)
                .orElse(false);
        boolean isStringified = player.getCapability(StringStateCapability.INSTANCE)
                .map(StringStateCapability::isStringified)
                .orElse(false);

        // 弦化/超弦化状态维护(仅服务器执行, 保证权威)
        if (isServerSide && player.tickCount % 5 == 0)
        {
            player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
            {
                if (cap.isStringified())
                {
                    applyStringifiedPhysics(player);
                }
                else
                {
                    restoreNormalPhysics(player);
                }

                // 卡丘身效果仅在Strinova模式开启时维持
                if (cap.isSuperStringified() && cap.isStrinovaMode())
                {
                    player.addEffect(new MobEffectInstance(ModEffects.KAQIU_BODY.get(), 30, 0, false, false, true));
                }
                else
                {
                    player.removeEffect(ModEffects.KAQIU_BODY.get());
                }
            });
        }

        // 自动退出弦化(仅服务器判定, 通过数据包同步客户端):
        // 1. 离地(掉落)时退出; 2. 飘飞中快落地时解除飘飞和弦化
        if (isServerSide)
        {
            checkAutoExitStringifyOnAirborne(player);
            checkAutoExitStringifyNearGround(player);
        }

        // 飘飞/贴墙: Strinova模式+弦化+空中时生效(客户端与服务器都执行,
        // 客户端保证本地手感流畅, 服务器保证权威一致)
        if (isStrinovaMode && isStringified)
        {
            applyGlidePhysics(player);
        }

        // 卡丘身跳跃增强: 上升过程中给一个额外的向上加速度(仅服务器, 通过hurtMarked同步)
        if (hasKaQiuBody && isServerSide)
        {
            double currentY = player.getDeltaMovement().y();
            Double prevY = lastMotionY.get(player.getUUID());

            if (prevY != null && currentY > 0.3 && prevY <= 0.1 && !player.onGround())
            {
                player.setDeltaMovement(player.getDeltaMovement().x(), currentY * 1.5, player.getDeltaMovement().z());
                player.hurtMarked = true;
            }

            lastMotionY.put(player.getUUID(), currentY);
        }
        else if (isServerSide)
        {
            lastMotionY.remove(player.getUUID());
        }
    }

    /*
     * 离地自动退出弦化
     * 检测"上一tick在地面、当前离地"的瞬间, 且竖直速度不是跳跃(<=0.1)时,
     * 判定为从边缘掉落, 自动退出弦化状态; 主动跳跃(竖直速度>0.1)不退出
     */
    private static void checkAutoExitStringifyOnAirborne(Player player)
    {
        Boolean wasOnGround = lastOnGround.get(player.getUUID());
        boolean onGroundNow = player.onGround();
        lastOnGround.put(player.getUUID(), onGroundNow);

        // 只有"刚从地面离开"的那一tick才可能触发
        if (wasOnGround == null || !wasOnGround || onGroundNow) return;

        // 跳跃的竖直速度为正且较大, 从边缘掉落时速度<=0, 以此区分跳跃与离地
        if (player.getDeltaMovement().y() > 0.1) return;

        exitStringifyAndSync(player);
    }

    /*
     * 快落地自动解除飘飞和弦化
     * 弦化+空中(飘飞中)且脚底距地面小于阈值时, 解除弦化状态(飘飞随之结束)
     */
    private static void checkAutoExitStringifyNearGround(Player player)
    {
        if (player.onGround()) return;
        if (getDistanceToGround(player) > LANDING_EXIT_DISTANCE) return;

        exitStringifyAndSync(player);
    }

    /*
     * 退出弦化状态并同步到客户端(解除扁平化渲染与弦化限制)
     */
    private static void exitStringifyAndSync(Player player)
    {
        player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
        {
            if (cap.isStringified())
            {
                cap.setStringified(false);
                if (player instanceof ServerPlayer serverPlayer)
                {
                    NetworkHandler.sendToPlayer(
                        new StringifyStateS2CPacket(cap.isStrinovaMode(), false, cap.isSuperStringified(), cap.getSelectedCharacter()),
                        serverPlayer
                    );
                }
            }
        });
    }

    /*
     * 飘飞/贴墙物理效果(弦化+空中时生效)
     * 飘飞: 空中下落时减缓下落速度, 像纸片一样飘落
     * 贴墙: 视线前方有墙时几乎贴住墙壁缓慢下滑
     * 附带: 免疫摔落伤害
     */
    private static void applyGlidePhysics(Player player)
    {
        // 弦化飘飞/贴墙状态下不承受摔落伤害
        player.fallDistance = 0.0F;

        // 站在地面或正在上升时不干预运动
        if (player.onGround()) return;
        Vec3 motion = player.getDeltaMovement();
        if (motion.y() >= 0) return;

        // 贴墙: 视线前方有墙 -> 几乎贴住缓慢下滑; 飘飞: 无墙 -> 缓慢飘落
        double targetFallSpeed = isWallInFront(player) ? WALL_STICK_FALL_SPEED : GLIDE_FALL_SPEED;

        // 1.20.1 空中移动: 每tick先空气阻力(竖直方向×0.98)再受重力(-0.08)然后位移。
        // 要让实际下落速度稳定在 targetFallSpeed, 需在tick末尾把速度预先设为 target/0.98 + 0.08。
        double targetMotionY = targetFallSpeed / 0.98 + 0.08;

        if (motion.y() < targetMotionY)
        {
            // 注意: 这里不要设置 hurtMarked!
            // 客户端与服务器两端都在执行相同的钳制逻辑, 速度天然一致;
            // 若每tick置hurtMarked会强制服务器把自身速度/位置同步给客户端,
            // 而服务器端玩家没有水平输入(deltaMovement水平分量为0), 会把客户端的水平移动清零。
            player.setDeltaMovement(motion.x(), targetMotionY, motion.z());
        }
    }

    /*
     * 检测玩家视线前方是否有可贴的墙壁
     * 只把侧面(而非地面/天花板)视为可贴的墙
     */
    private static boolean isWallInFront(Player player)
    {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        BlockHitResult hit = player.level().clip(new ClipContext(
                eyePos,
                eyePos.add(look.scale(WALL_DETECT_DISTANCE)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (hit.getType() != HitResult.Type.BLOCK) return false;

        Direction hitDirection = hit.getDirection();
        return hitDirection != Direction.UP && hitDirection != Direction.DOWN;
    }

    /*
     * 计算玩家脚底到下方地面的距离(向下最多查8格, 查不到返回大数)
     */
    private static double getDistanceToGround(Player player)
    {
        BlockPos feet = player.blockPosition();
        for (int i = 1; i <= 8; i++)
        {
            BlockPos below = feet.below(i);
            BlockState state = player.level().getBlockState(below);
            if (!state.getCollisionShape(player.level(), below).isEmpty())
            {
                // 地面表面高度 = below.getY() + 1, 距离 = 脚部Y - 表面高度
                return player.getY() - (below.getY() + 1);
            }
        }
        return 999.0;
    }

    private static void applyStringifiedPhysics(Player player)
    {
        float yaw = player.getYRot();

        if (isFacingForwardOrBack(yaw))
        {
            // 面向前后时, 沿Z轴压扁为"纸片"碰撞箱(0.6 - 0.29*2 ≈ 0.02厚)
            player.setBoundingBox(player.getBoundingBox().inflate(0, 0, -0.29));
        } else
        {
            // 面向左右时, 沿X轴压扁为"纸片"碰撞箱
            player.setBoundingBox(player.getBoundingBox().inflate(-0.29, 0, 0));
        }
    }

    private static void restoreNormalPhysics(Player player)
    {
        player.refreshDimensions();
    }

    private static boolean isFacingForwardOrBack(float yaw)
    {
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;
        return (yaw >= 45 && yaw <= 135) || (yaw >= 225 && yaw <= 315);
    }
}
