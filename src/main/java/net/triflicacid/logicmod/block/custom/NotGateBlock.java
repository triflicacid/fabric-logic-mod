package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NotGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "not_gate_block";
    public static final String ITEM_NAME = "not_gate";

    public NotGateBlock(Settings settings) {
        super(settings, facing -> new Direction[] { facing }, true);
    }


    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        return !inputs[0];
    }
}
