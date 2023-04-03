package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;

public class BufferGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "buffer_gate_block";
    public static final String ITEM_NAME = "buffer_gate";

    public BufferGateBlock() {
        super(facing -> new Direction[] { facing }, false);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        return Util.logicalBuffer(this.areInputsRecievingPower(world, pos, state));
    }
}