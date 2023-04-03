package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;

public class AndGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "and_gate_block";
    public static final String ITEM_NAME = "and_gate";

    public AndGateBlock() {
        super(facing -> new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() }, false);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        return Util.logicalAnd(inputs);
    }
}