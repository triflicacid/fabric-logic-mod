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
import net.triflicacid.logicmod.block.custom.*;
import net.triflicacid.logicmod.item.custom.WrenchItem;

public class ModItems {
    public static final Item WRENCH = addToItemGroup(registerItem(WrenchItem.NAME, new WrenchItem(new FabricItemSettings())), ModItemGroup.LOGIC);

    public static final Item AND_GATE = registerLogicGate(ModBlocks.AND_GATE, AndGateBlock.ITEM_NAME);
    public static final Item NAND_GATE = registerLogicGate(ModBlocks.NAND_GATE, NandGateBlock.ITEM_NAME);
    public static final Item NOT_GATE = registerLogicGate(ModBlocks.NOT_GATE, NotGateBlock.ITEM_NAME);
    public static final Item OR_GATE = registerLogicGate(ModBlocks.OR_GATE, OrGateBlock.ITEM_NAME);
    public static final Item NOR_GATE = registerLogicGate(ModBlocks.NOR_GATE, NorGateBlock.ITEM_NAME);
    public static final Item XOR_GATE = registerLogicGate(ModBlocks.XOR_GATE, XorGateBlock.ITEM_NAME);
    public static final Item XNOR_GATE = registerLogicGate(ModBlocks.XNOR_GATE, XnorGateBlock.ITEM_NAME);

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(LogicMod.MOD_ID, name), item);
    }

    private static Item addToItemGroup(Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return item;
    }

    /** Add a logic gate */
    private static Item registerLogicGate(LogicGateBlock block, String name) {
        return addToItemGroup(registerItem(name, new AliasedBlockItem(block, new FabricItemSettings())), ModItemGroup.LOGIC);
    }

    public static void registerItems() {
        LogicMod.LOGGER.info("Registering items...");
    }
}
