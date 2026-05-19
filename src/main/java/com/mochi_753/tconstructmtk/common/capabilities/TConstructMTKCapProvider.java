package com.mochi_753.tconstructmtk.common.capabilities;

import com.mochi_753.tconstructmtk.common.registry.TConstructMTKModifiers;
import com.takoy3466.manaitamtk.capability.MTKCapabilities;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.function.Supplier;

public class TConstructMTKCapProvider implements ToolCapabilityProvider.IToolCapabilityProvider {

    protected ToolMTKCapability toolMTKCapability;
    protected ArmorMTKCapability armorMTKCapability;

    public TConstructMTKCapProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        if(stack.getItem() instanceof ArmorItem){
            armorMTKCapability = new ArmorMTKCapability(stack, toolSupplier.get());
        } else {
            toolMTKCapability = new ToolMTKCapability(stack, toolSupplier.get());
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(IToolStackView iToolStackView, Capability<T> capability) {
        if(iToolStackView.getModifierLevel(TConstructMTKModifiers.MTK_MODIFIER.get()) > 0){
            if(iToolStackView.hasTag(TinkerTags.Items.WORN_ARMOR)){
                if(capability == MTKCapabilities.INVINCIBLE || capability == MTKCapabilities.FLY || capability == ArmorMTKCapability.ARMOR_MODE_CAPABILITY){
                    return LazyOptional.of(()->armorMTKCapability).cast();
                }
            } else {
                if(capability == MTKCapabilities.KILL_SWORD || capability == MTKCapabilities.RANGE_BREAK || capability == MTKCapabilities.SPREAD_GROW || capability == MTKCapabilities.WOOD_REVERSE){
                    return LazyOptional.of(()->toolMTKCapability).cast();
                }
            }
        }
        return LazyOptional.empty();
    }
}
