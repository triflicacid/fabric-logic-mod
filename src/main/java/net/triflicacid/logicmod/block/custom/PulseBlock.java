package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.PulseBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.numberToText;
import static net.triflicacid.logicmod.util.Util.specialToText;

public class PulseBlock extends SignalIOBlock implements AdvancedWrenchable, BlockEntityProvider {
    public static final String NAME = "pulse";

    public PulseBlock() {
        super(0);
        this.setDefaultState(this.stateManager.getDefaultState().with(ACTIVE, false));
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return state.get(ACTIVE) ? 15 : 0;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    public void update(BlockState state, ServerWorld world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof PulseBlockEntity pulseEntity) {
            boolean isActive = state.get(ACTIVE);
            boolean shouldBeActive = pulseEntity.isActive();
            if (isActive != shouldBeActive) {
                state = state.with(ACTIVE, shouldBeActive).with(POWER, shouldBeActive ? 15 : 0);
                world.setBlockState(pos, state);
            }
        }
    }


    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof PulseBlockEntity entity2) {
            entity2.incrementDuration(Screen.hasShiftDown() ? -1 : 1);
            player.sendMessage(Text.literal("Set ").append(specialToText("duration")).append(" to ").append(numberToText(entity2.getDuration())));
        }
        return null;
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof PulseBlockEntity entity2) {
            entity2.incrementDuration(Screen.hasShiftDown() ? -1 : 1);
            player.sendMessage(Text.literal("Duration: ").append(numberToText(entity2.getDuration())));
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PulseBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(world, type, ModBlockEntity.PULSE);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> checkType(World world, BlockEntityType<T> givenType, BlockEntityType<? extends PulseBlockEntity> expectedType) {
        return world.isClient ? null : checkType2(givenType, expectedType, PulseBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType2(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}
