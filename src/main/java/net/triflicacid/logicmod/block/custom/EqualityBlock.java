package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.util.Util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EqualityBlock extends SignalIOBlock {
    public static final String NAME = "equality";

    public EqualityBlock() {
        super(0);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        Set<Direction> directions = new HashSet<>();
        Direction facing = state.get(FACING);
        directions.add(facing.rotateYClockwise());
        directions.add(facing.rotateYCounterclockwise());
        if (world.getBlockState(pos.offset(facing)).emitsRedstonePower()) directions.add(facing);

        int[] inputs = new int[directions.size()];
        int i = 0;
        Iterator<Direction> it = directions.iterator();
        while (it.hasNext())
            inputs[i++] = getPower(world, pos, state, it.next());

        return Util.allEqual(inputs) ? inputs[0] : 0;
    }
}
