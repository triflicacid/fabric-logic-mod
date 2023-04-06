package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

public class InputBlock extends SignalEmitterBlock implements AdvancedWrenchable {
    public static final String NAME = "input";

    public InputBlock() {
        super(0);
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return state.get(POWER);
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        int newPower = state.get(POWER) + (Screen.hasShiftDown() ? (-1) : 1);
        if (newPower < 0) newPower = 15;
        else if (newPower > 15) newPower = 0;
        return state.with(POWER, newPower).with(ACTIVE, state.get(POWER) > 0);
    }
}
