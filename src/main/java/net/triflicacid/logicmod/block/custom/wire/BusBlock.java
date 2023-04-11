package net.triflicacid.logicmod.block.custom.wire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.util.WireColor;

import java.util.*;

public class BusBlock extends Block implements BlockEntityProvider {
    public static final String NAME = "bus";
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public BusBlock() {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).breakInstantly());
    }

    /** Get received power */
    public final Map<WireColor, Integer> getReceivedPower(World world, BlockState state, BlockPos pos) {
        return getReceivedPower(world, pos, state, new HashSet<>(), new HashSet<>());
    }

    /** Get received power. Don't recalculate receiving power for knownBlocks -- their POWER property is updated */
    public final Map<WireColor, Integer> getReceivedPower(World world, BlockPos pos, BlockState state, Set<BlockPos> knownBlocks) {
        return getReceivedPower(world, pos, state, new HashSet<>(), knownBlocks);
    }

    /** Explore to get received power, but do not loop back */
    protected final Map<WireColor, Integer> getReceivedPower(World world, BlockPos pos, BlockState state, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        exploredPositions.add(pos);
        Map<WireColor, Integer> power = new HashMap<>();

        for (Direction direction : Direction.values()) {
            BlockPos dstPos = pos.offset(direction);
            if (exploredPositions.contains(dstPos))
                continue;

            exploredPositions.add(dstPos);
            BlockState dstState = world.getBlockState(dstPos);

            Map<WireColor, Integer> power2 = getPowerOfNeighbor(world, pos, state, dstPos, dstState, dstState.getBlock(), direction, exploredPositions, knownBlocks);
            for (WireColor color : power2.keySet()) {
                if (power.containsKey(color)) {
                    if (power2.get(color) > power.get(color))
                        power.put(color, power2.get(color));
                } else {
                    power.put(color, power2.get(color));
                }
            }
        }

        return power;
    }

    protected Map<WireColor, Integer> getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        Map<WireColor, Integer> power = null;

        if (dstBlock instanceof BusBlock dstBusBlock) {
            power = knownBlocks.contains(dstPos) ? ((BusBlockEntity) world.getBlockEntity(dstPos)).getPowerMap() : dstBusBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
        }


        return power == null ? new HashMap<>() : power;
    }

    public static final void update(World world, BlockPos origin) {
        update(world, origin, new HashSet<>());
    }

    public static final void update(World world, BlockPos origin, Set<BlockPos> explored) {
        Deque<BlockPos> positions = new ArrayDeque<>();

        positions.add(origin);

        while (positions.size() > 0) {
            BlockPos pos = positions.remove();
            if (explored.contains(pos)) {
                continue;
            }

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof BusBlock busBlock) {
                BusBlockEntity entity = (BusBlockEntity) world.getBlockEntity(pos);
                Map<WireColor, Integer> receiving = busBlock.getReceivedPower(world, pos, state, explored);
                explored.add(pos);

                if (!entity.arePowerMapsEqual(receiving)) {
                    for (WireColor color : receiving.keySet())
                        entity.setPower(color, receiving.get(color));

                    state = state.with(ACTIVE, entity.containsPower());
                    world.setBlockState(pos, state, 2);

                    for (Direction direction : Direction.values()) {
                        BlockPos pos2 = pos.offset(direction);
                        BlockState state2 = world.getBlockState(pos2);
                        Block block2 = state2.getBlock();

                        if (block2 instanceof BusBlock) {
                            positions.add(pos.offset(direction));
                        } else {
                            block2.neighborUpdate(state2, world, pos2, block, pos, true);
                        }
                    }
                }
            } else if (block instanceof AbstractWireBlock) {
                AbstractWireBlock.update(world, pos, explored);
                explored.add(pos);
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BusBlockEntity(pos, state);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(ACTIVE);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(ACTIVE)) {
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
        super.appendProperties(builder);
    }
}
