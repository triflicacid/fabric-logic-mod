package net.triflicacid.logicmod.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/** Utilities to aid with "pipe-like" blocks, such as wires */
public class PipeModel {
    // BLOCK STATE PROPERTIES
    public static final BooleanProperty STATE_UP = BooleanProperty.of("up");
    public static final BooleanProperty STATE_DOWN = BooleanProperty.of("down");
    public static final BooleanProperty STATE_NORTH = BooleanProperty.of("north");
    public static final BooleanProperty STATE_EAST = BooleanProperty.of("east");
    public static final BooleanProperty STATE_SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty STATE_WEST = BooleanProperty.of("west");

    // DIRECTIONAL VOXEL SHAPES
    public final VoxelShape SHAPE_CENTRE;
    public final VoxelShape SHAPE_NORTH;
    public final VoxelShape SHAPE_SOUTH;
    public final VoxelShape SHAPE_WEST;
    public final VoxelShape SHAPE_EAST;
    public final VoxelShape SHAPE_DOWN;
    public final VoxelShape SHAPE_UP;

    public PipeModel(double thickness, double length) {
        float ht = (float) thickness / 2f;
        float lo = 8 - ht, hi = 8 + ht;
        SHAPE_CENTRE = Block.createCuboidShape(lo, lo, lo, hi, hi, hi);
        SHAPE_NORTH = Block.createCuboidShape(lo, lo, 0, hi, hi, lo);
        SHAPE_SOUTH = Block.createCuboidShape(lo, lo, hi, hi, hi, 16);
        SHAPE_WEST = Block.createCuboidShape(0, lo, lo, lo, hi, hi);
        SHAPE_EAST = Block.createCuboidShape(hi, lo, lo, 16, hi, hi);
        SHAPE_DOWN = Block.createCuboidShape(lo, 0, lo, hi, lo, hi);
        SHAPE_UP = Block.createCuboidShape(lo, hi, lo, hi, 16, hi);
    }

    // Thickness: 3

    /** get Voxel outline of a pipe-like block */
    public VoxelShape getVoxelShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = SHAPE_CENTRE;
        if (state.get(STATE_NORTH)) shape = VoxelShapes.union(shape, SHAPE_NORTH);
        if (state.get(STATE_SOUTH)) shape = VoxelShapes.union(shape, SHAPE_SOUTH);
        if (state.get(STATE_WEST)) shape = VoxelShapes.union(shape, SHAPE_WEST);
        if (state.get(STATE_EAST)) shape = VoxelShapes.union(shape, SHAPE_EAST);
        if (state.get(STATE_UP)) shape = VoxelShapes.union(shape, SHAPE_UP);
        if (state.get(STATE_DOWN)) shape = VoxelShapes.union(shape, SHAPE_DOWN);
        return shape;
    }

    /** add required block states to a block */
    public static void appendBlockStates(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STATE_UP, STATE_DOWN, STATE_NORTH, STATE_EAST, STATE_SOUTH, STATE_WEST);
    }

    /** get default state of pipe-like block : all directional states as FALSE */
    public static BlockState setDefaultState(BlockState state) {
        return state.with(STATE_UP, false)
                .with(STATE_DOWN, false)
                .with(STATE_NORTH, false)
                .with(STATE_SOUTH, false)
                .with(STATE_EAST, false)
                .with(STATE_WEST, false);
    }

    /** Get direction property from direction */
    public static BooleanProperty getPropertyFromDirection(Direction dir) {
        return switch (dir) {
            case DOWN -> STATE_DOWN;
            case UP -> STATE_UP;
            case NORTH -> STATE_NORTH;
            case SOUTH -> STATE_SOUTH;
            case WEST -> STATE_WEST;
            case EAST -> STATE_EAST;
        };
    }
}
