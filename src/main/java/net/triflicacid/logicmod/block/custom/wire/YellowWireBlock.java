package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class YellowWireBlock extends WireBlock {
    public static final WireColor COLOR = WireColor.YELLOW;
    public static final String NAME = getName(COLOR);

    public YellowWireBlock() {
        super(COLOR);
    }
}
