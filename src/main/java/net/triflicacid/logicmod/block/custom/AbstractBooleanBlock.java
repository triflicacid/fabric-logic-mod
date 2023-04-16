package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;

import static net.triflicacid.logicmod.util.Util.booleanToText;

public abstract class AbstractBooleanBlock extends Block implements Analysable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    protected final boolean emitsRedstone;
    protected int tickDelay = 0;
    protected int LO = 0;
    protected int HI = 15;

    public AbstractBooleanBlock(Settings settings, boolean emitsRedstone) {
        this(settings, emitsRedstone, false);
    }

    public AbstractBooleanBlock(Settings settings, boolean emitsRedstone, boolean initiallyActive) {
        super(settings);
        this.emitsRedstone = emitsRedstone;
        this.setDefaultState(this.getDefaultState().with(ACTIVE, initiallyActive));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return emitsRedstone;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return emitsRedstone && state.get(FACING) == direction ? (state.get(ACTIVE) ? HI : LO) : 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient)
            update(world, state, pos);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
                update(world, state, pos);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(world, state, pos);
    }

    public abstract void update(World world, BlockState state, BlockPos pos);

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.updateTarget(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            this.updateTarget(world, pos, state);
        }
    }

    protected void updateTarget(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            Direction direction = state.get(FACING);
            BlockPos blockPos = pos.offset(direction.getOpposite());
            world.updateNeighbor(blockPos, this, pos);
            world.updateNeighborsExcept(blockPos, this, direction);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, FACING);
        super.appendProperties(builder);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("State: ").append(booleanToText(state.get(ACTIVE), "on", "off")));
        }
    }

    /** Get power being received in a given direction */
    public static int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        return dstState.getStrongRedstonePower(world, dstPos, direction);
    }
}
