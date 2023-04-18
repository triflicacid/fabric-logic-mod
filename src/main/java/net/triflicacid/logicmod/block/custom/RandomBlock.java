package net.triflicacid.logicmod.block.custom;

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

import static net.triflicacid.logicmod.util.Util.*;

public class RandomBlock extends AbstractPowerBlock implements AdvancedWrenchable {
    public static final String NAME = "random";
    public static final BooleanProperty BINARY = BooleanProperty.of("binary");
    public static final BooleanProperty LAST_STATE = BooleanProperty.of("last_state"); // Was the line behind us active or inactive?

    public RandomBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
        this.setDefaultState(this.getDefaultState().with(BINARY, false).with(LAST_STATE, false));
    }

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
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        player.sendMessage(Text.literal("Set ").append(specialToText(BINARY.getName())).append(" to ").append(booleanToText(!state.get(BINARY))));
        return state.cycle(BINARY);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
            player.sendMessage(Text.literal("Binary: ").append(booleanToText(state.get(BINARY))));
        }
    }
}
