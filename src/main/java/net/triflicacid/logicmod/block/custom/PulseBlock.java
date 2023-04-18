package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.PulseBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.numberToText;
import static net.triflicacid.logicmod.util.Util.specialToText;

public class PulseBlock extends AbstractBooleanBlock implements AdvancedWrenchable, BlockEntityProvider {
    public static final String NAME = "pulse";

    public PulseBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient && (from == null || from == state.get(FACING))) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof PulseBlockEntity pulseEntity) {
                boolean shouldBeActive = pulseEntity.isActive();
                update(world, state, pos, shouldBeActive);
            }
        }
    }

    /** Called from BlockEntity */
    public void update(World world, BlockState state, BlockPos pos, boolean shouldBeActive) {
        if (!world.isClient && shouldBeActive != state.get(ACTIVE)) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive));
        }
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
