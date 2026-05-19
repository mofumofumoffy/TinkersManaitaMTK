package com.mochi_753.tconstructmtk.common.item;

import com.takoy3466.manaitamtk.entity.EntityArrowMTK;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TinkerArrowMTKItem extends ArrowItem {
    public TinkerArrowMTKItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        return new EntityArrowMTK(pLevel, pShooter);
    }
}
