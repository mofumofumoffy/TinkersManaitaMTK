package com.mochi_753.tconstructmtk.datagen.material;

import com.mochi_753.tconstructmtk.common.material.TConstructMTKMaterialIds;
import com.mochi_753.tconstructmtk.common.registry.TConstructMTKTiers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.*;

@MethodsReturnNonnullByDefault
public class TConstructMTKMaterialStatsDataProvider extends AbstractMaterialStatsDataProvider {
    public TConstructMTKMaterialStatsDataProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
        super(packOutput, materials);
    }

    @Override
    protected void addMaterialStats() {
        addMaterialStats(TConstructMTKMaterialIds.MTK,
                new HeadMaterialStats(Integer.MAX_VALUE, Float.MAX_VALUE, TConstructMTKTiers.MTK, Float.MAX_VALUE),
                HandleMaterialStats.multipliers().attackDamage(Integer.MAX_VALUE).attackSpeed(Integer.MAX_VALUE).durability(Integer.MAX_VALUE).miningSpeed(Integer.MAX_VALUE).build(),
                StatlessMaterialStats.BINDING);

        addMaterialStats(TConstructMTKMaterialIds.MTK,
                new LimbMaterialStats(Integer.MAX_VALUE, Float.MAX_VALUE, 3.0F, 1.0F),
                new GripMaterialStats(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));

        addArmorShieldStats(TConstructMTKMaterialIds.MTK,
                PlatingMaterialStats.builder().armor(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE).durabilityFactor(Float.MAX_VALUE).knockbackResistance(Float.MAX_VALUE).toughness(Float.MAX_VALUE));
    }

    @Override
    public String getName() {
        return "Tinkers' ManaitaMTK Material Stats";
    }
}
