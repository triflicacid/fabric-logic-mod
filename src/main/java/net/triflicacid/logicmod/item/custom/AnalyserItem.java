package net.triflicacid.logicmod.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.util.Analyse;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

public class AnalyserItem extends Item {
    public static final String NAME = "analyser";

    public AnalyserItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Use to analyse blocks").formatted(Formatting.GRAY, Formatting.ITALIC));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient() && context.getHand() == Hand.MAIN_HAND) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            PlayerEntity player = context.getPlayer();

            player.sendMessage(Text.literal("Block: ").append(blockToText(block)));
            if (block instanceof Analysable analysable) {
                analysable.onAnalyse(world, pos, state, context.getSide(), player, context.getHorizontalPlayerFacing());
            } else {
                Text[] message = Analyse.analyseBlock(world, pos, state, block);
                if (message == null) {
//                player.sendMessage(commentToText("No further information"));
                } else {
                    for (Text line : message)
                        player.sendMessage(line);
                }
            }
        }

        return super.useOnBlock(context);
    }
}
