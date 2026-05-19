package com.mochi_753.tconstructmtk.mixin;

import com.mochi_753.tconstructmtk.common.registry.TConstructMTKModifiers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private static final List<EquipmentSlot> ARMOR_SLOTS = new ArrayList<>(List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));

    @Inject(at = @At("HEAD"), method = "setHealth", cancellable = true)
    private void onSetHealth(float v, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (tinkersManaitaMTK$whetherInvincible(entity)) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getHealth", cancellable = true)
    private void onGetHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (tinkersManaitaMTK$whetherInvincible(entity)) {
            cir.setReturnValue(entity.getMaxHealth());
        }
    }

    @Inject(method = "isDeadOrDying", at = @At("RETURN"), cancellable = true)
    private void onIsDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (tinkersManaitaMTK$whetherInvincible(entity)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAlive", at = @At("RETURN"), cancellable = true)
    private void onIsAlive(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (tinkersManaitaMTK$whetherInvincible(entity)) {
            cir.setReturnValue(true);
        }
    }


    @Unique
    private boolean tinkersManaitaMTK$whetherInvincible(LivingEntity entity) {
        if (entity == null) return false;
        if (entity instanceof Player player && player.getInventory() != null) {
            return ARMOR_SLOTS.stream().anyMatch(equipmentSlot -> {
                ItemStack armorStack = player.getItemBySlot(equipmentSlot);
                return armorStack.getItem() instanceof IModifiable && ToolStack.from(armorStack).getModifierLevel(TConstructMTKModifiers.MTK_MODIFIER.get()) > 0;
            });
        }
        return false;
    }
}
