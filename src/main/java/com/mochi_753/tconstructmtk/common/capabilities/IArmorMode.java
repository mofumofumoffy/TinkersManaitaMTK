package com.mochi_753.tconstructmtk.common.capabilities;

import com.mochi_753.tconstructmtk.common.capabilities.props.ArmorMode;
import com.mochi_753.tconstructmtk.common.capabilities.props.FlySpeedMode;

public interface IArmorMode {
    ArmorMode getArmorMode();
    FlySpeedMode getFlySpeedMode();

    void setArmorMode(int index);
    void setFlySpeedMode(int index);

    default void setArmorMode(ArmorMode mode){
        setArmorMode(mode.getIndex());
    }

    default void setFlySpeedMode(FlySpeedMode mode){
        setFlySpeedMode(mode.getIndex());
    }
}
