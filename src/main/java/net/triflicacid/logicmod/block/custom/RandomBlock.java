package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import static net.triflicacid.logicmod.util.Util.booleanToText;
import static net.triflicacid.logicmod.util.Util.numberToText;

public class RandomBlock extends SignalIOBlock implements AdvancedWrenchable {
    public static final String NAME = "random";
    public static final BooleanProperty BINARY = BooleanProperty.of("binary");

    public RandomBlock() {
        super(0);
        this.setDefaultState(this.stateManager.getDefaultState().with(BINARY, false));
    }

    protected final int getRandom(BlockState state) {
        return (int) (Math.random() * (state.get(BINARY) ? 2 : 16));
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return state.get(POWER);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (getPower(world, pos, state, state.get(FACING)) > 0) {
            int value = getRandom(state);
            state = state.with(POWER, value).with(ACTIVE, value > 0);
            world.setBlockState(pos, state);
            world.scheduleBlockTick(pos, this, 2, TickPriority.NORMAL);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BINARY);
        super.appendProperties(builder);
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        player.sendMessage(Text.literal("Set " + BINARY.getName() + " to ").append(booleanToText(!state.get(BINARY))));
        return state.cycle(BINARY);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        player.sendMessage(Text.literal("Binary: ").append(booleanToText(state.get(BINARY))));
    }
}
