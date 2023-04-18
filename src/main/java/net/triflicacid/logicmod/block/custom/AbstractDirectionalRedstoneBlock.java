package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.util.RelativeDirection;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDirectionalRedstoneBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public AbstractDirectionalRedstoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            update(world, state, pos);
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            BlockState sourceState = world.getBlockState(sourcePos);
            // Only update if: air (was destroyed), or could have a redstone signal
            if (sourceState.isOf(Blocks.AIR) || sourceState.emitsRedstonePower()) {
                Direction from = Direction.fromVector(sourcePos.subtract(pos));
                if (from != state.get(FACING).getOpposite()) {
                    update(world, state, pos, from);
                }
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(world, state, pos);
    }

    /** Update the block, with the update emanating from said direction (provided if neighborUpdate) */
    public abstract void update(World world, BlockState state, BlockPos pos, @Nullable Direction directionFrom);

    public final void update(World world, BlockState state, BlockPos pos) {
        update(world, state, pos, null);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, false);
            this.updateTarget(world, pos, state);
        }
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            Direction direction = state.get(FACING);
            BlockPos blockPos = pos.offset(direction.getOpposite());
            world.updateNeighbor(blockPos, this, pos);
            world.updateNeighborsExcept(blockPos, this, direction);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.appendProperties(builder);
    }

    /** Get power being received in a given direction */
    public static int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        return dstState.getStrongRedstonePower(world, dstPos, direction);
    }

    /** Get power being received in a relative direction from FACING */
    public static int getPower(World world, BlockPos pos, BlockState state, RelativeDirection relative) {
        Direction direction = RelativeDirection.turn(state.get(FACING), relative);
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        return dstState.getStrongRedstonePower(world, dstPos, direction);
    }
}
