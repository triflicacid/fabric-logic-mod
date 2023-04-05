package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class BufferGateBlock extends LogicGateBlock {
    public static final String NAME = "buffer_gate";

    public BufferGateBlock() {
        super(facing -> new Direction[] { facing }, false);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        return Util.logicalBuffer(this.areInputsRecievingPower(world, pos, state));
    }

    @Override
    public boolean isNotVariant() {
        return false;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.NOT_GATE;
    }
}
