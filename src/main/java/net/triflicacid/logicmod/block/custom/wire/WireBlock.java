package net.triflicacid.logicmod.block.custom.wire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.interfaces.WireConnectable;
import net.triflicacid.logicmod.util.WireColor;
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * The concrete implementation of AbstractWireBlock
 */
public class WireBlock extends AbstractWireBlock implements Analysable, AdvancedWrenchable {
    public static final VoxelShape SHAPE_CENTRE = Block.createCuboidShape(6.5, 6.5, 6.5, 9.5, 9.5, 9.5);
    public static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(6.5, 6.5, 0, 9.5, 9.5, 6.5);
    public static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(6.5, 6.5, 9.5, 9.5, 9.5, 16);
    public static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0, 6.5, 6.5, 6.5, 9.5, 9.5);
    public static final VoxelShape SHAPE_EAST = Block.createCuboidShape(9.5, 6.5, 6.5, 16, 9.5, 9.5);
    public static final VoxelShape SHAPE_DOWN = Block.createCuboidShape(6.5, 0, 6.5, 9.5, 6.5, 9.5);
    public static final VoxelShape SHAPE_UP = Block.createCuboidShape(6.5, 9.5, 6.5, 9.5, 16, 9.5);

    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST = BooleanProperty.of("west");

    protected WireBlock(WireColor color) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).breakInstantly(), color);
        this.setDefaultState(this.getDefaultState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = SHAPE_CENTRE;
        if (state.get(NORTH)) shape = VoxelShapes.union(shape, SHAPE_NORTH);
        if (state.get(SOUTH)) shape = VoxelShapes.union(shape, SHAPE_SOUTH);
        if (state.get(WEST)) shape = VoxelShapes.union(shape, SHAPE_WEST);
        if (state.get(EAST)) shape = VoxelShapes.union(shape, SHAPE_EAST);
        if (state.get(UP)) shape = VoxelShapes.union(shape, SHAPE_UP);
        if (state.get(DOWN)) shape = VoxelShapes.union(shape, SHAPE_DOWN);
        return shape;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(POWER) > 0;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (hasRandomTicks(state)) {
            spawnRedstoneParticles(world, pos);
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    /** Get the name of a wire block of a given color */
    public static String getName(WireColor color) {
        return color + "_wire";
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Color: ").append(Text.literal(color.toString()).formatted(color.getFormatting())));
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        }
    }

    /** Get direction property from direction */
    protected BooleanProperty getPropertyFromDirection(Direction dir) {
        return switch (dir) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    /** Should we be connected to the block in the given direction */
    private boolean shouldConnect(World world, BlockPos pos, Direction dir) {
        BlockState dstState = world.getBlockState(pos.offset(dir));
        return dstState.getBlock() instanceof WireConnectable connectable && connectable.shouldWireConnect(getWireColor());
    }

    /** Update model's direction states from every direction */
    protected void updateModel(World world, BlockState state, BlockPos pos) {
        boolean different = false;
        for (Direction dir : Direction.values()) {
            BooleanProperty prop = getPropertyFromDirection(dir);
            boolean bl = shouldConnect(world, pos, dir);
            if (bl != state.get(prop)) {
                state = state.with(prop, bl);
                different = true;
            }
        }

        if (different) {
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        }
    }

    /** Update model's states from one direction */
    protected void updateModel(World world, BlockState state, BlockPos pos, Direction dir) {
        BooleanProperty prop = getPropertyFromDirection(dir);
        boolean bl = shouldConnect(world, pos, dir);
        if (bl != state.get(prop)) {
            world.setBlockState(pos, state.with(prop, bl), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
        super.appendProperties(builder);
    }

    /** Return class instance for a wire of said color */
    public static WireBlock instantiate(WireColor color) {
        return new WireBlock(color) {};
    }

    @Override
    public @Nullable BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        updateModel(world, state, pos);
        return null;
    }
}
