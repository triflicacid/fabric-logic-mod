package net.triflicacid.logicmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.triflicacid.logicmod.LogicMod;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.block.custom.*;
import net.triflicacid.logicmod.block.custom.logicgate.*;
import net.triflicacid.logicmod.block.custom.wire.*;
import net.triflicacid.logicmod.item.custom.AdvancedWrenchItem;
import net.triflicacid.logicmod.item.custom.AnalyserItem;
import net.triflicacid.logicmod.item.custom.WrenchItem;

public class ModItems {
    public static final Item WRENCH = addToItemGroup(registerItem(WrenchItem.NAME, new WrenchItem()), ModItemGroup.LOGIC);
    public static final Item ADV_WRENCH = addToItemGroup(registerItem(AdvancedWrenchItem.NAME, new AdvancedWrenchItem()), ModItemGroup.LOGIC);
    public static final Item ANALYSER = addToItemGroup(registerItem(AnalyserItem.NAME, new AnalyserItem()), ModItemGroup.LOGIC);

    /** Logic gates */
    public static final Item AND_GATE = registerAliased(ModBlocks.AND_GATE, AndGateBlock.NAME);
    public static final Item NAND_GATE = registerAliased(ModBlocks.NAND_GATE, NandGateBlock.NAME);
    public static final Item BUFFER_GATE = registerAliased(ModBlocks.BUFFER_GATE, BufferGateBlock.NAME);
    public static final Item NOT_GATE = registerAliased(ModBlocks.NOT_GATE, NotGateBlock.NAME);
    public static final Item OR_GATE = registerAliased(ModBlocks.OR_GATE, OrGateBlock.NAME);
    public static final Item NOR_GATE = registerAliased(ModBlocks.NOR_GATE, NorGateBlock.NAME);
    public static final Item XOR_GATE = registerAliased(ModBlocks.XOR_GATE, XorGateBlock.NAME);
    public static final Item XNOR_GATE = registerAliased(ModBlocks.XNOR_GATE, XnorGateBlock.NAME);

    public static final Item INPUT = registerAliased(ModBlocks.INPUT, InputBlock.NAME);
    public static final Item OUTPUT = registerAliased(ModBlocks.OUTPUT, OutputBlock.NAME);
    public static final Item PULSE = registerAliased(ModBlocks.PULSE, PulseBlock.NAME);
    public static final Item CLOCK = registerAliased(ModBlocks.CLOCK, ClockBlock.NAME);
    public static final Item CONDITIONAL = registerAliased(ModBlocks.CONDITIONAL, ConditionalBlock.NAME);
    public static final Item EQUALITY = registerAliased(ModBlocks.EQUALITY, EqualityBlock.NAME);
    public static final Item MEMORY_CELL = registerAliased(ModBlocks.MEMORY_CELL, MemoryCellBlock.NAME);
    public static final Item RANDOM = registerAliased(ModBlocks.RANDOM, RandomBlock.NAME);

    /** Wires and adapters */
    public static final Item BLUE_WIRE = registerAliased(ModBlocks.BLUE_WIRE, BlueWireBlock.NAME);
    public static final Item BLUE_WIRE_ADAPTER = registerAliased(ModBlocks.BLUE_WIRE_ADAPTER, BlueWireAdapterBlock.NAME);
    public static final Item GREEN_WIRE = registerAliased(ModBlocks.GREEN_WIRE, GreenWireBlock.NAME);
    public static final Item GREEN_WIRE_ADAPTER = registerAliased(ModBlocks.GREEN_WIRE_ADAPTER, GreenWireAdapterBlock.NAME);
    public static final Item ORANGE_WIRE = registerAliased(ModBlocks.ORANGE_WIRE, OrangeWireBlock.NAME);
    public static final Item ORANGE_WIRE_ADAPTER = registerAliased(ModBlocks.ORANGE_WIRE_ADAPTER, OrangeWireAdapterBlock.NAME);
    public static final Item PURPLE_WIRE = registerAliased(ModBlocks.PURPLE_WIRE, PurpleWireBlock.NAME);
    public static final Item PURPLE_WIRE_ADAPTER = registerAliased(ModBlocks.PURPLE_WIRE_ADAPTER, PurpleWireAdapterBlock.NAME);
    public static final Item RED_WIRE = registerAliased(ModBlocks.RED_WIRE, RedWireBlock.NAME);
    public static final Item RED_WIRE_ADAPTER = registerAliased(ModBlocks.RED_WIRE_ADAPTER, RedWireAdapterBlock.NAME);
    public static final Item YELLOW_WIRE = registerAliased(ModBlocks.YELLOW_WIRE, YellowWireBlock.NAME);
    public static final Item YELLOW_WIRE_ADAPTER = registerAliased(ModBlocks.YELLOW_WIRE_ADAPTER, YellowWireAdapterBlock.NAME);
    public static final Item BUS = registerAliased(ModBlocks.BUS, BusBlock.NAME);
    public static final Item BUS_ADAPTER = registerAliased(ModBlocks.BUS_ADAPTER, BusAdapterBlock.NAME);

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(LogicMod.MOD_ID, name), item);
    }

    private static Item addToItemGroup(Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return item;
    }

    /** Add a logic gate */
    private static Item registerAliased(Block block, String name) {
        return addToItemGroup(registerItem(name, new AliasedBlockItem(block, new FabricItemSettings())), ModItemGroup.LOGIC);
    }

    public static void registerItems() {
        LogicMod.LOGGER.info("Registering items...");
    }
}
