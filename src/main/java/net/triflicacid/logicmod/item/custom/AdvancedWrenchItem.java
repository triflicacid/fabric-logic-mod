package net.triflicacid.logicmod.item.custom;

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
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdvancedWrenchItem extends Item {
    public static final String NAME = "advanced_wrench";

    public AdvancedWrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Configure some logical components").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
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

            if (state.getBlock() instanceof AdvancedWrenchable wrenchableBlock) {
                BlockState newState = wrenchableBlock.applyAdvancedWrench(state, Screen.hasShiftDown());
                if (newState != null) {
                    world.setBlockState(pos, newState);
                    doCooldown = true;
                }
            }

            // Set cooldown to prevent spamming
            if (doCooldown)
                context.getPlayer().getItemCooldownManager().set(this, 7);
        }

        return super.useOnBlock(context);
    }
}
