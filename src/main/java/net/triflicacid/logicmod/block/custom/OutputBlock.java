package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OutputBlock extends AbstractPowerBlock {
    public static final String NAME = "output";

    public OutputBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), false);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true; // To make redstone cables veer towards it
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient && (from == null || from == state.get(FACING))) {
            int power = getPower(world, pos, state, state.get(FACING));
            if (state.get(POWER) != power) {
                world.setBlockState(pos, state.with(POWER, power));
            }
        }
    }
}