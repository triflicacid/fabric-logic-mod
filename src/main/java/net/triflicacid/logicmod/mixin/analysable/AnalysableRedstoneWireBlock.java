package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.numberToText;

@Mixin(RedstoneWireBlock.class)
public class AnalysableRedstoneWireBlock implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Power: ").append(numberToText(state.get(RedstoneWireBlock.POWER))));
        return messages;
    }
}
