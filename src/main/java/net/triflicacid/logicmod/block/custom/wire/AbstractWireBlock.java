package net.triflicacid.logicmod.block.custom.wire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.adapter.BusAdapterBlock;
import net.triflicacid.logicmod.block.custom.adapter.JunctionBlock;
import net.triflicacid.logicmod.util.UpdateCache;
import net.triflicacid.logicmod.util.WireColor;

import java.util.*;

/**
 * Functionality for a block which stores an internal signal strength.
 * It has a color, and will propagate it's signal immediately to adjacent AbstractWireBlocks of the same color.
 *
 * Doesn't inherit from AbstractPowerBlock as update behaviour varies slightly.
 */
public abstract class AbstractWireBlock extends Block {
    public static final IntProperty POWER = Properties.POWER;
    protected final WireColor color;

    public AbstractWireBlock(BlockSoundGroup sound, WireColor color) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(sound).breakInstantly());
        this.color = color;
    }

    public final WireColor getWireColor() {
        return color;
    }

    /** Get received power */
    public final int getReceivedPower(World world, BlockState state, BlockPos pos) {
        return getReceivedPower(world, pos, state, new HashSet<>(), new UpdateCache());
    }

    /** Get received power. Populate blocks in 'explored' */
    public final int getReceivedPower(World world, BlockPos pos, BlockState state, UpdateCache cache) {
        return getReceivedPower(world, pos, state, new HashSet<>(), cache);
    }

    /** Explore to get received power, but do not loop back */
    public final int getReceivedPower(World world, BlockPos pos, BlockState state, Set<BlockPos> exploredPositions, UpdateCache cache) {
        exploredPositions.add(pos);
        int power = 0;

        for (Direction direction : Direction.values()) {
            BlockPos dstPos = pos.offset(direction);
            if (exploredPositions.contains(dstPos))
                continue;

            BlockState dstState = world.getBlockState(dstPos);
            int power2 = getPowerOfNeighbor(world, pos, state, dstPos, dstState, dstState.getBlock(), direction, exploredPositions, cache);
            if (power2 > power) {
                power = power2;
            }
        }

        return power;
    }

    /** Helper for getReceivedPower -- get power of neighbor block. srcState is the state of this */
    protected int getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, UpdateCache cache) {
        int power = 0;

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            if (wireBlock.getWireColor() == getWireColor()) {
                power = wireBlock.getPowerOf(world, dstPos, dstState, exploredPositions, cache);
            }
        } else if (dstBlock instanceof BusAdapterBlock busAdapterBlock) {
            busAdapterBlock.getPowerOf(world, dstPos, dstState, exploredPositions, cache);
            power = cache.get(dstPos, getWireColor());
        } else if (dstBlock instanceof JunctionBlock jBlock) {
            jBlock.getPowerOf(world, dstPos, dstState, exploredPositions, cache);
            power = cache.get(dstPos, getWireColor());
        }

        return power;
    }

    public static void update(World world, BlockPos origin) {
        update(world, origin, new HashSet<>(), new UpdateCache());
    }

    /** Update oneself and all connected AbstractWireBlocks */
    public static void update(World world, BlockPos origin, Set<BlockPos> explored, UpdateCache cache) {
        Deque<Pair<BlockPos,BlockPos>> positions = new ArrayDeque<>(); // (prev, pos)
        positions.add(new Pair<>(null, origin));

        while (positions.size() > 0) {
            Pair<BlockPos, BlockPos> pair = positions.remove();
            BlockPos pos = pair.getRight();
            if (explored.contains(pos)) {
                continue;
            }

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof AbstractWireBlock wireBlock) {
                int power = state.get(POWER);
                int receiving = wireBlock.getReceivedPower(world, pos, state, cache);
                explored.add(pos);
                cache.set(pos, receiving);

                if (power != receiving) {
                    state = state.with(POWER, receiving);
                    world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);

                    for (Direction direction : Direction.values()) {
                        positions.add(new Pair<>(pos, pos.offset(direction)));
                    }
                }
            } else if (block instanceof BusBlock) {
                BusBlock.update(world, pos, explored, cache);
                explored.add(pos);
            } else if (block instanceof JunctionBlock) {
                JunctionBlock.update(world, pos, state, explored, cache);
                explored.add(pos);
            } else {
                // Update neighbors -- if adapter, may update a redstone component
//                BlockPos prev = pair.getLeft();
//                BlockState prevState = world.getBlockState(prev);
//                block.neighborUpdate(state, world, pos, prevState.getBlock(), prev, true);
            }
        }
    }

    /** Get power of said block. Set position as explored & add to cache. */
    public int getPowerOf(World world, BlockPos pos, BlockState state, Set<BlockPos> explored, UpdateCache cache) {
        explored.add(pos);

        if (cache.has(pos)) {
            return cache.get(pos);
        } else {
            int power = getReceivedPower(world, pos, state, explored, cache);
            cache.set(pos, power);
            return power;
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient)
            update(world, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            update(world, pos);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        super.appendProperties(builder);
    }
}
