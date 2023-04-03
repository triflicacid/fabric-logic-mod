package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import java.util.function.Function;

public abstract class LogicGateBlock extends SignalEmitterBlock implements AdvancedWrenchable {
    protected Function<Direction, Direction[]> getInputDirections; // Given current direction block is facing in, return array of directions we accept as inputs

    public LogicGateBlock(Function<Direction, Direction[]> getInputDirections, boolean initiallyActive) {
        super(15, initiallyActive);
        this.getInputDirections = getInputDirections;
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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean active = state.get(ACTIVE);
        boolean shouldBeActive = this.shouldBeActive(world, pos, state);

        if (active != shouldBeActive) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive), 2);
        }
    }

    public abstract boolean isNotVariant();

    public abstract LogicGateBlock getInverse();

    public BlockState applyAdvancedWrench(BlockState state, boolean holdingShift) {
        LogicGateBlock inverse = this.getInverse();
        return inverse == null ? null : inverse.getDefaultState().with(FACING, state.get(FACING));
    }
}
