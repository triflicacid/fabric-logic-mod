package net.triflicacid.logicmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.triflicacid.logicmod.LogicMod;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.block.custom.NotGateBlock;
import net.triflicacid.logicmod.item.custom.WrenchItem;

public class ModItems {
    public static final Item WRENCH = registerItem(WrenchItem.NAME, new WrenchItem(new FabricItemSettings()));
    public static final Item NOT_GATE = registerItem(NotGateBlock.ITEM_NAME, new AliasedBlockItem(ModBlocks.NOT_GATE, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(LogicMod.MOD_ID, name), item);
    }

    public static void addItemsToItemGroup() {
        addToItemGroup(WRENCH, ModItemGroup.LOGIC);
        addToItemGroup(NOT_GATE, ModItemGroup.LOGIC);
    }

    private static void addToItemGroup(Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }

    public static void registerItems() {
        LogicMod.LOGGER.info("Registering items...");
        addItemsToItemGroup();
    }
}
