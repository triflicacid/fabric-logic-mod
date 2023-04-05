package net.triflicacid.logicmod.block.custom.wire;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Wrenchable;
import net.triflicacid.logicmod.util.DirectionState;
import net.triflicacid.logicmod.util.WireColor;

import java.util.HashSet;
import java.util.Set;

public abstract class WireAdapterBlock extends AbstractWireBlock implements Wrenchable, AdvancedWrenchable {
    public static final EnumProperty<DirectionState> DOWN = EnumProperty.of("down", DirectionState.class);
    public static final EnumProperty<DirectionState> UP = EnumProperty.of("up", DirectionState.class);
    public static final EnumProperty<DirectionState> NORTH = EnumProperty.of("north", DirectionState.class);
    public static final EnumProperty<DirectionState> SOUTH = EnumProperty.of("south", DirectionState.class);
    public static final EnumProperty<DirectionState> WEST = EnumProperty.of("west", DirectionState.class);
    public static final EnumProperty<DirectionState> EAST = EnumProperty.of("east", DirectionState.class);

    public WireAdapterBlock(WireColor color) {
        super(color);
    }

    /** Get power being received in a given direction */
    protected int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        int i = world.getEmittedRedstonePower(blockPos, direction);
        if (i >= 15) {
            return i;
        } else {
            BlockState blockState = world.getBlockState(blockPos);
            return Math.max(i, blockState.isOf(Blocks.REDSTONE_WIRE) ? blockState.get(RedstoneWireBlock.POWER) : 0);
        }
    }

    private Set<Direction> getDirectionsWithState(BlockState state, DirectionState directionState) {
        Set<Direction> directions = new HashSet<>();

        if (state.get(UP) == directionState)
            directions.add(Direction.UP);
        if (state.get(DOWN) == directionState)
            directions.add(Direction.DOWN);
        if (state.get(NORTH) == directionState)
            directions.add(Direction.NORTH);
        if (state.get(SOUTH) == directionState)
            directions.add(Direction.SOUTH);
        if (state.get(WEST) == directionState)
            directions.add(Direction.WEST);
        if (state.get(EAST) == directionState)
            directions.add(Direction.EAST);

        return directions;
    }

    @Override
    protected int getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        int power = 0;

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            if (wireBlock.getWireColor() == getWireColor()) {
                power = knownBlocks.contains(dstPos) ? dstState.get(POWER) : wireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            } else if (wireBlock instanceof WireAdapterBlock adapterBlock) {
                if (this.isInput(srcState, direction) && adapterBlock.isOutput(dstState, direction.getOpposite())) {
                    power = knownBlocks.contains(dstPos) ? dstState.get(POWER) : wireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
                }
            }
        } else if (isInput(srcState, direction)) {
            power = getPower(world, srcPos, srcState, direction);
        }

        return power;
    }

    public static final EnumProperty<DirectionState> getDirectionState(Direction direction) {
        switch (direction) {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
        }
        throw new IllegalStateException("Could not resolve direction to directionState: " + direction);
    }

    public boolean isInput(BlockState state, Direction direction) {
        EnumProperty<DirectionState> directionState = getDirectionState(direction);
        return directionState == null || !state.isOf(this) ? false : state.get(directionState) == DirectionState.INPUT;
    }

    public boolean isOutput(BlockState state, Direction direction) {
        EnumProperty<DirectionState> directionState = getDirectionState(direction);
        return directionState == null || !state.isOf(this) ? false : state.get(directionState) == DirectionState.OUTPUT;
    }

    @Override
    public BlockState applyWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        Direction.Axis axis = side.getAxis();
        EnumProperty<DirectionState>[] directions = null;

        if (axis == Direction.Axis.X || axis == Direction.Axis.Z) {
            switch (playerFacing) {
                case NORTH:
                    directions = new EnumProperty[] { NORTH, EAST, SOUTH, WEST };
                    break;
                case SOUTH:
                    directions = new EnumProperty[] { SOUTH, WEST, NORTH, EAST };
                    break;
                case WEST:
                    directions = new EnumProperty[] { WEST, NORTH, EAST, SOUTH };
                    break;
                case EAST:
                    directions = new EnumProperty[] { EAST, SOUTH, WEST, NORTH };
                    break;
            }
        } else if (axis == Direction.Axis.Y) {
            switch (playerFacing) {
                case NORTH:
                    directions = new EnumProperty[] { NORTH, UP, SOUTH, DOWN };
                    break;
                case SOUTH:
                    directions = new EnumProperty[] { SOUTH, UP, NORTH, DOWN };
                    break;
                case WEST:
                    directions = new EnumProperty[] { WEST, UP, EAST, DOWN };
                    break;
                case EAST:
                    directions = new EnumProperty[] { EAST, UP, WEST, DOWN };
                    break;
            }
        }

        return directions == null ? null : rotateProperties(state, directions);
    }

    protected BlockState rotateProperties(BlockState state, EnumProperty<DirectionState>[] properties) {
        DirectionState[] states = new DirectionState[properties.length];
        for (int i = 0; i < states.length; i++) {
            states[i] = state.get(properties[i]);
        }

        for (int i = 1; i < states.length; i++) {
            state = state.with(properties[i], states[i - 1]);
        }
        state = state.with(properties[0], states[states.length - 1]);

        return state;
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        world.scheduleBlockTick(pos, this, 1, TickPriority.NORMAL);
        return state.cycle(getDirectionState(side));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return isOutput(state, direction.getOpposite()) ? state.get(POWER) : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, WEST, EAST);
        super.appendProperties(builder);
    }

    protected static final String getBlockName(WireColor color) {
        return color + "_wire_adapter_block";
    }

    protected static final String getItemName(WireColor color) {
        return color + "_wire_adapter";
    }
}
