package net.triflicacid.logicmod.block.custom.wire;

import net.minecraft.block.BlockState;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.util.WireColor;

public abstract class WireBlock extends AbstractWireBlock {
    public WireBlock(WireColor color) {
        super(color);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(POWER) > 0;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (hasRandomTicks(state)) {
            spawnParticles(world, pos);
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    private static void spawnParticles(World world, BlockPos pos) {
        double d = 0.5625;
        Random random = world.random;

        for(Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (!world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
                Direction.Axis axis = direction.getAxis();
                double dx = axis == Direction.Axis.X ? 0.5 + d * (double)direction.getOffsetX() : (double)random.nextFloat();
                double dy = axis == Direction.Axis.Y ? 0.5 + d * (double)direction.getOffsetY() : (double)random.nextFloat();
                double dz = axis == Direction.Axis.Z ? 0.5 + d * (double)direction.getOffsetZ() : (double)random.nextFloat();
                world.addParticle(DustParticleEffect.DEFAULT, (double)pos.getX() + dx, (double)pos.getY() + dy, (double)pos.getZ() + dz, 0.0, 0.0, 0.0);
            }
        }
    }

    protected static final String getBlockName(WireColor color) {
        return color + "_wire_block";
    }

    protected static final String getItemName(WireColor color) {
        return color + "_wire";
    }
}
