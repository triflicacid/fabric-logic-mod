package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class XorGateBlock extends LogicGateBlock {
    public static final String NAME = "xor_gate";

    public XorGateBlock() {
        super(facing -> new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() }, false);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        return Util.logicalXor(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return false;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.XNOR_GATE;
    }
}
