package net.triflicacid.logicmod.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchItem extends Item {
    public static final String NAME = "wrench";

    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Able to rotate some blocks").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
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
            boolean doCooldown = false;

            if (state.contains(HorizontalFacingBlock.FACING)) {
                Direction newDirection = state.get(HorizontalFacingBlock.FACING).rotateYClockwise();
                world.setBlockState(pos, state.with(HorizontalFacingBlock.FACING, newDirection));
                doCooldown = true;
            }

            // Set cooldown to prevent spamming
            if (doCooldown)
                context.getPlayer().getItemCooldownManager().set(this, 5);
        }

        return super.useOnBlock(context);
    }
}
