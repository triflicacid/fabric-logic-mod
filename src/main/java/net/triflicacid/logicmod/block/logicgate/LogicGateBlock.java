package net.triflicacid.logicmod.block.logicgate;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.AbstractBooleanBlock;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.triflicacid.logicmod.util.Util.specialToText;

/**
 * Represent a logic gate which outputs a boolean depending on the application of a function to its inputs.
 *
 * Accepts anywhere between a given minimum/maximum number of inputs
 */
public abstract class LogicGateBlock extends AbstractBooleanBlock implements AdvancedWrenchable, Analysable {
    public static final int TICK_DELAY = 2;
    private final int minInputs;
    private final int maxInputs;

    public LogicGateBlock(int inputCount, boolean initiallyActive) {
        this(inputCount, inputCount, initiallyActive);
    }

    public LogicGateBlock(int minInputs, int maxInputs, boolean initiallyActive) {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true, initiallyActive);
        if (minInputs < 1 || minInputs > 3) {
            throw new IllegalStateException("Invalid number of minInputs to logic gate: " + minInputs);
        }
        if (maxInputs < 1 || maxInputs > 3) {
            throw new IllegalStateException("Invalid number of maxInputs to logic gate: " + maxInputs);
        }
        if (maxInputs < minInputs) {
            throw new IllegalStateException("Invalid relation of maxInputs to minInputs to logic gate: " + minInputs + " and " + maxInputs);
        }
        this.minInputs = minInputs;
        this.maxInputs = maxInputs;
    }

    public abstract boolean logicalFunction(boolean[] inputs);

    /** Return the string type of logic gate to be displayed to the player */
    public abstract String getType();

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

        int i = 0;
        for (Direction direction : directions) {
            receiving[i++] = isReceivingFrom(world, pos, state, direction);
        }

        while (i < minInputs) {
            receiving[i++] = false;
        }

        return receiving;
    }

    /** Get input from a direction */
    public boolean isReceivingFrom(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);

        if (dstState.getBlock() instanceof LogicGateBlock && direction != state.get(FACING)) {
            return isReceivingFrom(world, dstPos, dstState, direction);
        } else {
            return dstState.getStrongRedstonePower(world, dstPos, direction) > 0;
        }
    }

    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        boolean isActive = state.get(ACTIVE);
        boolean shouldBeActive = logicalFunction(areInputsReceivingPower(world, pos, state));

        if (isActive != shouldBeActive) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive));
        }
    }

    /** Is this gate a not variant? E.g. "nor" is the not variant of "or" */
    public abstract boolean isNotVariant();

    /** Return the inverse logic gate block */
    public abstract LogicGateBlock getInverse();

    /** Toggle between inverse logic gate type */
    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            LogicGateBlock inverse = this.getInverse();
            if (inverse == null) {
                return null;
            } else {
                return inverse.getDefaultState().with(FACING, state.get(FACING));
            }
        } else {
            return null;
        }
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = super.onAnalyse(world, pos, state, side, playerFacing);
        messages.add(Text.literal("Gate type: ").append(specialToText(getType())));
        return messages;
    }
}
