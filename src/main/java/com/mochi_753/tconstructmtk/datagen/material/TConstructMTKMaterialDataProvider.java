package com.mochi_753.tconstructmtk.datagen.material;

import com.mochi_753.tconstructmtk.common.material.TConstructMTKMaterialIds;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;

@MethodsReturnNonnullByDefault
public class TConstructMTKMaterialDataProvider extends AbstractMaterialDataProvider {
    public TConstructMTKMaterialDataProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addMaterials() {
        addMaterial(TConstructMTKMaterialIds.MTK, 999, ORDER_COMPAT, true);
    }

    @Override
    public String getName() {
        return "Tinkers' ManaitaMTK Materials";
    }
}
