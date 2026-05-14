package com.mochi_753.tconstructmtk.common.event;

import com.mochi_753.tconstructmtk.common.capabilities.ArmorMTKCapability;
import com.mochi_753.tconstructmtk.common.capabilities.IArmorMode;
import com.mochi_753.tconstructmtk.common.capabilities.props.ArmorMode;
import com.mochi_753.tconstructmtk.common.capabilities.props.FlySpeedMode;
import com.mochi_753.tconstructmtk.mixin.ManaitaHelmetAccessor;
import com.takoy3466.manaitamtk.KeyMapping.MTKKeyMappings;
import com.takoy3466.manaitamtk.capability.MTKCapabilities;
import com.takoy3466.manaitamtk.capability.helper.MTKCapabilityHelper;
import com.takoy3466.manaitamtk.config.MTKConfig;
import com.takoy3466.manaitamtk.item.armor.HelmetManaita;
import com.takoy3466.manaitamtk.network.MTKNetwork;
import com.takoy3466.manaitamtk.network.PacketisKillAll;
import com.takoy3466.manaitamtk.util.WeaponUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.*;
import java.util.function.Predicate;

import static com.mochi_753.tconstructmtk.common.capabilities.props.FlySpeedMode.FIRST_TEXT;

public class TConstructMTKEventHandler {

    public static final List<EquipmentSlot> ARMOR_SLOTS = new ArrayList<>(List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));

    public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(IArmorMode.class);
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        cancelLivingEvent(event);
    }

    public static void onLivingAttack(LivingAttackEvent event) {
        cancelLivingEvent(event);
    }

    public static void onPlayerDamage(LivingDamageEvent event) {
        cancelLivingEvent(event);
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        cancelLivingEvent(event);
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = player.level();
        InteractionHand hand = event.getHand();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack stack = serverPlayer.getItemInHand(hand);

        if (!level.isClientSide() && stack.getItem() instanceof IModifiable) {
            double radius = (double) MTKConfig.SWORD_KILL_RADIUS.get() * 100;

            MTKCapabilityHelper.execute(com.takoy3466.manaitamtk.capability.MTKCapabilities.KILL_SWORD, player, hand, iKillSword -> {
                Class<LivingEntity> entityClass = LivingEntity.class;
                Predicate<LivingEntity> allEntity = entity -> (entity != player) && (entity instanceof LivingEntity);
                Predicate<LivingEntity> onlyEnemy = entity -> (entity != player) && (entity instanceof Enemy);

                List<LivingEntity> targets = iKillSword.isKillAll() ?
                        WeaponUtil.selectTargets(entityClass, level, player, radius/10, allEntity)
                        : WeaponUtil.selectTargets(entityClass, level, player, radius, onlyEnemy);

                iKillSword.kill(targets, level, player);

                if (!targets.isEmpty()) {
                    WeaponUtil.RightClickTrigger(stack);
                }
            });
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if(!player.level().isClientSide()){
            ArmorMode.getCurrentArmorMode(player).ifPresent(currentArmorMode -> {
                if(currentArmorMode == ArmorMode.ENABLED){
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20*20, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20*20, 0));
                    player.onUpdateAbilities();
                }

                FlySpeedMode.getCurrentFlySpeedMode(player).ifPresent(flySpeedMode -> {
                    player.getAbilities().setFlyingSpeed(flySpeedMode.getSpeed());
                    player.onUpdateAbilities();
                });
            });
        }
    }

    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return;
            ItemStack previous = event.getFrom();
            ItemStack current = event.getTo();
            if (previous.getItem() instanceof IModifiable && previous.getCapability(MTKCapabilities.FLY).isPresent()) {
                if (!current.getCapability(MTKCapabilities.FLY).isPresent()) {
                    boolean atLeastOneManaita = ARMOR_SLOTS.stream()
                            .filter(armorSlot -> armorSlot != current.getEquipmentSlot())
                            .anyMatch(armorSlot -> player.getItemBySlot(armorSlot).getCapability(ArmorMTKCapability.ARMOR_MODE_CAPABILITY).isPresent());
                    if (!atLeastOneManaita) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();

                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.teleport(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                                    serverPlayer.getYRot(), serverPlayer.getXRot());
                        }
                    }
                }
            }else {
                if (current.getItem() instanceof IModifiable && current.getCapability(MTKCapabilities.FLY).isPresent()) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if (event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null){
            //sword
            ItemStack mainHandStack = player.getMainHandItem();
            if (MTKKeyMappings.SwitchExterminationKey.consumeClick() && Objects.requireNonNull(player).isSteppingCarefully()) {
                    mainHandStack.getCapability(MTKCapabilities.KILL_SWORD).ifPresent(iKillSword -> {
                        iKillSword.setIsKillAll(!iKillSword.isKillAll());
                        MTKNetwork.sendToServer(new PacketisKillAll(iKillSword.isKillAll()));
                    });
            }

            //armor
            if (MTKKeyMappings.HelmetKey.consumeClick()){
                if(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof HelmetManaita helmetManaita){
                    ManaitaHelmetAccessor manaitaHelmetAccessor = (ManaitaHelmetAccessor) helmetManaita;
                    ManaitaHelmetAccessor.setModeNumber(manaitaHelmetAccessor.invokeModeChange(ManaitaHelmetAccessor.getModeNumber(), 1));
                    player.displayClientMessage(Component.literal("MODE :" + manaitaHelmetAccessor.invokeModeName()),true);
                } else {
                    ArmorMode.adjustArmorMode(player);
                }
            }

            if (MTKKeyMappings.FlySpeedKey.consumeClick()) {
                if(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof HelmetManaita helmetManaita){
                    ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
                    ManaitaHelmetAccessor manaitaHelmetAccessor = (ManaitaHelmetAccessor) helmetManaita;
                    manaitaHelmetAccessor.setFlySpeedNumber(manaitaHelmetAccessor.invokeModeChange(manaitaHelmetAccessor.getFlySpeedNumber(), 3));
                    switch (manaitaHelmetAccessor.getFlySpeedNumber()) {
                        case 1 -> helmetManaita.setFlySpeed(stack, 0.1f);
                        case 2 -> helmetManaita.setFlySpeed(stack, 0.2f);
                        case 3 -> helmetManaita.setFlySpeed(stack, 0.6f);
                        default -> helmetManaita.setFlySpeed(stack, 0.05f);
                    }
                    String s =  switch (manaitaHelmetAccessor.getFlySpeedNumber()) {
                        case 1 -> "0.1";
                        case 2 -> "0.2";
                        case 3 -> "0.6";
                        default -> "0.05";
                    };
                    player.displayClientMessage(Component.literal(FIRST_TEXT.getString() + s),true);
                } else {
                    FlySpeedMode.adjustFlySpeedMode(player);
                }
            }
        }
    }

    public static void cancelLivingEvent(LivingEvent event) {
        if (event.getEntity() instanceof Player player) {
            ARMOR_SLOTS.forEach(equipmentSlot -> {
                if (player.getItemBySlot(equipmentSlot).getItem() instanceof IModifiable) {
                    MTKCapabilityHelper.execute(MTKCapabilities.INVINCIBLE, player, equipmentSlot, iInvincible -> {
                        event.setCanceled(true);
                    });
                }
            });
        }
    }
}
