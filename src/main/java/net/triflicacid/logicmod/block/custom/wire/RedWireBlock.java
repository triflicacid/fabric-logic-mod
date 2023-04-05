package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class RedWireBlock extends WireBlock {
    public static final WireColor COLOR = WireColor.RED;
    public static final String NAME = getName(COLOR);

    public RedWireBlock() {
        super(COLOR);
    }
}
