package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.ClockBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.*;
import static net.triflicacid.logicmod.util.Util.numberToText;

/**
 * Core mechanics for a redstone component which activates/deactivates every x/y ticks respectively.
 */
public class ClockBlock extends AbstractBooleanBlock implements AdvancedWrenchable, Analysable, BlockEntityProvider {
    public static final String NAME = "clock";

    /** When locked, the clock won't "tick" -- freezes the clock */
    public static final BooleanProperty LOCKED = BooleanProperty.of("locked");

    public ClockBlock() {
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

    /** Update the clock's state directly */
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
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient() && ModBlockEntity.CLOCK == type
                ? (w, p, s, e) -> ClockBlockEntity.tick(w, p, s, (ClockBlockEntity) e)
                : null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LOCKED);
        super.appendProperties(builder);
    }

    @Override
    /** Increment/decrement the on or off tick duration (by using the ALT key) */
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ClockBlockEntity clockEntity) {
            int delta = Screen.hasShiftDown() ? -1 : 1;
            if (Screen.hasAltDown()) {
                clockEntity.setOffTickCount(clockEntity.getOffTickCount() + delta);
                player.sendMessage(Text.literal("Set ").append(specialToText("off duration")).append(" to ").append(numberToText(clockEntity.getOffTickCount())));
            } else {
                clockEntity.setOnTickCount(clockEntity.getOnTickCount() + delta);
                player.sendMessage(Text.literal("Set ").append(specialToText("on duration")).append(" to ").append(numberToText(clockEntity.getOnTickCount())));
            }
        }

        return null;
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return;

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ClockBlockEntity clockEntity) {
            player.sendMessage(Text.literal("On Duration: ")
                    .append(numberToText(clockEntity.getOnTickCount())));
            player.sendMessage(Text.literal("Off Duration: ")
                    .append(numberToText(clockEntity.getOffTickCount())));
        }
    }
}
