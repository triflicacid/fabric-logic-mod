package net.triflicacid.logicmod.block.custom.wire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.adapter.JunctionBlock;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.util.WireColor;

import java.util.*;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * Similar to a wire, but contains a field for every wire color type (using a blockentity).
 */
public class BusBlock extends Block implements BlockEntityProvider, Analysable {
    public static final String NAME = "bus";
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public BusBlock(BlockSoundGroup sound) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(sound).breakInstantly());
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

    protected Map<WireColor, Integer> getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        Map<WireColor, Integer> power = null;

        if (dstBlock instanceof BusBlock dstBusBlock) {
            exploredPositions.add(dstPos);
            power = knownBlocks.contains(dstPos) ? ((BusBlockEntity) world.getBlockEntity(dstPos)).getPowerMap() : dstBusBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
        }

        return power == null ? new HashMap<>() : power;
    }

    public static void update(World world, BlockPos origin) {
        update(world, origin, new HashSet<>());
    }

    public static void update(World world, BlockPos origin, Set<BlockPos> explored) {
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
                        positions.add(pos.offset(direction));
                    }
                }
            } else if (block instanceof AbstractWireBlock) {
                AbstractWireBlock.update(world, pos, explored);
                explored.add(pos);
            } else if (block instanceof JunctionBlock) {
                JunctionBlock.update(world, pos, state, explored);
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
            spawnRedstoneParticles(world, pos);
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof BusBlockEntity busEntity) {
                for (WireColor color : BusBlockEntity.COLORS) {
                    player.sendMessage(Text.literal("").append(Text.literal(capitalise(color.toString())).formatted(color.getFormatting())).append(Text.literal(" power: ")).append(numberToText(busEntity.getPower(color))));
                }
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
        super.appendProperties(builder);
    }
}
