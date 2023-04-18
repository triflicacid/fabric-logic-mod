package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.custom.AbstractClockBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.ClockBlockEntity;
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.messageAll;

public abstract class AbstractClockBlock extends AbstractBooleanBlock implements BlockEntityProvider {
    public static final BooleanProperty LOCKED = BooleanProperty.of("locked");

    public AbstractClockBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
        this.setDefaultState(this.stateManager.getDefaultState().with(LOCKED, false));
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient && (from == null || from == state.get(FACING))) {
            boolean behind = getPower(world, pos, state, state.get(FACING)) > 0;
            if (behind != state.get(LOCKED)) {
                world.setBlockState(pos, state.with(LOCKED, behind));
            }
        }
    }

    public void update(BlockState state, ServerWorld world, BlockPos pos, boolean shouldBeActive) {
        if (!state.get(LOCKED) && state.get(ACTIVE) != shouldBeActive) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive));
        }
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
