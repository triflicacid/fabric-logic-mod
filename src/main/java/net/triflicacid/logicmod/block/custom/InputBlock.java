package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import static net.triflicacid.logicmod.util.Util.*;

public class InputBlock extends SignalIOBlock implements AdvancedWrenchable {
    public static final String NAME = "input";
    public static final IntProperty POTENTIAL_POWER = IntProperty.of("potential_power", 0, 15);

    public InputBlock() {
        super(0);
    }

    @Override
    public int getSignalStrength(BlockState state, World world, BlockPos pos) {
        return getPower(world, pos, state, state.get(FACING)) == 0 ? state.get(POTENTIAL_POWER) : 0;
    }

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        int newPower = wrapInt(state.get(POTENTIAL_POWER) + (Screen.hasShiftDown() ? (-1) : 1), 0, 15);
        player.sendMessage(Text.literal("Set ").append(specialToText(POTENTIAL_POWER.getName())).append(" to ").append(numberToText(newPower)));
        return state.with(POTENTIAL_POWER, newPower);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POTENTIAL_POWER))));
            player.sendMessage(Text.literal("Disabled: ").append(booleanToText(getPower(world, pos, state, state.get(FACING)) > 0)));
        }
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POTENTIAL_POWER);
        super.appendProperties(builder);
    }
}
