package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

@Mixin(LeveledCauldronBlock.class)
public class AnalysableLeveledCauldronMixin implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        String fluid = null;
        if (state.isOf(Blocks.WATER_CAULDRON)) {
            fluid = "water";
        } else if (state.isOf(Blocks.POWDER_SNOW_CAULDRON)) {
            fluid = "powdered snow";
        }

        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Contents: ").append(fluid == null ? commentToText("unknown") : specialToText(fluid)));
        messages.add(Text.literal("Level: ").append(numberToText(state.get(LeveledCauldronBlock.LEVEL))));
        return messages;
    }
}
