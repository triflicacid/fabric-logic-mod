package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class NotGateBlock extends LogicGateBlock {
    public static final String NAME = "not_gate";

    public NotGateBlock() {
        super(1, true);
    }

    @Override
    public boolean logicalFunction(boolean[] inputs) {
        return Util.logicalNot(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.BUFFER_GATE;
    }
}
