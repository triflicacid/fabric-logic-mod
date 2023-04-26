package net.triflicacid.logicmod.block.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.wire.AbstractWireBlock;
import net.triflicacid.logicmod.block.wire.BusBlock;
import net.triflicacid.logicmod.util.UpdateCache;
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
    protected Map<WireColor, Integer> getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> explored, UpdateCache cache) {
        Map<WireColor, Integer> power = new HashMap<>();

        if (dstBlock instanceof BusBlock dstBusBlock) {
            power = dstBusBlock.getPowerOf(world, dstPos, dstState, explored, cache);
        } else if (dstBlock instanceof AbstractWireBlock dstWireBlock) {
            int power2 = dstWireBlock.getPowerOf(world, dstPos, dstState, explored, cache);
            power.put(dstWireBlock.getWireColor(), power2);
        } else if (dstBlock instanceof JunctionBlock jBlock) {
            power = jBlock.getPowerOf(world, dstPos, dstState, explored, cache);
        }

        return power;
    }
}
