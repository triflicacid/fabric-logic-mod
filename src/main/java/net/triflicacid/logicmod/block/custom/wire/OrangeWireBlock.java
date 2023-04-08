package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class OrangeWireBlock extends WireBlock {
    public static final WireColor COLOR = WireColor.ORANGE;
    public static final String NAME = getName(COLOR);

    public OrangeWireBlock() {
        super(COLOR);
    }
}
