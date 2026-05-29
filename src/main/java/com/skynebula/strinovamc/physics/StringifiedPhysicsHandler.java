package com.skynebula.strinovamc.physics;

import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class StringifiedPhysicsHandler
{
    private static final Map<UUID, Double> lastMotionY = new HashMap<>();

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
        if (event.getEntity() instanceof Player player && !player.level().isClientSide())
        {
            if (player.tickCount % 5 == 0) {
                player.getCapability(StringStateCapability.INSTANCE).ifPresent(cap ->
                {
                    if (cap.isStringified())
                    {
                        applyStringifiedPhysics(player);
                    } else {
                        restoreNormalPhysics(player);
                    }

                    if (cap.isSuperStringified())
                    {
                        player.addEffect(new MobEffectInstance(ModEffects.KAQIU_BODY.get(), 30, 0, false, false, true));
                    }
                    else
                    {
                        player.removeEffect(ModEffects.KAQIU_BODY.get());
                    }
                });
            }

            double currentY = player.getDeltaMovement().y();
            Double prevY = lastMotionY.get(player.getUUID());

            if (prevY != null && currentY > 0.3 && prevY <= 0.1 && !player.onGround()
                    && player.hasEffect(ModEffects.KAQIU_BODY.get()))
            {
                player.setDeltaMovement(player.getDeltaMovement().x(), currentY * 1.5, player.getDeltaMovement().z());
                player.hurtMarked = true;
            }

            lastMotionY.put(player.getUUID(), currentY);
        }
    }

    private static void applyStringifiedPhysics(Player player)
    {
        float yaw = player.getYRot();

        if (isFacingForwardOrBack(yaw))
        {
            player.setBoundingBox(player.getBoundingBox().inflate(0, 0, -0.4));
        } else
        {
            player.setBoundingBox(player.getBoundingBox().inflate(-0.4, 0, 0));
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
