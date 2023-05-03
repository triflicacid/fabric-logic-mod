package net.triflicacid.logicmod.block.adapter;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.wire.AbstractWireBlock;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.interfaces.Wrenchable;
import net.triflicacid.logicmod.util.DirectionState;
import net.triflicacid.logicmod.util.UpdateCache;
import net.triflicacid.logicmod.util.WireColor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * Extends upon basic wire functionality. Each face can be one of three states, where it can act as accepting redstone
 * input into the wire, or emitting a redstone signal. Acts as a medium to interface redstone with wires.
 */
public class WireAdapterBlock extends AbstractWireBlock implements Wrenchable, AdvancedWrenchable, Analysable {
    public static final EnumProperty<DirectionState> DOWN = EnumProperty.of("down", DirectionState.class);
    public static final EnumProperty<DirectionState> UP = EnumProperty.of("up", DirectionState.class);
    public static final EnumProperty<DirectionState> NORTH = EnumProperty.of("north", DirectionState.class);
    public static final EnumProperty<DirectionState> SOUTH = EnumProperty.of("south", DirectionState.class);
    public static final EnumProperty<DirectionState> WEST = EnumProperty.of("west", DirectionState.class);
    public static final EnumProperty<DirectionState> EAST = EnumProperty.of("east", DirectionState.class);


    private WireAdapterBlock(WireColor color) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.STONE).breakInstantly(), color);
    }

    /** Get power being received in a given direction */
    protected int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getStrongRedstonePower(world, blockPos, direction);
    }

    @Override
    protected int getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, UpdateCache cache) {
        int power = 0;

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            if (wireBlock.getWireColor() == getWireColor()) {
                power = wireBlock.getPowerOf(world, dstPos, dstState, exploredPositions, cache);
            } else if (wireBlock instanceof WireAdapterBlock adapterBlock) {
                if (this.isInput(srcState, direction) && adapterBlock.isOutput(dstState, direction.getOpposite())) {
                    exploredPositions.add(dstPos);

                    if (cache.has(dstPos)) {
                        power = cache.get(dstPos);
                    } else {
                        power = dstState.getStrongRedstonePower(world, dstPos, direction);
                        cache.set(dstPos, power);
                    }
                }
            }
        } else if (dstBlock instanceof BusAdapterBlock busAdapterBlock) {
            busAdapterBlock.getPowerOf(world, dstPos, dstState, exploredPositions, cache);
            power = cache.get(dstPos, getWireColor());
        } else if (dstBlock instanceof JunctionBlock jBlock) {
            jBlock.getPowerOf(world, dstPos, dstState, exploredPositions, cache);
            power = cache.get(dstPos, getWireColor());
        } else if (isInput(srcState, direction)) {
            exploredPositions.add(dstPos);
            power = getPower(world, srcPos, srcState, direction);
        }

        return power;
    }

    /** Given a direction, return the property which contains the state of that face */
    public static EnumProperty<DirectionState> getDirectionState(Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    /** Is the face in the given direction in "input" mode? */
    public boolean isInput(BlockState state, Direction direction) {
        EnumProperty<DirectionState> directionState = getDirectionState(direction);
        return directionState != null && state.isOf(this) && state.get(directionState) == DirectionState.INPUT;
    }

    /** Is the face in the given direction in "output" mode? */
    public boolean isOutput(BlockState state, Direction direction) {
        EnumProperty<DirectionState> directionState = getDirectionState(direction);
        return directionState != null && state.isOf(this) && state.get(directionState) == DirectionState.OUTPUT;
    }

    @Override
    /** A more advanced rotation. Rotates clockwise from the player's position, or rotates faces towards them. */
    public BlockState applyWrench(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        if (world.isClient)
            return null;

        Direction.Axis axis = side.getAxis();
        EnumProperty<DirectionState>[] directions = null;

        if (axis == Direction.Axis.X || axis == Direction.Axis.Z) {
            switch (playerFacing) {
                case NORTH -> directions = new EnumProperty[]{NORTH, EAST, SOUTH, WEST};
                case SOUTH -> directions = new EnumProperty[]{SOUTH, WEST, NORTH, EAST};
                case WEST -> directions = new EnumProperty[]{WEST, NORTH, EAST, SOUTH};
                case EAST -> directions = new EnumProperty[]{EAST, SOUTH, WEST, NORTH};
            }
        } else if (axis == Direction.Axis.Y) {
            directions = switch (playerFacing) {
                case NORTH -> new EnumProperty[]{NORTH, UP, SOUTH, DOWN};
                case SOUTH -> new EnumProperty[]{SOUTH, UP, NORTH, DOWN};
                case WEST -> new EnumProperty[]{WEST, UP, EAST, DOWN};
                case EAST -> new EnumProperty[]{EAST, UP, WEST, DOWN};
                default -> directions;
            };
        }

        if (directions == null)
            return null;

        if (Screen.hasShiftDown()) {
            ArrayUtils.reverse(directions);
        }

        return rotateProperties(state, directions);
    }

    /** Rotate the modes of each face clockwise e.g. new_state[1] = old_state[0] */
    protected BlockState rotateProperties(BlockState state, EnumProperty<DirectionState>[] properties) {
        DirectionState[] states = new DirectionState[properties.length];
        for (int i = 0; i < states.length; i++) {
            states[i] = state.get(properties[i]);
        }

        for (int i = 1; i < states.length; i++) {
            state = state.with(properties[i], states[i - 1]);
        }
        state = state.with(properties[0], states[states.length - 1]);

        return state;
    }

    @Override
    /** Cycle the mode of the given face */
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        return state.cycle(getDirectionState(side));
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Direction> inputs = new ArrayList<>();
        List<Direction> outputs = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            EnumProperty<DirectionState> property = getDirectionState(direction);
            switch (state.get(property)) {
                case INPUT -> inputs.add(direction);
                case OUTPUT -> outputs.add(direction);
            }
        }

        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Color: ").append(Text.literal(color.toString()).formatted(color.getFormatting())));
        messages.add(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        messages.add(Text.literal("Inputs: ").append(inputs.isEmpty() ? commentToText("none") :  specialToText(joinList(inputs, ", "))));
        messages.add(Text.literal("Outputs: ").append(outputs.isEmpty() ? commentToText("none") :  specialToText(joinList(outputs, ", "))));
        return messages;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        // Only emit a signal if the requesting face is in output mode.
        return isOutput(state, direction.getOpposite()) ? state.get(POWER) : 0;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            this.updateTarget(world, pos, state);
        }
    }

    /** Update each surrounding block */
    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            for (Direction direction : Direction.values()) {
                if (isOutput(state, direction)) {
                    BlockPos blockPos = pos.offset(direction);
                    world.updateNeighbor(blockPos, this, pos);
                    world.updateNeighborsExcept(blockPos, this, direction.getOpposite());
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, WEST, EAST);
        super.appendProperties(builder);
    }

    /** get the name of an adapter of said color */
    public static String getName(WireColor color) {
        return color + "_wire_adapter";
    }

    /** Return class instance for a wire adapter of said color */
    public static WireAdapterBlock instantiate(WireColor color) {
        return new WireAdapterBlock(color) {};
    }
}
