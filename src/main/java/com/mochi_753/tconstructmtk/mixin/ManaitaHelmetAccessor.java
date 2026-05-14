package com.mochi_753.tconstructmtk.mixin;

import com.takoy3466.manaitamtk.item.armor.HelmetManaita;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = HelmetManaita.class, remap = false)
public interface ManaitaHelmetAccessor {

    @Accessor("modeNumber")
    static int getModeNumber(){
        throw new AssertionError();
    };

    @Accessor("modeNumber")
    static void setModeNumber(int modeNumber){
        throw new AssertionError();
    };

    @Accessor("flySpeed")
    int getFlySpeedNumber();

    @Accessor("flySpeed")
    void setFlySpeedNumber(int FlySpeedNumber);

    @Invoker("modeChange")
    int invokeModeChange(int select, int maxMode);

    @Invoker("modeName")
    String invokeModeName();
}
