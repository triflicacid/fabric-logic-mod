package net.triflicacid.logicmod.block.custom.wire;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.util.WireColor;

import static net.triflicacid.logicmod.util.Util.*;

public abstract class WireBlock extends AbstractWireBlock implements Analysable {
    public WireBlock(WireColor color) {
        super(BlockSoundGroup.WOOL, color);
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

    public static final String getName(WireColor color) {
        return color + "_wire";
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Color: ").append(Text.literal(color.toString()).formatted(color.getFormatting())));
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        }
    }
}
