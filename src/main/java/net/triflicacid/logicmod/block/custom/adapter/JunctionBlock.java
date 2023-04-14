package net.triflicacid.logicmod.block.custom.adapter;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.wire.AbstractWireBlock;
import net.triflicacid.logicmod.block.custom.wire.BusBlock;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.util.WireColor;

import java.util.*;

import static net.triflicacid.logicmod.util.Util.capitalise;
import static net.triflicacid.logicmod.util.Util.numberToText;

public class JunctionBlock extends Block implements AdvancedWrenchable, Analysable {
    public static final String NAME = "junction";

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    public static final EnumProperty<WireColor> COLOR_1 = EnumProperty.of("color_1", WireColor.class);
    public static final IntProperty POWER_1 = IntProperty.of("power_1", 0, 15);
    public static final EnumProperty<WireColor> COLOR_2 = EnumProperty.of("color_2", WireColor.class);
    public static final IntProperty POWER_2 = IntProperty.of("power_2", 0, 15);

    public JunctionBlock() {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.STONE).breakInstantly());
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(ACTIVE, false)
                .with(COLOR_1, WireColor.GREEN)
                .with(POWER_1, 0)
                .with(COLOR_2, WireColor.BLUE)
                .with(POWER_2, 0));
    }

    /** Is this a valid color? */
    public static final boolean validColor(BlockState state, WireColor color) {
        return color == state.get(COLOR_1) || color == state.get(COLOR_2);
    }

    /** Get power associated with this color */
    public static final int getPower(BlockState state, WireColor color) {
        if (color == state.get(COLOR_1))
            return state.get(POWER_1);
        if (color == state.get(COLOR_2))
            return state.get(POWER_2);
        return 0;
    }

    /** Set power for a given color */
    public static final BlockState setPower(BlockState state, WireColor color, int power) {
        if (color == state.get(COLOR_1))
            state =  state.with(POWER_1, power);
        if (color == state.get(COLOR_2))
            state = state.with(POWER_2, power);
        return state.with(ACTIVE, state.get(POWER_1) > 0 || state.get(POWER_2) > 0);
    }

    /** Get received power */
    public final int getReceivedPower(World world, BlockState state, BlockPos pos, WireColor color) {
        return getReceivedPower(world, pos, state, color, new HashSet<>(), new HashSet<>());
    }

    /** Get received power. Don't recalculate receiving power for knownBlocks -- their POWER property is updated */
    public final int getReceivedPower(World world, BlockPos pos, BlockState state, WireColor color, Set<BlockPos> knownBlocks) {
        return getReceivedPower(world, pos, state, color, new HashSet<>(), knownBlocks);
    }

    /** Explore to get received power, but do not loop back */
    public final int getReceivedPower(World world, BlockPos pos, BlockState state, WireColor color, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        exploredPositions.add(pos);
        int power = 0;

        if (color == state.get(COLOR_1) || color == state.get(COLOR_2)) {
            for (Direction direction : Direction.values()) {
                BlockPos dstPos = pos.offset(direction);
                if (exploredPositions.contains(dstPos))
                    continue;

                exploredPositions.add(dstPos);
                BlockState dstState = world.getBlockState(dstPos);

                int power2 = getPowerOfNeighbor(world, pos, state, dstPos, dstState, dstState.getBlock(), direction, color, exploredPositions, knownBlocks);
                if (power2 > power) {
                    power = power2;
                }
            }
        }

        return power;
    }

    /** Helper for getReceivedPower -- get power of neighbor block. srcState is the state of this */
    protected int getPowerOfNeighbor(World world, BlockPos srcPos, BlockState srcState, BlockPos dstPos, BlockState dstState, Block dstBlock, Direction direction, WireColor color, Set<BlockPos> exploredPositions, Set<BlockPos> knownBlocks) {
        int power = 0;

        if (dstBlock instanceof AbstractWireBlock wireBlock) {
            if (wireBlock.getWireColor() == color) {
                power = knownBlocks.contains(dstPos) ? dstState.get(AbstractWireBlock.POWER) : wireBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            }
        } else if (dstBlock instanceof BusAdapterBlock busAdapterBlock) {
            Map<WireColor, Integer> powerMap = knownBlocks.contains(dstPos) ? ((BusBlockEntity) world.getBlockEntity(dstPos)).getPowerMap() : busAdapterBlock.getReceivedPower(world, dstPos, dstState, exploredPositions, knownBlocks);
            if (powerMap.containsKey(color))
                power = powerMap.get(color);
        } else if (dstBlock instanceof JunctionBlock junctionBlock) {
            power = knownBlocks.contains(dstPos) ? junctionBlock.getPower(dstState, color) : this.getReceivedPower(world, dstPos, dstState, color, exploredPositions, knownBlocks);
        }

        return power;
    }

    public static final void update(World world, BlockPos origin, BlockState state) {
        update(world, origin, state, state.get(COLOR_1), new HashSet<>());
        update(world, origin, state, state.get(COLOR_2), new HashSet<>());
    }

    public static final void update(World world, BlockPos origin, BlockState state, WireColor color) {
        update(world, origin, state, color, new HashSet<>());
    }

    public static final void update(World world, BlockPos origin, BlockState state, Set<BlockPos> explored) {
        update(world, origin, state, state.get(COLOR_1), new HashSet<>());
        update(world, origin, state, state.get(COLOR_2), new HashSet<>());
    }

    /** Update oneself and all connected AbstractWireBlocks */
    public static final void update(World world, BlockPos origin, BlockState originState, WireColor color, Set<BlockPos> explored) {
        if (!validColor(originState, color))
            return;

        Deque<BlockPos> positions = new ArrayDeque<>();

        positions.add(origin);

        while (positions.size() > 0) {
            BlockPos pos = positions.remove();
            if (explored.contains(pos)) {
                continue;
            }

            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof JunctionBlock junctionBlock) {
                int power = junctionBlock.getPower(state, color);
                int receiving = junctionBlock.getReceivedPower(world, pos, state, color, explored);
                explored.add(pos);

                if (power != receiving) {
                    state = junctionBlock.setPower(state, color, receiving);
                    world.setBlockState(pos, state, 2);

                    for (Direction direction : Direction.values()) {
                        BlockPos pos2 = pos.offset(direction);
                        BlockState state2 = world.getBlockState(pos2);
                        Block block2 = state2.getBlock();

                        if (block2 instanceof JunctionBlock junctionBlock2) {
                            if (junctionBlock2.validColor(state2, color)) {
                                positions.add(pos.offset(direction));
                            }
                        } else {
                            block2.neighborUpdate(state2, world, pos2, block, pos, true);
                        }
                    }
                }
            } else if (block instanceof BusBlock) {
                BusBlock.update(world, pos, explored);
                explored.add(pos);
            } else if (block instanceof AbstractWireBlock wireBlock) {
                if (wireBlock.getWireColor() == color) {
                    AbstractWireBlock.update(world, pos, explored);
                    explored.add(pos);
                }
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient)
            update(world, pos, state);
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, COLOR_1, COLOR_2, POWER_1, POWER_2);
        super.appendProperties(builder);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        WireColor color1 = state.get(COLOR_1);
        WireColor color2 = state.get(COLOR_2);
        player.sendMessage(Text.literal("").append(Text.literal(capitalise(color1.toString())).formatted(color1.getFormatting())).append(" power: ").append(numberToText(state.get(POWER_1))));
        player.sendMessage(Text.literal("").append(Text.literal(capitalise(color2.toString())).formatted(color2.getFormatting())).append(" power: ").append(numberToText(state.get(POWER_2))));
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        EnumProperty<WireColor> property = Screen.hasAltDown() ? COLOR_2 : COLOR_1;
        WireColor oldColor = state.get(property);
        state = state.cycle(property);
        WireColor newColor = state.get(property);

        player.sendMessage(Text.literal("Changed channel from ").append(Text.literal(oldColor.toString()).formatted(oldColor.getFormatting())).append(" to ").append(Text.literal(newColor.toString()).formatted(newColor.getFormatting())));
        return state;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(ACTIVE);
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
}
