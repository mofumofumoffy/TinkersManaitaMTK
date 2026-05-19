package com.mochi_753.tconstructmtk;

import com.mochi_753.tconstructmtk.common.capabilities.TConstructMTKCapProvider;
import com.mochi_753.tconstructmtk.common.event.TConstructMTKEventHandler;
import com.mochi_753.tconstructmtk.common.network.SyncArmorModePacket;
import com.mochi_753.tconstructmtk.common.registry.TConstructMTKFluids;
import com.mochi_753.tconstructmtk.common.registry.TConstructMTKItems;
import com.mochi_753.tconstructmtk.common.registry.TConstructMTKModifiers;
import com.mochi_753.tconstructmtk.common.registry.TConstructMTKTiers;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

@Mod(TConstructMTK.MOD_ID)
public class TConstructMTK {
    public static final String MOD_ID = "tconstructmtk";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SuppressWarnings("removal")
    public TConstructMTK() {
        this(FMLJavaModLoadingContext.get());
    }

    public TConstructMTK(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();

        TConstructMTKItems.register(eventBus);
        TConstructMTKFluids.register(eventBus);
        TConstructMTKModifiers.register(eventBus);

        eventBus.addListener(TConstructMTKEventHandler::onRegisterCaps);

        new TConstructMTKTiers();

        ToolCapabilityProvider.register(TConstructMTKCapProvider::new);

        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onLivingAttack);
        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onPlayerDamage);
        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onLivingEquipmentChange);
        MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onRightClickItem);

        CHANNEL.messageBuilder(SyncArmorModePacket.class, 1)
                .encoder(SyncArmorModePacket::encode)
                .decoder(SyncArmorModePacket::new)
                .consumerMainThread(SyncArmorModePacket::handle)
                .add();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(TConstructMTKEventHandler::onClientTick);
        });
    }
}
