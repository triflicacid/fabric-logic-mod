package net.triflicacid.logicmod.block.custom.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.triflicacid.logicmod.block.custom.wire.AbstractWireBlock;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.interfaces.Wrenchable;
import net.triflicacid.logicmod.util.DirectionState;
import net.triflicacid.logicmod.util.WireColor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.triflicacid.logicmod.util.Util.*;

public abstract class WireAdapterBlock extends AbstractWireBlock implements Wrenchable, AdvancedWrenchable, Analysable {
    public static final EnumProperty<DirectionState> DOWN = EnumProperty.of("down", DirectionState.class);
    public static final EnumProperty<DirectionState> UP = EnumProperty.of("up", DirectionState.class);
    public static final EnumProperty<DirectionState> NORTH = EnumProperty.of("north", DirectionState.class);
    public static final EnumProperty<DirectionState> SOUTH = EnumProperty.of("south", DirectionState.class);
    public static final EnumProperty<DirectionState> WEST = EnumProperty.of("west", DirectionState.class);
    public static final EnumProperty<DirectionState> EAST = EnumProperty.of("east", DirectionState.class);


    public WireAdapterBlock(WireColor color) {
        super(BlockSoundGroup.STONE, color);
    }

    /** Get power being received in a given direction */
    protected int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.getStrongRedstonePower(world, blockPos, direction);
    }

    @Override
    protected int getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        int power = 0;

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            if (wireBlock.getWireColor() == getWireColor()) {
                exploredPositions.add(dstPos);
                power = knownBlocks.contains(dstPos) ? dstState.get(POWER) : wireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            } else if (wireBlock instanceof WireAdapterBlock adapterBlock) {
                if (this.isInput(srcState, direction) && adapterBlock.isOutput(dstState, direction.getOpposite())) {
                    exploredPositions.add(dstPos);
                    power = knownBlocks.contains(dstPos) ? dstState.get(POWER) : dstState.getStrongRedstonePower(world, dstPos, direction);
                }
            }
        } else if (dstBlock instanceof BusAdapterBlock busAdapterBlock) {
            exploredPositions.add(dstPos);
            Map<WireColor, Integer> powerMap = knownBlocks.contains(dstPos) ? ((BusBlockEntity) world.getBlockEntity(dstPos)).getPowerMap() : busAdapterBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            if (powerMap.containsKey(getWireColor()))
                power = powerMap.get(getWireColor());
        } else if (dstBlock instanceof JunctionBlock jBlock) {
            exploredPositions.add(dstPos);
            Map<WireColor, Integer> map = jBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            if (map.containsKey(getWireColor())) {
                power = map.get(getWireColor());
            }
        } else if (isInput(srcState, direction)) {
            exploredPositions.add(dstPos);
            power = getPower(world, srcPos, srcState, direction);
        }

        return power;
    }

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

    public boolean isInput(BlockState state, Direction direction) {
        EnumProperty<DirectionState> directionState = getDirectionState(direction);
        return directionState != null && state.isOf(this) && state.get(directionState) == DirectionState.INPUT;
    }

    public boolean isOutput(BlockState state, Direction direction) {
        EnumProperty<DirectionState> directionState = getDirectionState(direction);
        return directionState != null && state.isOf(this) && state.get(directionState) == DirectionState.OUTPUT;
    }

    @Override
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
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

//        world.scheduleBlockTick(pos, this, 1);
        return state.cycle(getDirectionState(side));
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return;

        List<Direction> inputs = new ArrayList<>();
        List<Direction> outputs = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            EnumProperty<DirectionState> property = getDirectionState(direction);
            switch (state.get(property)) {
                case INPUT -> inputs.add(direction);
                case OUTPUT -> outputs.add(direction);
            }
        }

        player.sendMessage(Text.literal("Color: ").append(Text.literal(color.toString()).formatted(color.getFormatting())));
        player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        player.sendMessage(Text.literal("Inputs: ").append(inputs.isEmpty() ? commentToText("none") :  specialToText(joinList(inputs, ", "))));
        player.sendMessage(Text.literal("Outputs: ").append(outputs.isEmpty() ? commentToText("none") :  specialToText(joinList(outputs, ", "))));
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

    protected static String getName(WireColor color) {
        return color + "_wire_adapter";
    }
}