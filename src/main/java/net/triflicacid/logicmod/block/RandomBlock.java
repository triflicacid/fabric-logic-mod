package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * Outputs the stored signal strength. A new signal strength will be generated randomly when a signal is received
 */
public class RandomBlock extends AbstractPowerBlock implements AdvancedWrenchable {
    public static final String NAME = "random";
    /** Random : either 0/15, or any number in 0-15. */
    public static final BooleanProperty BINARY = BooleanProperty.of("binary");
    /** Was the line behind us active or inactive? */
    public static final BooleanProperty LAST_STATE = BooleanProperty.of("last_state");

    public RandomBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
        this.setDefaultState(this.getDefaultState().with(BINARY, false).with(LAST_STATE, false));
    }

    /** Generate a new random signal, taking into account the value of BINARY */
    protected final int getRandom(BlockState state) {
        if (state.get(BINARY))
            return Math.random() < 0.5 ? 0 : 15;
        return (int) (Math.random() * 16);
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient && (from == null || from == state.get(FACING))) { // Only permit updates from input
            boolean active = getPower(world, pos, state, state.get(FACING)) > 0;

            if (active != state.get(LAST_STATE)) {
                state = state.with(LAST_STATE, active);

                if (active) { // Only update on rising edge
                    state = state.with(POWER, getRandom(state));
                }

                world.setBlockState(pos, state);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAST_STATE, BINARY);
        super.appendProperties(builder);
    }

    @Override
    /** Toggle between binary and non-binary random generators */
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        player.sendMessage(Text.literal("Set ").append(specialToText(BINARY.getName())).append(" to ").append(booleanToText(!state.get(BINARY))));
        return state.cycle(BINARY);
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
            List<Text> messages = new ArrayList<>();
            messages.add(Text.literal("Power: ").append(numberToText(state.get(POWER))));
            messages.add(Text.literal("Binary: ").append(booleanToText(state.get(BINARY))));
            return messages;
    }
}
