package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.booleanToText;
import static net.triflicacid.logicmod.util.Util.numberToText;

@Mixin(RepeaterBlock.class)
public class AnalysableRepeaterBlock implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("State: ").append(booleanToText(state.get(RepeaterBlock.POWERED), "on", "off")));
        messages.add(Text.literal("Delay: ").append(numberToText(state.get(RepeaterBlock.DELAY))));
        messages.add(Text.literal("Locked: ").append(booleanToText(state.get(RepeaterBlock.LOCKED))));
        return messages;
    }
}
