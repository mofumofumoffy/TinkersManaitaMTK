package com.mochi_753.tconstructmtk.common.capabilities;

import com.mochi_753.tconstructmtk.TConstructMTK;
import com.takoy3466.manaitamtk.capability.interfaces.*;
import com.takoy3466.manaitamtk.util.ToolUtil;
import com.takoy3466.manaitamtk.util.WeaponUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolMTKCapability implements IKillSword, IRangeBreak, IMultiple, IWoodReverse, ISpreadGrow {

    //melee
    public static final ResourceLocation IS_KILL_ALL = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "is_kill_all");

    //harvest
    public static final ResourceLocation RANGE_BREAK = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "range_break");
    public static final ResourceLocation MULTIPLE = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "multiple");


    protected final ItemStack stack;
    protected final IToolStackView toolStack;

    public ToolMTKCapability(ItemStack stack, IToolStackView toolStack){
        this.stack = stack;
        this.toolStack = toolStack;
    }

    @Override
    public void setIsKillAll(boolean isKillAll) {
        if(stack.is(TinkerTags.Items.MELEE_WEAPON)) {
            toolStack.getPersistentData().putBoolean(IS_KILL_ALL, isKillAll);
        }
    }

    @Override
    public boolean isKillAll() {
        if(stack.is(TinkerTags.Items.MELEE_WEAPON)) {
            return toolStack.getPersistentData().getBoolean(IS_KILL_ALL);
        }
        return false;
    }

    @Override
    public void kill(LivingEntity target, Level level, Player player) {
        if(stack.is(TinkerTags.Items.MELEE_WEAPON)){
            WeaponUtil.lightningStriker(target, level, player);
        }
    }

    @Override
    public void rangeBreak(Level level, int x, int y, int z, LivingEntity livingEntity, int size) {
        if (!level.isClientSide()) {
            ToolUtil.RangeBreak(level, x, y, z, livingEntity, size);
        }
    }

    @Override
    public void setRange(int range) {
        toolStack.getPersistentData().putInt(RANGE_BREAK, Math.max(range, 1));
    }

    @Override
    public int getRange() {
        return toolStack.getPersistentData().getInt(RANGE_BREAK);
    }

    @Override
    public void setMultiple(int multiple) {
        toolStack.getPersistentData().putInt(MULTIPLE, Math.max(multiple, 1));
    }

    @Override
    public int getMultiple() {
        return toolStack.getPersistentData().getInt(MULTIPLE);
    }

    @Override
    public void spreadGrow(Level level, BlockPos blockPos, int i) {

    }

    @Override
    public void woodReverse(Level level, BlockPos blockPos, Player player, ItemStack itemStack, InteractionHand interactionHand) {

    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        //no op
    }

    @Override
    public CompoundTag serializeNBT() {
        //no op
        return new CompoundTag();
    }
}
