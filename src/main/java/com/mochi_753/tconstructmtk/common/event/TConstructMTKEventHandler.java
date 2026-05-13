package com.mochi_753.tconstructmtk.common.event;

import com.takoy3466.manaitamtk.KeyMapping.MTKKeyMappings;
import com.takoy3466.manaitamtk.capability.MTKCapabilities;
import com.takoy3466.manaitamtk.capability.helper.MTKCapabilityHelper;
import com.takoy3466.manaitamtk.config.MTKConfig;
import com.takoy3466.manaitamtk.network.MTKNetwork;
import com.takoy3466.manaitamtk.network.PacketisKillAll;
import com.takoy3466.manaitamtk.util.WeaponUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TConstructMTKEventHandler {

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

    @OnlyIn(Dist.CLIENT)
    public static void onPlayerTick(TickEvent.ClientTickEvent event){

        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null){
            ItemStack mainHandStack = player.getMainHandItem();
            if (MTKKeyMappings.SwitchExterminationKey.consumeClick() && Objects.requireNonNull(player).isSteppingCarefully()) {
                    mainHandStack.getCapability(MTKCapabilities.KILL_SWORD).ifPresent(iKillSword -> {
                        iKillSword.setIsKillAll(!iKillSword.isKillAll());
                        MTKNetwork.sendToServer(new PacketisKillAll(iKillSword.isKillAll()));
                    });
            }
        }
    }
}
