package net.triflicacid.logicmod.block.custom.adapter;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.triflicacid.logicmod.block.custom.wire.AbstractWireBlock;
import net.triflicacid.logicmod.block.custom.wire.BusBlock;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.util.WireColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.triflicacid.logicmod.util.Util.*;

public class JunctionBlock extends Block implements Analysable {
    public static final String NAME = "junction";

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public JunctionBlock() {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.STONE).breakInstantly());
        this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
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
    public final Map<WireColor, Integer> getReceivedPower(World world, BlockPos pos, BlockState state, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        exploredPositions.add(pos);
        Map<WireColor, Integer> power = new HashMap<>();

        for (Direction direction : Direction.values()) {
            BlockPos dstPos = pos.offset(direction);
            if (exploredPositions.contains(dstPos))
                continue;

            BlockState dstState = world.getBlockState(dstPos);
            Map<WireColor, Integer> power2 = getPowerOfNeighbor(world, pos, state, dstPos, dstState, dstState.getBlock(), direction, exploredPositions, knownBlocks);
            mergePowerMaps(power, power2);
        }

        return power;
    }

    /** Helper for getReceivedPower -- get power of neighbor block. srcState is the state of this */
    protected Map<WireColor, Integer> getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        Map<WireColor, Integer> power = new HashMap<>();

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            exploredPositions.add(dstPos);
            int power2 = knownBlocks.contains(dstPos) ? dstState.get(AbstractWireBlock.POWER) : wireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            power.put(wireBlock.getWireColor(), power2);
        } else if (dstBlock instanceof BusAdapterBlock busAdapterBlock) {
            exploredPositions.add(dstPos);
            power = knownBlocks.contains(dstPos) ? ((BusBlockEntity) world.getBlockEntity(dstPos)).getPowerMap() : busAdapterBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
        } else if (dstBlock instanceof JunctionBlock junctionBlock) {
//            exploredPositions.add(dstPos);
//            power = junctionBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
        }

        return power;
    }

    public static void update(World world, BlockPos origin, BlockState state) {
        update(world, origin, state, new HashSet<>());
    }

    /** Update oneself and all connected AbstractWireBlocks */
    public static void update(World world, BlockPos origin, BlockState originState, Set<BlockPos> explored) {
        if (explored.contains(origin))
            return;
        explored.add(origin);

        Map<WireColor, Integer> power = ((JunctionBlock) originState.getBlock()).getReceivedPower(world, originState, origin);
        boolean active = false;
        for (WireColor color : WireColor.values()) {
            if (power.containsKey(color) && power.get(color) > 0) {
                active = true;
                break;
            }
        }
        originState = originState.with(ACTIVE, active);
        world.setBlockState(origin, originState, 2);

        for (Direction direction : Direction.values()) {
            BlockPos pos = origin.offset(direction);
            if (explored.contains(pos))
                continue;

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof AbstractWireBlock) {
                AbstractWireBlock.update(world, pos, explored);
            } else if (block instanceof BusBlock) {
                BusBlock.update(world, pos, explored);
            } else if (block instanceof JunctionBlock) {
//                JunctionBlock.update(world, pos, state, explored);
            }

            explored.add(pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient()) {
            update(world, pos, state);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(world, pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            update(world, pos, state);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
        super.appendProperties(builder);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        Map<WireColor, Integer> map = getReceivedPower(world, state, pos);
        int power, count = 0;
        for (WireColor color : WireColor.values()) {
            if (map.containsKey(color) && (power = map.get(color)) > 0) {
                count++;
                player.sendMessage(Text.literal("").append(Text.literal(capitalise(color.toString())).formatted(color.getFormatting())).append(" power: ").append(numberToText(power)));
            }
        }
        if (count == 0) {
            player.sendMessage(commentToText("No power"));
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(ACTIVE);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (hasRandomTicks(state)) {
            spawnRedstoneParticles(world, pos);
        }
        super.randomDisplayTick(state, world, pos, random);
    }
}
