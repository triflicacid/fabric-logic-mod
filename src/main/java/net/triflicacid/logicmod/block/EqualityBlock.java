package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.triflicacid.logicmod.util.Util.booleanToText;
import static net.triflicacid.logicmod.util.Util.numberToText;

/**
 * A block which emits a signal strength only if said signal is being received from all of its other faces.
 * Note that if there's not a redstone-emitting block adjacent to a face, that face will not be considered.
 */
public class EqualityBlock extends AbstractPowerBlock {
    public static final String NAME = "equality";

    public EqualityBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        if (world.isClient)
            return;

        int[] inputs = getInputs(state, world, pos);
        int power = Util.allEqual(inputs) ? inputs[0] : 0;

        if (power != state.get(POWER)) {
            world.setBlockState(pos, state.with(POWER, power));
        }
    }

    /** Get the signals being received from this block's faces */
    protected int[] getInputs(BlockState state, World world, BlockPos pos) {
        Set<Direction> directions = new HashSet<>();
        Direction facing = state.get(FACING);
        directions.add(facing.rotateYClockwise());
        directions.add(facing.rotateYCounterclockwise());
        if (world.getBlockState(pos.offset(facing)).emitsRedstonePower()) directions.add(facing);

        int[] inputs = new int[directions.size()];
        int i = 0;
        for (Direction direction : directions)
            inputs[i++] = getPower(world, pos, state, direction);

        return inputs;
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();

        int[] inputs = getInputs(state, world, pos);
        messages.add(Text.literal("All Equal: ").append(booleanToText(Util.allEqual(inputs))));
        messages.add(Text.literal("Power: ").append(numberToText(state.get(POWER))));

        return messages;
    }
}
