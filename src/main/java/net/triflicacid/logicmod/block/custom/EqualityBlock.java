package net.triflicacid.logicmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.util.Util;

import java.util.HashSet;
import java.util.Set;

import static net.triflicacid.logicmod.util.Util.booleanToText;
import static net.triflicacid.logicmod.util.Util.numberToText;

public class EqualityBlock extends AbstractPowerBlock {
    public static final String NAME = "equality";

    public EqualityBlock() {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true);
    }

    @Override
    public void update(World world, BlockState state, BlockPos pos) {
        int[] inputs = getInputs(state, world, pos);
        int power = Util.allEqual(inputs) ? inputs[0] : 0;

        if (power != state.get(POWER)) {
            world.setBlockState(pos, state.with(POWER, power));
        }
    }

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
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            int[] inputs = getInputs(state, world, pos);
            player.sendMessage(Text.literal("All Equal: ").append(booleanToText(Util.allEqual(inputs))));
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        }
    }
}
