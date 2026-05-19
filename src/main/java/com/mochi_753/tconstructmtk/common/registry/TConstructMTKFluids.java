package com.mochi_753.tconstructmtk.common.registry;

import com.mochi_753.tconstructmtk.TConstructMTK;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;

public class TConstructMTKFluids {
    private static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TConstructMTK.MOD_ID);
    private static final FluidType.Properties MTK_FLUID_PROPERTY = FluidType.Properties.create()
            .adjacentPathType(null)
            .canDrown(false)
            .canSwim(false)
            .density(Integer.MAX_VALUE)
            .descriptionId("fluid.tconstructmtk.molten_mtk")
            .motionScale(0.0)
            .pathType(BlockPathTypes.LAVA)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .temperature(1300);
    public static final FlowingFluidObject<ForgeFlowingFluid> MOLTEN_MTK = FLUIDS.register("molten_mtk")
            .block(MapColor.COLOR_BLACK, 0)
            .bucket()
            .commonTag()
            .type(MTK_FLUID_PROPERTY)
            .flowing();

    private TConstructMTKFluids() {
    }

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
