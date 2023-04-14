package net.triflicacid.logicmod.block.custom.adapter;

import net.triflicacid.logicmod.util.WireColor;

public class BlueWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.BLUE;
    public static final String NAME = getName(COLOR);

    public BlueWireAdapterBlock() {
        super(COLOR);
    }
}
