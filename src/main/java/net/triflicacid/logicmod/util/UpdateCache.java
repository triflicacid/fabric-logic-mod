package net.triflicacid.logicmod.util;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Data cache used in update routines
 *
 * Stores a block's power (or a power map) against their position, to stop redundant lookups
 */
public class UpdateCache {
    private Map<BlockPos, Integer> cachedPower = new HashMap<>();
    private Map<BlockPos, Map<WireColor, Integer>> cachedMultiPower = new HashMap<>();

    /** Is the give block cached? */
    public boolean has(BlockPos pos) {
        return cachedPower.containsKey(pos) && cachedMultiPower.containsKey(pos);
    }

    /** Get cached power for a given block. Return 0 if not cached. */
    public int get(BlockPos pos) {
        return cachedPower.containsKey(pos) ? cachedPower.get(pos) : 0;
    }

    /** Get color's power from a cached block. Return 0 if not cached, or color not provided in map. */
    public int get(BlockPos pos, WireColor color) {
        if (cachedPower.containsKey(pos)) {
            return cachedPower.get(pos);
        } else if (cachedMultiPower.containsKey(pos)) {
            Map<WireColor, Integer> map = cachedMultiPower.get(pos);
            return map.containsKey(color) ? map.get(color) : 0;
        } else {
            return 0;
        }
    }

    /** Get cached power map for a given block */
    @Nullable
    public Map<WireColor,Integer> getMap(BlockPos pos) {
        return cachedMultiPower.get(pos);
    }

    /** Cache the power of a block */
    public void set(BlockPos pos, int power) {
        cachedPower.put(pos, power);
    }

    /** Cache the power of a block as multi-power */
    public void set(BlockPos pos, WireColor color, int power) {
        Map<WireColor,Integer> map;
        if (cachedMultiPower.containsKey(pos)) {
            map = cachedMultiPower.get(pos);
        } else {
            map = new HashMap<>();
            cachedMultiPower.put(pos, map);
        }

        map.put(color, power);
    }

    /** Cache the power map of a block */
    public void set(BlockPos pos, Map<WireColor, Integer> power) {
        cachedMultiPower.put(pos, power);
    }

    /** Uncache a given block */
    public void remove(BlockPos pos) {
        cachedPower.remove(pos);
        cachedMultiPower.remove(pos);
    }

    /** Uncache this color's power for given block */
    public void remove(BlockPos pos, WireColor color) {
        cachedPower.remove(pos);

        if (cachedMultiPower.containsKey(pos)) {
            Map<WireColor, Integer> map = cachedMultiPower.get(pos);
            map.remove(color);
            if (map.size() == 0) {
                cachedMultiPower.remove(pos);
            }
        }
    }
}
