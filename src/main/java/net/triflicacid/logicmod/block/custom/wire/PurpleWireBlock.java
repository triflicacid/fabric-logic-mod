package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class PurpleWireBlock extends WireBlock {
    public static final WireColor COLOR = WireColor.PURPLE;
    public static final String NAME = getName(COLOR);

    public PurpleWireBlock() {
        super(COLOR);
    }
}
