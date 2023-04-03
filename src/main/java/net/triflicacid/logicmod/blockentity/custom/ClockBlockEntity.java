package net.triflicacid.logicmod.blockentity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class ClockBlockEntity extends AbstractClockBlockEntity {
    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        setOnTickCount(20);
        setOffTickCount(20);
    }
}
