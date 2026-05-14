package com.mochi_753.tconstructmtk.mixin;

import com.mochi_753.tconstructmtk.common.registry.TConstructMTKModifiers;
import com.takoy3466.manaitamtk.capability.MTKCapabilities;
import com.takoy3466.manaitamtk.capability.helper.MTKCapabilityHelper;
import com.takoy3466.manaitamtk.network.MTKNetwork;
import com.takoy3466.manaitamtk.network.PacketRange;
import com.takoy3466.manaitamtk.screen.MTKSwitcherScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = MTKSwitcherScreen.class, remap = false)
public class MTKSwitcherScreenMixin {
    @Shadow
    private MTKSwitcherScreen.MTKIcon previousMode;

    @Inject(
            at = @At("HEAD"),
            method = "setDefault"
    )
    private void setDefaultExtension(CallbackInfo ci){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            ItemStack stack = minecraft.player.getMainHandItem();
            if (stack.getItem() instanceof IModifiable && ToolStack.from(stack).getModifierLevel(TConstructMTKModifiers.MTK_MODIFIER.get()) > 0) {
                MTKCapabilityHelper.execute(MTKCapabilities.RANGE_BREAK, stack, iRangeBreak -> this.previousMode = MTKSwitcherScreen.MTKIcon.getFromRange(iRangeBreak.getRange()));
            }
        }
    }

    @Inject(
            at = @At("HEAD"),
            method = "setRange"
    )
    private void setRangeExtension(MTKSwitcherScreen.MTKIcon mtkIcon, CallbackInfo ci){
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            ItemStack stack = minecraft.player.getMainHandItem();
            if (stack.getItem() instanceof IModifiable && ToolStack.from(stack).getModifierLevel(TConstructMTKModifiers.MTK_MODIFIER.get()) > 0) {
                MTKNetwork.sendToServer(new PacketRange(mtkIcon.getModeRange()));
            }
        }
    }
}
