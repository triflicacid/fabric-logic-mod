package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * Defines a component which emits a constant, strong, pre-programmed signal.
 */
public class InputBlock extends AbstractPowerBlock implements AdvancedWrenchable {
    public static final String NAME = "input";

    /** If component is locked (receiving power from behind), don't output anything */
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    public InputBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return emitsRedstone && state.get(ACTIVE) && state.get(FACING) == direction? state.get(POWER) : 0;
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (!world.isClient && (from == null || from == state.get(FACING))) {
            boolean active = getPower(world, pos, state, state.get(FACING)) == 0;

            if (active != state.get(ACTIVE)) {
                world.setBlockState(pos, state.with(ACTIVE, active));
            }
        }
    }

    @Override
    /** Increment/decrement the signal we're outputting */
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (world.isClient)
            return null;

        int newPower = wrapInt(state.get(POWER) + (Screen.hasShiftDown() ? (-1) : 1), 0, 15);
        player.sendMessage(Text.literal("Set ").append(specialToText(POWER.getName())).append(" to ").append(numberToText(newPower)));
        return state.with(POWER, newPower);
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Active: ").append(booleanToText(state.get(ACTIVE))));
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
        super.appendProperties(builder);
    }
}
