package net.triflicacid.logicmod.mixin;

import net.minecraft.block.ButtonBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ButtonBlock.class)
public interface ButtonAccessor {
    @Accessor
    int getPressTicks();
}
