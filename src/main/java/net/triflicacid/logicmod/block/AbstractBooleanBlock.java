package net.triflicacid.logicmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.booleanToText;

/**
 * Represent a block which emits a *strong* boolean redstone signal -- either HI or LO -- in the direction we're facing.
 */
public abstract class AbstractBooleanBlock extends AbstractDirectionalBlock implements Analysable {
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
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("State: ").append(booleanToText(state.get(ACTIVE), "on", "off")));
        return messages;
    }
}
