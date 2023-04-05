package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class BlueWireBlock extends WireBlock {
    public static final WireColor COLOR = WireColor.BLUE;
    public static final String NAME = getName(COLOR);

    public BlueWireBlock() {
        super(COLOR);
    }
}
