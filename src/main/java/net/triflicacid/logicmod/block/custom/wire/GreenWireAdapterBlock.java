package net.triflicacid.logicmod.block.custom.wire;

import net.triflicacid.logicmod.util.WireColor;

public class GreenWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.GREEN;
    public static final String NAME = getName(COLOR);

    public GreenWireAdapterBlock() {
        super(COLOR);
    }
}
