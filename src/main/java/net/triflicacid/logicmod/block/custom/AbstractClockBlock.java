package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.custom.AbstractClockBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.ClockBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractClockBlock extends SignalIOBlock implements BlockEntityProvider {
    public static final BooleanProperty LOCKED = BooleanProperty.of("locked");

    public AbstractClockBlock() {
        super(0);
        this.setDefaultState(this.stateManager.getDefaultState().with(LOCKED, false));
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return state.get(ACTIVE) ? 15 : 0;
    }

    @Override
    protected void update(World world, BlockPos pos, BlockState state) {
        boolean behind = this.getPower(world, pos, state, state.get(FACING)) > 0;
        if (behind != state.get(LOCKED)) {
            world.setBlockState(pos, state.with(LOCKED, behind));
        }
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    public void update(BlockState state, ServerWorld world, BlockPos pos) {
        if (!state.get(LOCKED)) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ClockBlockEntity clockEntity) {
                // Check if block and entity are aligned
                boolean isActive = state.get(ACTIVE);
                boolean shouldBeActive = clockEntity.isActive();
                if (isActive != shouldBeActive) {
                    state = state.with(ACTIVE, shouldBeActive).with(POWER, shouldBeActive ? 15 : 0);
                    world.setBlockState(pos, state);
                }
            }
        }
    }


    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClockBlockEntity(pos, state);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> checkType(World world, BlockEntityType<T> givenType, BlockEntityType<? extends AbstractClockBlockEntity> expectedType) {
        return world.isClient ? null : checkType2(givenType, expectedType, AbstractClockBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType2(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LOCKED);
        super.appendProperties(builder);
    }
}
