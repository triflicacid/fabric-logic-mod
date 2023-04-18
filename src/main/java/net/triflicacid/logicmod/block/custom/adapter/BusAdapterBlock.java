package net.triflicacid.logicmod.block.custom.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.wire.AbstractWireBlock;
import net.triflicacid.logicmod.block.custom.wire.BusBlock;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.util.WireColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An adapter for the BusBlock. Cannot interface with redstone, but can read/write signals to/from other adapters.
 */
public class BusAdapterBlock extends BusBlock {
    public static final String NAME = "bus_adapter";

    public BusAdapterBlock() {
        super(BlockSoundGroup.STONE);
    }

    @Override
    protected Map<WireColor, Integer> getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        Map<WireColor, Integer> power = new HashMap<>();

        if (dstBlock instanceof BusBlock dstBusBlock) {
            exploredPositions.add(dstPos);
            power = knownBlocks.contains(dstPos) ? ((BusBlockEntity) world.getBlockEntity(dstPos)).getPowerMap() : dstBusBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
        } else if (dstBlock instanceof AbstractWireBlock dstWireBlock) {
            exploredPositions.add(dstPos);
            int value = knownBlocks.contains(dstPos) ? dstState.get(AbstractWireBlock.POWER) : dstWireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            power.put(dstWireBlock.getWireColor(), value);
        } else if (dstBlock instanceof JunctionBlock jBlock) {
            exploredPositions.add(dstPos);
            power = jBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
        }

        return power;
    }
}
