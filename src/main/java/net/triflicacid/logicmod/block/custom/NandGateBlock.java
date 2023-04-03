package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.Util;
import net.triflicacid.logicmod.block.ModBlocks;

public class NandGateBlock extends LogicGateBlock {
    public static final String BLOCK_NAME = "nand_gate_block";
    public static final String ITEM_NAME = "nand_gate";

    public NandGateBlock() {
        super(facing -> new Direction[] { facing.rotateYClockwise(), facing.rotateYCounterclockwise() }, true);
    }

    @Override
    protected boolean shouldBeActive(World world, BlockPos pos, BlockState state) {
        boolean[] inputs = this.areInputsRecievingPower(world, pos, state);
        return Util.logicalNand(inputs);
    }

    @Override
    public boolean isNotVariant() {
        return true;
    }

    @Override
    public LogicGateBlock getInverse() {
        return ModBlocks.AND_GATE;
    }
}
