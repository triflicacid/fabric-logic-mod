package net.triflicacid.logicmod.block.custom.logicgate;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.SignalIOBlock;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;

import java.util.*;

import static net.triflicacid.logicmod.util.Util.specialToText;

public abstract class LogicGateBlock extends SignalIOBlock implements AdvancedWrenchable, Analysable {
    public static final int TICK_DELAY = 2;
    private final int minInputs;
    private final int maxInputs;

    public LogicGateBlock(int inputCount, boolean initiallyActive) {
        this(inputCount, inputCount, initiallyActive);
    }

    public LogicGateBlock(int minInputs, int maxInputs, boolean initiallyActive) {
        super(initiallyActive ? 15 : 0);
        if (minInputs < 1 || minInputs > 3)
            throw new IllegalStateException("Invalid number of minInputs to logic gate: " + minInputs);
        if (maxInputs < 1 || maxInputs > 3)
            throw new IllegalStateException("Invalid number of maxInputs to logic gate: " + maxInputs);
        if (maxInputs < minInputs)
            throw new IllegalStateException("Invalid relation of maxInputs to minInputs to logic gate: " + minInputs + " and " + maxInputs);
        this.minInputs = minInputs;
        this.maxInputs = maxInputs;
    }

    public abstract boolean logicalFunction(boolean[] inputs);

    public abstract String getType();

    @Override
    public final int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return logicalFunction(this.areInputsReceivingPower(world, pos, state)) ? 15 : 0;
    }

    /** Return array indicating if each direction returned by getInputDirections is receiving power */
    public boolean[] areInputsReceivingPower(World world, BlockPos pos, BlockState state) {
        Direction dirFacing = state.get(FACING);
        Direction dirLeft = dirFacing.rotateYCounterclockwise();
        Direction dirRight = dirFacing.rotateYClockwise();
        Set<Direction> directions = new HashSet<>();

        BlockState stateFacing = world.getBlockState(pos.offset(dirFacing));
        if (minInputs == 1 && maxInputs == 1) {
            directions.add(dirFacing);
        } else {
            BlockState stateLeft = world.getBlockState(pos.offset(dirLeft));
            BlockState stateRight = world.getBlockState(pos.offset(dirRight));
            if (minInputs == 2 && maxInputs == 2) {
                directions.add(dirLeft);
                directions.add(dirRight);
            } else if (minInputs == 3 && maxInputs == 3) {
                directions.add(dirFacing);
                directions.add(dirLeft);
                directions.add(dirRight);
            } else {
                if (!stateFacing.isAir()) directions.add(dirFacing);
                if (!stateLeft.isAir()) directions.add(dirLeft);
                if (!stateRight.isAir()) directions.add(dirRight);
            }
        }

        boolean[] receiving = new boolean[Math.max(directions.size(), minInputs)];

        Iterator<Direction> it = directions.iterator();
        int i = 0;
        while (it.hasNext()) {
            receiving[i++] = getPower(world, pos, state, it.next()) > 0;
        }

        while (i < minInputs) {
            receiving[i++] = false;
        }

        return receiving;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return TICK_DELAY;
    }

    public abstract boolean isNotVariant();

    public abstract LogicGateBlock getInverse();

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        LogicGateBlock inverse = this.getInverse();
        if (inverse == null)
            return null;
        player.sendMessage(Text.literal("Set gate type to ").append(specialToText(inverse.getType())));
        return inverse.getDefaultState().with(FACING, state.get(FACING));
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        player.sendMessage(Text.literal("Gate type: ").append(specialToText(getType())));
    }
}
