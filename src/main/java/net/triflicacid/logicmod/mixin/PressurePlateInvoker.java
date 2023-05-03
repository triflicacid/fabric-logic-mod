package net.triflicacid.logicmod.mixin;

import net.minecraft.block.AbstractPressurePlateBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractPressurePlateBlock.class)
public interface PressurePlateInvoker {
    @Invoker("getTickRate")
    int invokeGetTickRate();
}
