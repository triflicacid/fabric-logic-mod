package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.mixin.ButtonAccessor;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

@Mixin(ButtonBlock.class)
public class AnalysableButtonMixin implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        int duration = ((ButtonAccessor) this).getPressTicks();

        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("State: ").append(booleanToText(state.get(ButtonBlock.POWERED), "on", "off")));
        messages.add(Text.literal("Pulse Duration: ").append(duration == -1 ? commentToText("unknown") : numberToText(duration)));
        return messages;
    }
}
