package net.triflicacid.logicmod.item;

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
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

/**
 * Used to receive information on a block.
 *
 * Reporting may be implemented by overriding the block's Analysable.onAnalyse method, or (secondly) by adding to the
 * Analysable.analyse static method.
 */
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
                List<Text> messages = analysable.onAnalyse(world, pos, state, context.getSide(), context.getHorizontalPlayerFacing());
                for (Text line : messages)
                    player.sendMessage(line);
            } else {
                return ActionResult.FAIL;
            }
        }

        return ActionResult.SUCCESS;
    }
}
