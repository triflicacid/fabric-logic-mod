package net.triflicacid.logicmod.block.custom.adapter;

import net.triflicacid.logicmod.block.custom.adapter.WireAdapterBlock;
import net.triflicacid.logicmod.util.WireColor;

public class PurpleWireAdapterBlock extends WireAdapterBlock {
    public static final WireColor COLOR = WireColor.PURPLE;
    public static final String NAME = getName(COLOR);

    public PurpleWireAdapterBlock() {
        super(COLOR);
    }
}