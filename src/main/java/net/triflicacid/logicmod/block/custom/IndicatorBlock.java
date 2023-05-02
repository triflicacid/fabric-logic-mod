package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * The indicator block is similar to a redstone lamp, but responds to STRONG redstone inputs only
 */
public class IndicatorBlock extends AbstractBooleanBlock {
    public static final String NAME = "indicator";

    public IndicatorBlock() {
        super(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).breakInstantly().luminance(s -> s.get(ACTIVE) ? 7 : 0), false);
    }

    protected boolean shouldBeActive(World world, BlockState state, BlockPos pos) {
        for (Direction direction : Direction.values())
            if (getPower(world, pos, state, direction) > 0)
                return true;

        return false;
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient) {
            boolean active = shouldBeActive(world, state, pos);
            if (state.get(ACTIVE) != active) {
                world.setBlockState(pos, state.with(ACTIVE, active), Block.NOTIFY_LISTENERS);
            }
        }
    }
}