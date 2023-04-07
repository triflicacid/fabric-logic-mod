package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

public class OutputBlock extends SignalIOBlock {
    public static final String NAME = "output";

    public OutputBlock() {
        super(0);
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return getPower(world, pos, state, state.get(FACING));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }
}
