package net.triflicacid.logicmod.block.custom.adapter;

import net.triflicacid.logicmod.util.WireColor;

public class OrangeWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.ORANGE;
    public static final String NAME = getName(COLOR);

    public OrangeWireAdapterBlock() {
        super(COLOR);
    }
}
