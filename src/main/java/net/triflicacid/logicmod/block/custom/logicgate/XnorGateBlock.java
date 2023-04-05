package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class XnorGateBlock extends LogicGateBlock {
    public static final String NAME = "xnor_gate";

    public XnorGateBlock() {
        super(facing -> new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() }, true);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        return Util.logicalXnor(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.XOR_GATE;
    }
}
