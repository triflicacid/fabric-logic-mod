package net.triflicacid.logicmod.block.custom;


import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

public class ConstantLoBlock extends SignalEmitterBlock implements AdvancedWrenchable {
    public static final String NAME = "constant_lo";

    public ConstantLoBlock() {
        super(false);
    }

    @Override
    public int getSignalStrength() {
        return 0;
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        return ModBlocks.CONSTANT_HI.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(HorizontalFacingBlock.FACING));
    }
}
