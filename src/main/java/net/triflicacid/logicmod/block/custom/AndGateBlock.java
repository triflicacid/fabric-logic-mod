package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AndGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "and_gate_block";
    public static final String ITEM_NAME = "and_gate";

    public AndGateBlock(Settings settings) {
        super(settings, facing -> new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() }, false);
    }


    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        for (boolean input : inputs)
            if (!input)
                return false;
        return true;
    }
}
