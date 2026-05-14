package com.mochi_753.tconstructmtk.common.network;

import com.mochi_753.tconstructmtk.common.capabilities.ArmorMTKCapability;
import com.mochi_753.tconstructmtk.common.capabilities.props.ArmorMode;
import com.mochi_753.tconstructmtk.common.capabilities.props.FlySpeedMode;
import com.takoy3466.manaitamtk.capability.helper.MTKCapabilityHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncArmorModePacket {
    private final EquipmentSlot slot;
    private final ArmorMode armorMode;
    private final FlySpeedMode flySpeedMode;

    public SyncArmorModePacket(EquipmentSlot slot, ArmorMode armorMode, FlySpeedMode flySpeedMode) {
        this.slot = slot;
        this.armorMode = armorMode;
        this.flySpeedMode = flySpeedMode;
    }

    public SyncArmorModePacket(FriendlyByteBuf buf){
        this.slot = buf.readEnum(EquipmentSlot.class);
        this.armorMode = buf.readEnum(ArmorMode.class);
        this.flySpeedMode = buf.readEnum(FlySpeedMode.class);
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeEnum(slot);
        buf.writeEnum(armorMode);
        buf.writeEnum(flySpeedMode);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            if (player != null) {
                MTKCapabilityHelper.execute(ArmorMTKCapability.ARMOR_MODE_CAPABILITY, player, slot, iArmorMode -> {
                    iArmorMode.setArmorMode(armorMode);
                    iArmorMode.setFlySpeedMode(flySpeedMode);
                });
            }
        });
        context.setPacketHandled(true);
    }
}
