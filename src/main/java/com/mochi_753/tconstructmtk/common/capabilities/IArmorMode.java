package com.mochi_753.tconstructmtk.common.capabilities;

import com.mochi_753.tconstructmtk.common.capabilities.props.ArmorMode;
import com.mochi_753.tconstructmtk.common.capabilities.props.FlySpeedMode;

public interface IArmorMode {
    ArmorMode getArmorMode();

    void setArmorMode(int index);

    default void setArmorMode(ArmorMode mode) {
        setArmorMode(mode.getIndex());
    }

    FlySpeedMode getFlySpeedMode();

    void setFlySpeedMode(int index);

    default void setFlySpeedMode(FlySpeedMode mode) {
        setFlySpeedMode(mode.getIndex());
    }
}
