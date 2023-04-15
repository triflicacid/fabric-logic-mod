package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.ClockBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.*;

public class ClockBlock extends AbstractClockBlock implements AdvancedWrenchable, Analysable {
    public static final String NAME = "clock";

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(world, type, ModBlockEntity.CLOCK);
    }

    @Override
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
