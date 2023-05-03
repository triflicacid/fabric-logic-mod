package net.triflicacid.logicmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.numberToText;

/**
 * Represent a block which emits a *strong* redstone signal between 0 and 15 in the direction we're facing.
 */
public abstract class AbstractPowerBlock extends AbstractDirectionalBlock implements Analysable {
    public static final IntProperty POWER = Properties.POWER;
    protected final boolean emitsRedstone;
    protected int tickDelay = 0;

    public AbstractPowerBlock(Settings settings, boolean emitsRedstone) {
        this(settings, emitsRedstone, 0);
    }

    public AbstractPowerBlock(Settings settings, boolean emitsRedstone, int initialPower) {
        super(settings);
        this.emitsRedstone = emitsRedstone;
        this.setDefaultState(this.getDefaultState().with(POWER, initialPower));
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
        return emitsRedstone && state.get(FACING) == direction ? state.get(POWER) : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        super.appendProperties(builder);
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        return messages;
    }
}
