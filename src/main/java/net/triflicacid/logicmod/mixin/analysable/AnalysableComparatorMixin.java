package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.booleanToText;
import static net.triflicacid.logicmod.util.Util.specialToText;

@Mixin(ComparatorBlock.class)
public class AnalysableComparatorMixin implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("State: ").append(booleanToText(state.get(ComparatorBlock.POWERED), "on", "off")));
        messages.add(Text.literal("Mode: ").append(specialToText(state.get(ComparatorBlock.MODE).toString())));
        return messages;
    }
}
