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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.util.WireColor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractWireBlock extends Block {
    public static final IntProperty POWER = Properties.POWER;
    private final WireColor color;

    public AbstractWireBlock(WireColor color) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).breakInstantly());
        this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0));
        this.color = color;
    }

    public final WireColor getWireColor() {
        return color;
    }

    /** Get received power */
    public final int getReceivedPower(World world, BlockState state, BlockPos pos) {
        return getReceivedPower(world, pos, state, new HashSet<>(), new HashSet<>());
    }

    /** Get received power. Don't recalculate receiving power for knownBlocks -- their POWER property is updated */
    public final int getReceivedPower(World world, BlockPos pos, BlockState state, Set<BlockPos> knownBlocks) {
        return getReceivedPower(world, pos, state, new HashSet<>(), knownBlocks);
    }

    /** Explore to get received power, but do not loop back */
    protected final int getReceivedPower(World world, BlockPos pos, BlockState state, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        int power = 0;

        for (Direction direction : Direction.values()) {
            BlockPos dstPos = pos.offset(direction);
            if (exploredPositions.contains(dstPos))
                continue;

            exploredPositions.add(dstPos);
            BlockState dstState = world.getBlockState(dstPos);

            int power2 = getPowerOfNeighbor(world, pos, state, dstPos, dstState, dstState.getBlock(), direction, exploredPositions, knownBlocks);
            if (power2 > power) {
                power = power2;
            }
        }

        return power;
    }

    /** Helper for getReceivedPower -- get power of neighbor block. srcState is the state of this */
    protected int getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        int power = 0;

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            if (wireBlock.getWireColor() == getWireColor()) {
                power = knownBlocks.contains(dstPos) ? dstState.get(POWER) : wireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            }
        }

        return power;
    }

    /** Update oneself and all connected AbstractWireBlocks */
    public static final void update(World world, BlockPos origin) {
        Deque<BlockPos> positions = new ArrayDeque<>();
        Set<BlockPos> explored = new HashSet<>();

        positions.add(origin);

        while (positions.size() > 0) {
            BlockPos pos = positions.remove();
            if (explored.contains(pos)) {
                continue;
            }

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof AbstractWireBlock wireBlock) {
                int power = state.get(POWER);
                int receiving = wireBlock.getReceivedPower(world, pos, state, explored);
                explored.add(pos);

                if (power != receiving) {
                    state = state.with(POWER, receiving);
                    world.setBlockState(pos, state, 2);

                    for (Direction direction : Direction.values()) {
                        BlockPos pos2 = pos.offset(direction);
                        BlockState state2 = world.getBlockState(pos2);
                        Block block2 = state2.getBlock();

                        if (block2 instanceof AbstractWireBlock wireBlock2) {
                            if (wireBlock2.getWireColor() == wireBlock.getWireColor()) {
                                positions.add(pos.offset(direction));
                            }
                        } else {
                            block2.neighborUpdate(state2, world, pos2, block, pos, true);
                        }
                    }
                }
            }
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
