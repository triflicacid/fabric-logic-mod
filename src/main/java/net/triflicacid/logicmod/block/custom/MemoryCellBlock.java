package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;

import static net.triflicacid.logicmod.util.Util.*;

public class MemoryCellBlock extends SignalIOBlock implements AdvancedWrenchable {
    public static final String NAME = "memory_cell";
    public static final IntProperty MEMORY = IntProperty.of("memory", 0, 15);

    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int CLEAR = 3;

    public MemoryCellBlock() {
        super(0);
    }

    private int getControlPower(World world, BlockPos pos, BlockState state) {
        return getPower(world, pos, state, state.get(FACING).rotateYClockwise());
    }

    private int getInputPower(World world, BlockPos pos, BlockState state) {
        return getPower(world, pos, state, state.get(FACING));
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        switch (getControlPower(world, pos, state)) {
            case READ:
                return state.get(MEMORY);
            case WRITE:
                return getInputPower(world, pos, state);
            case CLEAR:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState newState = null;
        switch (getControlPower(world, pos, state)) {
            case READ:
                if (state.get(POWER) != state.get(MEMORY)) {
                    int memory = state.get(MEMORY);
                    newState = state.with(POWER, memory).with(ACTIVE, memory > 0);
                }
                break;
            case WRITE: {
                int receiving = getInputPower(world, pos, state);
                if (receiving != state.get(MEMORY)) {
                    newState = state.with(MEMORY, receiving);
                }
                break;
            }
            case CLEAR:
                if (state.get(MEMORY) != 0) {
                    newState = state.with(MEMORY, 0);
                }
                break;
            default:
                if (state.get(POWER) != 0) {
                    newState = state.with(POWER, 0).with(ACTIVE, false);
                }
                break;
        }

        if (newState != null) {
            world.setBlockState(pos, newState);
        }
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        int newMemory = wrapInt(state.get(MEMORY) + (Screen.hasShiftDown() ? (-1) : 1), 0, 15);
        player.sendMessage(Text.literal("Set ").append(specialToText(MEMORY.getName())).append(" to ").append(numberToText(newMemory)));
        return state.with(MEMORY, newMemory);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return;

        int control = getControlPower(world, pos, state);
        String controlStr;
        switch (control) {
            case READ:
                controlStr = "read";
                break;
            case WRITE:
                controlStr = "write";
                break;
            case CLEAR:
                controlStr = "clear";
                break;
            default:
                controlStr = "none";
        }

        player.sendMessage(Text.literal("Memory: ").append(numberToText(state.get(MEMORY))));
        player.sendMessage(Text.literal("Control Line: ").append(specialToText(controlStr)).append(" (").append(numberToText(state.get(MEMORY))).append(")"));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MEMORY);
        super.appendProperties(builder);
    }
}
