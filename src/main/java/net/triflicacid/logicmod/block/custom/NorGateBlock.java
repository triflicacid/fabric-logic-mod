package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;

public class NorGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "nor_gate_block";
    public static final String ITEM_NAME = "nor_gate";

    public NorGateBlock() {
        super(facing -> new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() }, true);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        return Util.logicalNor(inputs);
    }
}
