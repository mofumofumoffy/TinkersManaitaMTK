package com.mochi_753.tconstructmtk.common.capabilities;

import com.mochi_753.tconstructmtk.TConstructMTK;
import com.takoy3466.manaitamtk.capability.interfaces.IKillSword;
import com.takoy3466.manaitamtk.capability.interfaces.IRangeBreak;
import com.takoy3466.manaitamtk.capability.interfaces.ISpreadGrow;
import com.takoy3466.manaitamtk.capability.interfaces.IWoodReverse;
import com.takoy3466.manaitamtk.core.MTKBlockList;
import com.takoy3466.manaitamtk.util.ToolUtil;
import com.takoy3466.manaitamtk.util.WeaponUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.HashMap;

public class ToolMTKCapability implements IKillSword, IRangeBreak, IWoodReverse, ISpreadGrow {

    //melee
    public static final ResourceLocation IS_KILL_ALL = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "is_kill_all");

    //harvest
    public static final ResourceLocation RANGE_BREAK = ResourceLocation.fromNamespaceAndPath(TConstructMTK.MOD_ID, "range_break");
    private static final HashMap<Block, Block> REVERSE_STRIPPABLES = new HashMap<>();

    static {
        for (HashMap.Entry<Block, Block> entry : MTKBlockList.STRIPPABLES.entrySet()) {
            Block originalBlock = entry.getKey();
            Block strippedBlock = entry.getValue();
            REVERSE_STRIPPABLES.put(strippedBlock, originalBlock);
        }
    }

    protected final ItemStack stack;
    protected final IToolStackView toolStack;

    public ToolMTKCapability(ItemStack stack, IToolStackView toolStack) {
        this.stack = stack;
        this.toolStack = toolStack;
    }

    @Override
    public void setIsKillAll(boolean isKillAll) {
        if (stack.is(TinkerTags.Items.MELEE_WEAPON)) {
            toolStack.getPersistentData().putBoolean(IS_KILL_ALL, isKillAll);
        }
    }

    @Override
    public boolean isKillAll() {
        if (stack.is(TinkerTags.Items.MELEE_WEAPON)) {
            return toolStack.getPersistentData().getBoolean(IS_KILL_ALL);
        }
        return false;
    }

    @Override
    public void kill(LivingEntity target, Level level, Player player) {
        if (stack.is(TinkerTags.Items.MELEE_WEAPON)) {
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
    public int getRange() {
        return toolStack.getPersistentData().getInt(RANGE_BREAK);
    }

    @Override
    public void setRange(int range) {
        toolStack.getPersistentData().putInt(RANGE_BREAK, Math.max(range, 1));
    }

    @Override
    public void spreadGrow(Level level, BlockPos blockPos, int radius) {
        if (toolStack.getModifierLevel(ModifierIds.tilling) > 0) {
            ToolUtil.spreadGrow(level, blockPos, radius);
            level.playSound(null, blockPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void woodReverse(Level level, BlockPos pos, Player player, ItemStack stack, InteractionHand hand) {
        if (toolStack.getModifierLevel(ModifierIds.stripping) > 0) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            Block strippedBlock = REVERSE_STRIPPABLES.get(block);
            if (strippedBlock == null) {
                return;
            }

            BlockState strippedState = strippedBlock.defaultBlockState();

            if (strippedState.hasProperty(RotatedPillarBlock.AXIS)) {
                strippedState = strippedState.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
            level.setBlock(pos, strippedState, 1 + 2 + 8);
            level.playSound(player, pos, SoundType.WOOD.getPlaceSound(), SoundSource.BLOCKS, 1, 1);
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
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
