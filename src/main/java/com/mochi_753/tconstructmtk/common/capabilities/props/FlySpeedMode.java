package com.mochi_753.tconstructmtk.common.capabilities.props;

import com.mochi_753.tconstructmtk.TConstructMTK;
import com.mochi_753.tconstructmtk.common.capabilities.ArmorMTKCapability;
import com.mochi_753.tconstructmtk.common.network.SyncArmorModePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.Comparator;
import java.util.Optional;

import static com.mochi_753.tconstructmtk.common.event.TConstructMTKEventHandler.ARMOR_SLOTS;

public enum FlySpeedMode{
    NOTHING(-1, 0f, "0"),
    SLOW(0, 0.05f, "0.05"),
    NORMAL(1, 0.1f, "0.1"),
    FAST(2, 0.2f, "0.2"),
    EXTREME(3, 0.6f, "0.6");

    public static final Component FIRST_TEXT = Component.translatable("item.manaitamtk.fwai.first_text");

    private final int index;
    private final float speed;
    private final String context;

    FlySpeedMode(int index, float speed, String context) {
        this.index = index;
        this.speed = speed;
        this.context = context;
    }

    public int getIndex() {
        return index;
    }

    public float getSpeed() {
        return speed;
    }

    public String getContext() {
        return context;
    }

    public static FlySpeedMode byIndex(int index){
        return switch (index){
            case 0 -> SLOW;
            case 1 -> NORMAL;
            case 2 -> FAST;
            case 3 -> EXTREME;
            default -> NOTHING;
        };
    }

    public static FlySpeedMode getNext(FlySpeedMode mode){
        return switch (mode.getIndex()){
            case 0 -> NORMAL;
            case 1 -> FAST;
            case 2 -> EXTREME;
            case 3 -> SLOW;
            default -> NOTHING;
        };
    }

    public static Optional<FlySpeedMode> getCurrentFlySpeedMode(Player player){
        return ARMOR_SLOTS.stream().map(equipmentSlot -> {
            ItemStack stack = player.getItemBySlot(equipmentSlot);
            if(stack.getItem() instanceof IModifiable){
                var flySpeedLazyOptional = stack.getCapability(ArmorMTKCapability.ARMOR_MODE_CAPABILITY);
                if(flySpeedLazyOptional.isPresent()){
                    return flySpeedLazyOptional.orElseThrow(IllegalStateException::new).getFlySpeedMode();
                }
            }
            return FlySpeedMode.NOTHING;
        }).max(Comparator.comparingInt(FlySpeedMode::getIndex));
    }

    public static void adjustFlySpeedMode(Player player){
        getCurrentFlySpeedMode(player).ifPresent(currentFlySpeedMode -> {
            if(currentFlySpeedMode != FlySpeedMode.NOTHING){
                FlySpeedMode newMode = getNext(currentFlySpeedMode);

                ARMOR_SLOTS.forEach(equipmentSlot -> {
                    ItemStack stack = player.getItemBySlot(equipmentSlot);
                    stack.getCapability(ArmorMTKCapability.ARMOR_MODE_CAPABILITY).ifPresent(iArmorMode -> {
                        if(iArmorMode.getFlySpeedMode() != FlySpeedMode.NOTHING){
                            iArmorMode.setFlySpeedMode(newMode);
                            TConstructMTK.CHANNEL.sendToServer(new SyncArmorModePacket(equipmentSlot, iArmorMode.getArmorMode(), newMode));
                        }
                    });
                });

                player.displayClientMessage(Component.literal(FIRST_TEXT.getString() + newMode.getContext()), true);
            }
        });
    }
}
