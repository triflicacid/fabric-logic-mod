package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import static net.triflicacid.logicmod.util.Util.*;

public class MemoryCellBlock extends AbstractPowerBlock implements AdvancedWrenchable {
    public static final String NAME = "memory_cell";
    public static final BooleanProperty READING = BooleanProperty.of("reading");

    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int INVERSE = 3;
    public static final int AND = 4;
    public static final int OR = 5;
    public static final int CLEAR = 15;

    public MemoryCellBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return emitsRedstone && state.get(READING) && state.get(FACING) == direction ? state.get(POWER) : 0;
    }

    protected int getControlPower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING).rotateYClockwise();
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        if (dstState.isOf(this)) {
            return getControlPower(world, dstPos, dstState);
        } else {
            return dstState.getStrongRedstonePower(world, dstPos, direction);
        }
    }

    protected int getInputPower(World world, BlockPos pos, BlockState state) {
        return getPower(world, pos, state, state.get(FACING));
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos) {
        int control = getControlPower(world, pos, state);
        boolean update = false;

        // Are we reading?
        boolean reading = control == READ;
        if (reading != state.get(READING)) {
            state = state.with(READING, reading);
            update = true;
        }

        // Other operations
        switch (control) {
            case READ:
                break;
            case WRITE: {
                int receiving = getInputPower(world, pos, state);
                if (receiving != state.get(POWER)) {
                    state = state.with(POWER, receiving);
                    update = true;
                }
                break;
            }
            case INVERSE:
                state = state.with(POWER, state.get(POWER) == 0 ? 15 : 0);
                update = true;
                break;
            case AND: {
                int oldPower = state.get(POWER);
                int newPower = getInputPower(world, pos, state) > 0 && oldPower > 0 ? oldPower : 0;
                if (newPower != oldPower) {
                    state = state.with(POWER, newPower);
                    update = true;
                }
                break;
            }
            case OR:
                if (state.get(POWER) == 0) {
                    state = state.with(POWER, getInputPower(world, pos, state));
                    update = true;
                }
                break;
            case CLEAR:
                if (state.get(POWER) != 0) {
                    state = state.with(POWER, 0);
                    update = true;
                }
                break;
        }

        if (update) {
            world.setBlockState(pos, state, 2);

            // Update output (in-front)
            BlockPos dstPos = pos.offset(state.get(FACING).getOpposite());
            world.updateNeighbor(dstPos, this, pos);

            // Update adjacent memory?
            dstPos = pos.offset(state.get(FACING).rotateYCounterclockwise());
            BlockState dstState = world.getBlockState(dstPos);
            if (dstState.isOf(this)) {
                update(world, dstState, dstPos);
            }
        }
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        int newPower = wrapInt(state.get(POWER) + (Screen.hasShiftDown() ? (-1) : 1), 0, 15);
        player.sendMessage(Text.literal("Set ").append(specialToText(POWER.getName())).append(" to ").append(numberToText(newPower)));
        return state.with(POWER, newPower);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return;

        int control = getControlPower(world, pos, state);
        String controlStr = switch (control) {
            case READ -> "read";
            case WRITE -> "write";
            case INVERSE -> "inverse";
            case AND -> "and";
            case OR -> "or";
            case CLEAR -> "clear";
            default -> "none";
        };

        player.sendMessage(Text.literal("Memory: ").append(numberToText(state.get(POWER))));
        player.sendMessage(Text.literal("Control Line: ").append(specialToText(controlStr)).append(" (").append(numberToText(control)).append(")"));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(READING);
        super.appendProperties(builder);
    }
}
