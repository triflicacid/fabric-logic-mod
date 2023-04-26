package net.triflicacid.logicmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;
import net.triflicacid.logicmod.interfaces.Wrenchable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.triflicacid.logicmod.util.Util.commentToText;

/**
 * Used on a block to rotate it
 *
 * By default, cycles the HorizontalDirectionfacing property, but default behaviour may be overridden using the Wrenchable.applyWrench method.
 */
public class WrenchItem extends Item {
    public static final String NAME = "wrench";

    public WrenchItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(commentToText("Use to rotate blocks"));
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
            BlockState newState = null;

            if (state.getBlock() instanceof Wrenchable wrenchableBlock) { // Has behaviour been overridden?
                newState = wrenchableBlock.applyWrench(world, pos, state, context.getSide(), context.getHorizontalPlayerFacing());
                if (newState != null) {
                    world.scheduleBlockTick(pos, state.getBlock(), 1, TickPriority.NORMAL);
                }
            } else if (state.contains(HorizontalFacingBlock.FACING)) { // Attempt to cycle the HorizontalFacing property
                Direction newDirection = state.get(HorizontalFacingBlock.FACING);
                newDirection = Screen.hasShiftDown() ? newDirection.rotateYCounterclockwise() : newDirection.rotateYClockwise();
                newState = state.with(HorizontalFacingBlock.FACING, newDirection);
            }

            if (newState == null) {
                return ActionResult.FAIL;
            } else {
                world.setBlockState(pos, newState);
                world.scheduleBlockTick(pos, newState.getBlock(), 1);
            }
        }

        return ActionResult.SUCCESS;
    }
}
