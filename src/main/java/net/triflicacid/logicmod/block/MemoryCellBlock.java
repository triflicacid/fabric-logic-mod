package net.triflicacid.logicmod.block;

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
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * A block which holds data in memory. Accepts a write line from behind, and a control line from the left.
 *
 * The control line uses constants defined in the class body; see README.md for more. memory cells may be
 * stacked next to one another: the control line value and any results of operations which operate over multiple
 * cells will be propagated as expected.
 */
public class MemoryCellBlock extends AbstractPowerBlock implements AdvancedWrenchable {
    public static final String NAME = "memory_cell";
    public static final BooleanProperty READING = BooleanProperty.of("reading");
    public static final BooleanProperty DONE_OP = BooleanProperty.of("done_op");

    /** Read: output contents of memory */
    public static final int READ = 1;
    /** Write: overwrite memory with input receiving from behind */
    public static final int WRITE = 2;
    /** Inverse: negate the memory contents (0/15) */
    public static final int INVERSE = 3;
    /** And: carry out binary AND with the input from behind: keep memory only if memory and receiving input are non-zero. */
    public static final int AND = 4;
    /** And: carry out binary OR with the input from behind: set memory to input receiving if memory is zero, else preserve. */
    public static final int OR = 5;
    /** Add: add input receiving to the memory value (as 0/1). propagate the carry to the right. */
    public static final int ADD = 10;
    /** Right shift: propagate memory contents one to the right */
    public static final int RSHIFT = 11;
    /** Left shift: propagate memory contents one to the left */
    public static final int LSHIFT = 12;
    /** Clear: set memory to zero */
    public static final int CLEAR = 15;

    public MemoryCellBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
        this.setDefaultState(this.getDefaultState().with(READING, false).with(DONE_OP, false));
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return emitsRedstone && state.get(READING) && state.get(FACING) == direction ? state.get(POWER) : 0;
    }

    /** Get the signal of the control line */
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

    /** Get the signal receiving from behind (the write line) */
    protected int getInputPower(World world, BlockPos pos, BlockState state) {
        return getPower(world, pos, state, state.get(FACING));
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient && (from == state.get(FACING) || from == state.get(FACING).rotateYClockwise())) {
            update(world, state, pos, false, true, 0);
        }
    }

    /**
     * Update this cell block
     *
     * @param force Force an update? Used to override DONE_OP
     * @param isStart Is this the starting memory cell in the chain?
     * @param data Data being passed; used by various operations. As such the meaning changes.
     */
    protected void update(World world, BlockState state, BlockPos pos, boolean force, boolean isStart, int data) {
        int control = getControlPower(world, pos, state);
        int nextData = 0;
        boolean update = false;

        // Are we reading?
        boolean reading = control == READ;
        if (reading != state.get(READING)) {
            state = state.with(READING, reading);
            update = true;
        }

        // Non-special operations
        if (control == READ);
        else if (control == WRITE) { // Write data from behind
            int receiving = getInputPower(world, pos, state);
            if (receiving != state.get(POWER)) {
                state = state.with(POWER, receiving);
                update = true;
            }
        } else if (control == CLEAR) { // Clear memory
            if (state.get(POWER) != 0) {
                state = state.with(POWER, 0);
                update = true;
            }
        }

        if (control == 0 || control == READ || control == WRITE || control == CLEAR) { // Doesn't count as a special operation, so reset DONE_OP.
            if (state.get(DONE_OP)) {
                state = state.with(DONE_OP, false);
                update = true;
            }
        } else if (state.get(DONE_OP) && !force) { // If we've done an operation, don't do it again
            if (control == 0) {
                state = state.with(DONE_OP, false);
                update = true;
            }
        } else { // Do a special operation. Update DONE_OP to make sure we only do it once
            state = state.with(DONE_OP, true);
            update = true;

            switch (control) {
                case INVERSE:
                    state = state.with(POWER, state.get(POWER) == 0 ? 15 : 0);
                    break;
                case AND: {
                    int oldPower = state.get(POWER);
                    int newPower = getInputPower(world, pos, state) > 0 && oldPower > 0 ? oldPower : 0;
                    if (newPower != oldPower) {
                        state = state.with(POWER, newPower);
                    }
                    break;
                }
                case OR:
                    if (state.get(POWER) == 0) {
                        state = state.with(POWER, getInputPower(world, pos, state));
                    }
                    break;
                case ADD: {
                    int behind = getInputPower(world, pos, state);
                    int power = state.get(POWER);
                    Pair<Boolean, Boolean> result = addBits(power != 0, behind != 0, data > 0);
                    boolean sum = result.getLeft();
                    state = state.with(POWER, sum ? 15 : 0);
                    if (result.getRight()) {
                        nextData = 1;
                    }
                    break;
                }
                case RSHIFT:
                    nextData = state.get(POWER);
                    state = state.with(POWER, data);
                    break;
                case LSHIFT: {
                    BlockPos nextPos = pos.offset(state.get(FACING).rotateYCounterclockwise());
                    BlockState nextState = world.getBlockState(nextPos);
                    state = state.with(POWER, nextState.isOf(this) ? nextState.get(POWER) : 0);
                    break;
                }
            }
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
                update(world, dstState, dstPos, nextData != 0, false, nextData);
            }
        }
    }

    @Override
    /** Manually increment/decrement the internal memory */
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        int newPower = wrapInt(state.get(POWER) + (Screen.hasShiftDown() ? (-1) : 1), 0, 15);
        player.sendMessage(Text.literal("Set ").append(specialToText(POWER.getName())).append(" to ").append(numberToText(newPower)));
        return state.with(POWER, newPower);
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        int control = getControlPower(world, pos, state);
        String controlStr = switch (control) {
            case READ -> "read";
            case WRITE -> "write";
            case INVERSE -> "inverse";
            case AND -> "and";
            case OR -> "or";
            case ADD -> "add";
            case LSHIFT -> "left_shift";
            case RSHIFT -> "right_shift";
            case CLEAR -> "clear";
            default -> "none";
        };

        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Memory: ").append(numberToText(state.get(POWER))));
        messages.add(Text.literal("Control Line: ").append(specialToText(controlStr)).append(" (").append(numberToText(control)).append(")"));
        return messages;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(READING, DONE_OP);
        super.appendProperties(builder);
    }
}
