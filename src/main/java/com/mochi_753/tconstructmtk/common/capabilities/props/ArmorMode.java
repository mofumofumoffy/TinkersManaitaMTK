package com.mochi_753.tconstructmtk.common.capabilities.props;

import com.mochi_753.tconstructmtk.TConstructMTK;
import com.mochi_753.tconstructmtk.common.capabilities.ArmorMTKCapability;
import com.mochi_753.tconstructmtk.common.network.SyncArmorModePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Optional;

import static com.mochi_753.tconstructmtk.common.event.TConstructMTKEventHandler.ARMOR_SLOTS;

public enum ArmorMode {
    NOTHING(-1, "nothing"),
    DISABLED(0, "NIGHT VISION and INVISIBILITY OFF"),
    ENABLED(1, "NIGHT VISION and INVISIBILITY ON");

    private final int index;
    private final String context;

    ArmorMode(int index, String context) {
        this.index = index;
        this.context = context;
    }

    public static ArmorMode byIndex(int index) {
        return switch (index) {
            case 1 -> ENABLED;
            case 0 -> DISABLED;
            default -> NOTHING;
        };
    }

    public static ArmorMode getInverse(ArmorMode mode) {
        return mode == ENABLED ? DISABLED : ENABLED;
    }

    public static Optional<ArmorMode> getCurrentArmorMode(Player player) {
        return ARMOR_SLOTS.stream()
                .map(equipmentSlot -> {
                    ItemStack stack = player.getItemBySlot(equipmentSlot);
                    var armorModeLazyOptional = stack.getCapability(ArmorMTKCapability.ARMOR_MODE_CAPABILITY);
                    if (armorModeLazyOptional.isPresent()) {
                        return armorModeLazyOptional.orElseThrow(IllegalStateException::new).getArmorMode();
                    }
                    return ArmorMode.NOTHING;
                }).max(Comparator.comparingInt(ArmorMode::getIndex));
    }

    public static void adjustArmorMode(Player player) {
        getCurrentArmorMode(player).ifPresent(currentMode -> {
            TConstructMTK.LOGGER.info(currentMode.getContext());
            if (currentMode != ArmorMode.NOTHING) {
                ArmorMode newMode = ArmorMode.getInverse(currentMode);

                ARMOR_SLOTS.forEach(equipmentSlot -> {
                    ItemStack stack = player.getItemBySlot(equipmentSlot);
                    stack.getCapability(ArmorMTKCapability.ARMOR_MODE_CAPABILITY).ifPresent(iArmorMode -> {
                        if (iArmorMode.getArmorMode() != ArmorMode.NOTHING) {
                            iArmorMode.setArmorMode(newMode);
                            TConstructMTK.CHANNEL.sendToServer(new SyncArmorModePacket(equipmentSlot, newMode, iArmorMode.getFlySpeedMode()));
                        }
                    });
                });

                player.displayClientMessage(Component.literal("MODE :" + newMode.getContext()), true);
            }
        });

    }

    public int getIndex() {
        return index;
    }

    public String getContext() {
        return context;
    }

}
