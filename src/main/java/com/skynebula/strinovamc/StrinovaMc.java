package com.skynebula.strinovamc;

import com.mojang.logging.LogUtils;
import com.skynebula.strinovamc.block.ModBlocks;
import com.skynebula.strinovamc.capability.StringStateCapability;
import com.skynebula.strinovamc.effect.ModEffects;
import com.skynebula.strinovamc.item.ModItems;
import com.skynebula.strinovamc.item.ModTabs;
import com.skynebula.strinovamc.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StrinovaMc.MOD_ID)
public class StrinovaMc
{
    public static final String MOD_ID = "strinovamc";
    private static final Logger LOGGER = LogUtils.getLogger();

    public StrinovaMc(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("StrinovaMc common setup");
        NetworkHandler.register();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("StrinovaMc server starting");
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(StringStateCapability.class);
    }
}
