package net.triflicacid.logicmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.triflicacid.logicmod.LogicMod;

public class ModItemGroup {
    public static ItemGroup LOGIC;

    public static void registerItemGroups() {
        LogicMod.LOGGER.info("Registering item groups...");

        LOGIC = FabricItemGroup.builder(new Identifier(LogicMod.MOD_ID, "base"))
                .displayName(Text.translatable("itemgroup.logic-mod"))
                .icon(() -> new ItemStack(ModItems.WRENCH))
                .build();
    }
}
