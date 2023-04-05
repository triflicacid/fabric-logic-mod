package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public abstract class SignalIOBlock extends SignalRecieverBlock  {
    public SignalIOBlock(boolean initiallyActive) {
        super(initiallyActive);
    }

    protected abstract int getUpdateDelayInternal(BlockState state);

    protected abstract boolean shouldBeActive(World world, BlockPos pos, BlockState state);

    protected void update(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            boolean active = state.get(ACTIVE);
            boolean shouldBeActive = this.shouldBeActive(world, pos, state);

            if (active != shouldBeActive && !world.getBlockTickScheduler().isTicking(pos, this)) {
                TickPriority tickPriority = active ? TickPriority.VERY_HIGH : TickPriority.HIGH;
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), tickPriority);
            }
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            if (state.canPlaceAt(world, pos)) {
                this.update(world, pos, state);
            } else {
                BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                dropStacks(state, world, pos, blockEntity);
                world.removeBlock(pos, false);

                for (Direction direction : Direction.values()) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }
            }
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean active = state.get(ACTIVE);
        boolean shouldBeActive = this.shouldBeActive(world, pos, state);

        if (active != shouldBeActive) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive), 2);
        }
    }
}
