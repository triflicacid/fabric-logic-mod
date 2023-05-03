package net.triflicacid.logicmod.mixin.analysable;

import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;
import static net.triflicacid.logicmod.util.Util.booleanToText;

@Mixin(NoteBlock.class)
public class AnalysableNoteBlock implements Analysable {
    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        int note = state.get(NoteBlock.NOTE);

        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Instrument: ").append(specialToText(state.get(NoteBlock.INSTRUMENT).toString().toLowerCase())));
        messages.add(Text.literal("Note: ").append(noteToText(note)).append(" (").append(numberToText(note)).append(")"));
        messages.add(Text.literal("Pitch: ").append(numberToText(getNotePitch(note))));
        messages.add(Text.literal("Powered: ").append(booleanToText(state.get(NoteBlock.POWERED))));
        return messages;
    }
}
