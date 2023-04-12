package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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

import static net.triflicacid.logicmod.util.Util.booleanToText;

public class ClockBlock extends AbstractClockBlock implements AdvancedWrenchable, Analysable {
    public static final String NAME = "clock";

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(world, type, ModBlockEntity.CLOCK);
    }

    // TODO clicking on clock will open a basic GUI where one can set OnTickCount and OffTickCount

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        player.sendMessage(Text.literal("Set " + LOCKED.getName() + " to ").append(booleanToText(!state.get(LOCKED))));
        return state.cycle(LOCKED);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ClockBlockEntity clockEntity) {
            player.sendMessage(Text.literal("On Duration: ")
                    .append(Text.literal(String.valueOf(clockEntity.getOnTickCount())).formatted(Formatting.GOLD)));
            player.sendMessage(Text.literal("Off Duration: ")
                    .append(Text.literal(String.valueOf(clockEntity.getOffTickCount())).formatted(Formatting.GOLD)));
        }
    }
}
