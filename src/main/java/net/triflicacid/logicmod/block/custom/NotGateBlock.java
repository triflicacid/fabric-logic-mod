package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class NotGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "not_gate_block";
    public static final String ITEM_NAME = "not_gate";

    public NotGateBlock() {
        super(facing -> new Direction[] { facing }, true);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        return Util.logicalNot(this.areInputsRecievingPower(world, pos, state));
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
