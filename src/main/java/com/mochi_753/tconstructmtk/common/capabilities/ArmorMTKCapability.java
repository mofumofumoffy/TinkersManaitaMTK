package com.mochi_753.tconstructmtk.common.capabilities;

import com.mochi_753.tconstructmtk.TConstructMTK;
import com.mochi_753.tconstructmtk.common.capabilities.props.ArmorMode;
import com.mochi_753.tconstructmtk.common.capabilities.props.FlySpeedMode;
import com.takoy3466.manaitamtk.capability.interfaces.IFly;
import com.takoy3466.manaitamtk.capability.interfaces.IInvincible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ArmorMTKCapability implements IInvincible, IFly, IArmorMode {

    public static final ResourceLocation FLY_SPEED = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "fly_speed");
    public static final ResourceLocation ARMOR_MODE = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "armor_mode");
    public static Capability<IArmorMode> ARMOR_MODE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    protected final ItemStack stack;
    protected final IToolStackView toolStack;

    public ArmorMTKCapability(ItemStack stack, IToolStackView toolStack) {
        this.stack = stack;
        this.toolStack = toolStack;
    }


    @Override
    public void setCanFly(boolean canFly) {
        //no op
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public float getFlySpeed() {
        return getFlySpeedMode().getSpeed();
    }

    @Override
    public void setFlySpeed(float flySpeed) {

    }

    @Override
    public boolean isInvincible() {
        return true;
    }

    @Override
    public void setInvincible(boolean invincible) {
        //no op
    }

    @Override
    public ArmorMode getArmorMode() {
        return ArmorMode.byIndex(toolStack.getPersistentData().getInt(ARMOR_MODE));
    }

    @Override
    public void setArmorMode(int index) {
        toolStack.getPersistentData().putInt(ARMOR_MODE, index);
    }

    @Override
    public FlySpeedMode getFlySpeedMode() {
        return FlySpeedMode.byIndex(toolStack.getPersistentData().getInt(FLY_SPEED));
    }

    @Override
    public void setFlySpeedMode(int index) {
        toolStack.getPersistentData().putInt(FLY_SPEED, index);
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        //no op
    }

    @Override
    public CompoundTag serializeNBT() {
        //no op
        return null;
    }
}
