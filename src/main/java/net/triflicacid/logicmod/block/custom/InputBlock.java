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
        int newPower = state.get(POTENTIAL_POWER) + (Screen.hasShiftDown() ? (-1) : 1);
        if (newPower < 0) newPower = 15;
        else if (newPower > 15) newPower = 0;
        player.sendMessage(Text.literal("Set " + POTENTIAL_POWER.getName() + " to ").append(Text.literal(String.valueOf(newPower)).formatted(Formatting.GOLD)));
        return state.with(POTENTIAL_POWER, newPower);
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
