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
import net.triflicacid.logicmod.block.custom.AndGateBlock;
import net.triflicacid.logicmod.block.custom.LogicGateBlock;
import net.triflicacid.logicmod.block.custom.NotGateBlock;
import net.triflicacid.logicmod.block.custom.OrGateBlock;
import net.triflicacid.logicmod.item.custom.WrenchItem;

public class ModItems {
    public static final Item WRENCH = registerItem(WrenchItem.NAME, new WrenchItem(new FabricItemSettings()));

    public static final Item AND_GATE = createLogicGate(ModBlocks.AND_GATE, AndGateBlock.ITEM_NAME);
    public static final Item NOT_GATE = createLogicGate(ModBlocks.NOT_GATE, NotGateBlock.ITEM_NAME);
    public static final Item OR_GATE = createLogicGate(ModBlocks.OR_GATE, OrGateBlock.ITEM_NAME);

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(LogicMod.MOD_ID, name), item);
    }

    private static Item addToItemGroup(Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return item;
    }

    /** Add a logic gate */
    private static Item createLogicGate(LogicGateBlock block, String name) {
        return addToItemGroup(registerItem(name, new AliasedBlockItem(block, new FabricItemSettings())), ModItemGroup.LOGIC);
    }

    public static void registerItems() {
        LogicMod.LOGGER.info("Registering items...");
    }
}
