package com.mochi_753.tconstructmtk.common.modiffier;

import com.mochi_753.tconstructmtk.common.capabilities.ToolMTKCapability;
import com.mochi_753.tconstructmtk.common.registry.TConstructMTKItems;
import com.takoy3466.manaitamtk.KeyMapping.MTKKeyMappings;
import com.takoy3466.manaitamtk.capability.MTKCapabilities;
import com.takoy3466.manaitamtk.entity.EntityArrowMTK;
import com.takoy3466.manaitamtk.network.MTKNetwork;
import com.takoy3466.manaitamtk.network.PacketisKillAll;
import com.takoy3466.manaitamtk.util.WeaponUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModifierMTK extends NoLevelsModifier implements BreakSpeedModifierHook, MeleeHitModifierHook, BowAmmoModifierHook, TooltipModifierHook {
    private final Component SWORD_TEXT_ENEMY = Component.translatable("gui.overlay.sword.enemy_die").withStyle(ChatFormatting.WHITE);
    private final Component SWORD_TEXT_ALL = Component.translatable("gui.overlay.sword.all_die").withStyle(ChatFormatting.RED);
    private final Component MODE = Component.translatable("item.manaitamtk.manaita_sword.hover_text_mode");
    private final Component KEY = Component.literal("Press Shift + ");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BREAK_SPEED, ModifierHooks.MELEE_HIT, ModifierHooks.BOW_AMMO, ModifierHooks.TOOLTIP);
    }

    @Override
    public void onBreakSpeed(IToolStackView iToolStackView, ModifierEntry modifierEntry, PlayerEvent.BreakSpeed breakSpeed, Direction direction, boolean b, float v) {
        breakSpeed.setNewSpeed(Float.MAX_VALUE);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        onMeleeHit(context.getAttacker(), context.getTarget(), true);
        return MeleeHitModifierHook.super.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        onMeleeHit(context.getAttacker(), context.getTarget(), false);
    }

    private void onMeleeHit(LivingEntity attacker, Entity entity, boolean lightning) {
        if (entity instanceof PartEntity<?> partEntity) {
            onMeleeHit(attacker, partEntity.getParent(), lightning);
            return;
        }

        if (attacker instanceof Player player && !player.level().isClientSide() && entity instanceof LivingEntity target) {
            WeaponUtil.die(target);
        }
    }

    @Override
    public Component getDisplayName() {
        return ((MutableComponent) super.getDisplayName()).withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public Component getDisplayName(int level) {
        return this.getDisplayName();
    }

    @Override
    public ItemStack findAmmo(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, ItemStack itemStack, Predicate<ItemStack> predicate) {
        return new ItemStack(TConstructMTKItems.TINKER_ARROW_MTK.get());
    }

    @Override
    public void addTooltip(IToolStackView iToolStackView, ModifierEntry modifierEntry, @Nullable Player player, List<Component> list, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.manaitamtk.manaita_sword_hover_text")
                .withStyle(ChatFormatting.GRAY));
        list.add(Component.literal(this.KEY.getString() + MTKKeyMappings.SwitchExterminationKey.getKey().getDisplayName().getString()));


        if(iToolStackView.hasTag(TinkerTags.Items.MELEE_WEAPON)){
            if (iToolStackView.getPersistentData().getBoolean(ToolMTKCapability.IS_KILL_ALL)) {
                list.add(Component.literal(this.MODE.getString() + this.SWORD_TEXT_ALL.getString()));
            } else list.add(Component.literal(this.MODE.getString() + this.SWORD_TEXT_ENEMY.getString()));
        }
    }
}