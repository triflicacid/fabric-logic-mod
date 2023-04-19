package net.triflicacid.logicmod.block.custom.adapter;

import net.triflicacid.logicmod.util.WireColor;

public class YellowWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.YELLOW;
    public static final String NAME = getName(COLOR);

    public YellowWireAdapterBlock() {
        super(COLOR);
    }
}
