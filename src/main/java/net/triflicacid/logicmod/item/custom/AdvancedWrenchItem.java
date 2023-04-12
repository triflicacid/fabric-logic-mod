package net.triflicacid.logicmod.item.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
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
import net.minecraft.world.tick.TickPriority;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.triflicacid.logicmod.util.Util.commentToText;

public class AdvancedWrenchItem extends Item {
    public static final String NAME = "advanced_wrench";

    public AdvancedWrenchItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(commentToText("Use to configure logical components"));
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

            if (state.getBlock() instanceof AdvancedWrenchable wrenchableBlock) {
                BlockState newState = wrenchableBlock.applyAdvancedWrench(world, pos, state, context.getSide(), context.getPlayer(), context.getHorizontalPlayerFacing());
                if (newState != null) {
                    world.setBlockState(pos, newState);
                    world.scheduleBlockTick(pos, state.getBlock(), 1, TickPriority.NORMAL);
                }
            }
        }

        return super.useOnBlock(context);
    }
}
