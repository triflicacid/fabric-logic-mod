package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

import java.util.function.Function;

public abstract class LogicGateBlock extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    protected Function<Direction, Direction[]> getInputDirections; // Given current direction block is facing in, return array of directions we accept as inputs

    public LogicGateBlock(Function<Direction, Direction[]> getInputDirections, boolean active) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).breakInstantly());
        this.getInputDirections = getInputDirections;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(ACTIVE, active));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(FACING) == direction && state.get(ACTIVE) ? 15 : 0;
    }

    /** Are we recieving power from any direction? */
    protected boolean recievingAnyPower(World world, BlockPos pos, BlockState state) {
        Direction[] directions = this.getInputDirections.apply(state.get(FACING));
        for (Direction direction : directions)
            if (this.getPower(world, pos, state, direction) > 0)
                return true;
        return false;
    }

    /** Are we recieving power from the given direction? */
    protected boolean recievingAnyPower(World world, BlockPos pos, BlockState state, Direction direction) {
        return this.getPower(world, pos, state, direction) > 0;
    }

    /** Get power being recieved in a given direction */
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

    /** Return array indicating if each direction returned by getInputDirections is recieving power */
    public boolean[] areInputsRecievingPower(World world, BlockPos pos, BlockState state) {
        Direction[] directions = this.getInputDirections.apply(state.get(FACING));
        boolean[] recieving = new boolean[directions.length];

        for (int i = 0; i < directions.length; i++) {
            recieving[i] = this.getPower(world, pos, state, directions[i]) > 0;
        }

        return recieving;
    }

    /** Should ACTIVE be set to true? */
    protected abstract boolean shouldBeActive(World world, BlockPos pos, BlockState state) ;

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (state.canPlaceAt(world, pos)) {
            this.updateActive(world, pos, state);
        } else {
            BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            dropStacks(state, world, pos, blockEntity);
            world.removeBlock(pos, false);

            for (Direction direction : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
        }
    }

    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    protected void updateActive(World world, BlockPos pos, BlockState state) {
        boolean active = state.get(ACTIVE);
        boolean shouldBeActive = this.shouldBeActive(world, pos, state);

        if (active != shouldBeActive && !world.getBlockTickScheduler().isTicking(pos, this)) {
            TickPriority tickPriority = active ? TickPriority.VERY_HIGH : TickPriority.HIGH;
            world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), tickPriority);
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (this.recievingAnyPower(world, pos, state)) {
            world.scheduleBlockTick(pos, this, 1);
        }

    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            this.updateTarget(world, pos, state);
        }
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean active = state.get(ACTIVE);
        boolean shouldBeActive = this.shouldBeActive(world, pos, state);

        if (active != shouldBeActive) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive), 2);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        // If we are active, we want to do stuff on random ticks
        return state.get(ACTIVE);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(ACTIVE)) {
            spawnParticles(world, pos);
        }
    }

    private static void spawnParticles(World world, BlockPos pos) {
        double d = 0.5625;
        Random random = world.random;

        for(Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (!world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
                Direction.Axis axis = direction.getAxis();
                double dx = axis == Direction.Axis.X ? 0.5 + d * (double)direction.getOffsetX() : (double)random.nextFloat();
                double dy = axis == Direction.Axis.Y ? 0.5 + d * (double)direction.getOffsetY() : (double)random.nextFloat();
                double dz = axis == Direction.Axis.Z ? 0.5 + d * (double)direction.getOffsetZ() : (double)random.nextFloat();
                world.addParticle(DustParticleEffect.DEFAULT, (double)pos.getX() + dx, (double)pos.getY() + dy, (double)pos.getZ() + dz, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ FACING, ACTIVE });
    }
}
