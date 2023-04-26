package net.triflicacid.logicmod.block.custom.wire;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.util.WireColor;

import static net.triflicacid.logicmod.util.Util.numberToText;
import static net.triflicacid.logicmod.util.Util.spawnRedstoneParticles;

/**
 * The concrete implementation of AbstractWireBlock
 */
public class WireBlock extends AbstractWireBlock implements Analysable {
    protected WireBlock(WireColor color) {
        super(BlockSoundGroup.WOOL, color);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(POWER) > 0;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (hasRandomTicks(state)) {
            spawnRedstoneParticles(world, pos);
        }
        super.randomDisplayTick(state, world, pos, random);
    }

    /** Get the name of a wire block of a given color */
    public static String getName(WireColor color) {
        return color + "_wire";
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("Color: ").append(Text.literal(color.toString()).formatted(color.getFormatting())));
            player.sendMessage(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        }
    }

    /** Return class instance for a wire of said color */
    public static WireBlock instantiate(WireColor color) {
        return new WireBlock(color) {};
    }
}
