package net.triflicacid.logicmod.block.custom;


import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

public class ConstantLoBlock extends SignalEmitterBlock implements AdvancedWrenchable {
    public static final String BLOCK_NAME = "constant_lo_block";
    public static final String ITEM_NAME = "constant_lo";

    public ConstantLoBlock() {
        super(0, true);
    }

    @Override
    public BlockState applyAdvancedWrench(BlockState state, boolean holdingShift) {
        return ModBlocks.CONSTANT_HI.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(HorizontalFacingBlock.FACING));
    }
}
