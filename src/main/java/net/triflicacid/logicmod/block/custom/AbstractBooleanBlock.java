package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import org.jetbrains.annotations.Nullable;

import static net.triflicacid.logicmod.util.Util.booleanToText;

public abstract class AbstractBooleanBlock extends AbstractDirectionalRedstoneBlock implements Analysable {
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
        super.appendProperties(builder);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("State: ").append(booleanToText(state.get(ACTIVE), "on", "off")));
        }
    }
}
