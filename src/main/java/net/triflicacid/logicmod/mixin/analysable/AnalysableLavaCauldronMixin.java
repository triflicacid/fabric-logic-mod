package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.LavaCauldronBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.numberToText;
import static net.triflicacid.logicmod.util.Util.specialToText;

@Mixin(LavaCauldronBlock.class)
public class AnalysableLavaCauldronMixin implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Contents: ").append(specialToText("lava")));
        messages.add(Text.literal("Level: ").append(numberToText(LeveledCauldronBlock.MAX_LEVEL)));
        return messages;
    }
}
