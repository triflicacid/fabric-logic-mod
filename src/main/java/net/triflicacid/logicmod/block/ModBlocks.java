package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.triflicacid.logicmod.LogicMod;
import net.triflicacid.logicmod.block.custom.*;
import net.triflicacid.logicmod.block.custom.adapter.BusAdapterBlock;
import net.triflicacid.logicmod.block.custom.adapter.JunctionBlock;
import net.triflicacid.logicmod.block.custom.adapter.WireAdapterBlock;
import net.triflicacid.logicmod.block.custom.logicgate.*;
import net.triflicacid.logicmod.block.custom.wire.BusBlock;
import net.triflicacid.logicmod.block.custom.wire.WireBlock;
import net.triflicacid.logicmod.item.ModItemGroup;
import net.triflicacid.logicmod.util.WireColor;

import java.util.HashMap;
import java.util.Map;

public class ModBlocks {
    public static final LogicGateBlock AND_GATE = (LogicGateBlock) registerBlock(AndGateBlock.NAME, new AndGateBlock());
    public static final LogicGateBlock NAND_GATE = (LogicGateBlock) registerBlock(NandGateBlock.NAME, new NandGateBlock());
    public static final LogicGateBlock BUFFER_GATE = (LogicGateBlock) registerBlock(BufferGateBlock.NAME, new BufferGateBlock());
    public static final LogicGateBlock NOT_GATE = (LogicGateBlock) registerBlock(NotGateBlock.NAME, new NotGateBlock());
    public static final LogicGateBlock OR_GATE = (LogicGateBlock) registerBlock(OrGateBlock.NAME, new OrGateBlock());
    public static final LogicGateBlock NOR_GATE = (LogicGateBlock) registerBlock(NorGateBlock.NAME, new NorGateBlock());
    public static final LogicGateBlock XOR_GATE = (LogicGateBlock) registerBlock(XorGateBlock.NAME, new XorGateBlock());
    public static final LogicGateBlock XNOR_GATE = (LogicGateBlock) registerBlock(XnorGateBlock.NAME, new XnorGateBlock());

    public static final Block INPUT = registerBlock(InputBlock.NAME, new InputBlock());
    public static final Block OUTPUT = registerBlock(OutputBlock.NAME, new OutputBlock());
    public static final Block PULSE = registerBlock(PulseBlock.NAME, new PulseBlock());
    public static final Block CLOCK = registerBlock(ClockBlock.NAME, new ClockBlock());
    public static final Block CONDITIONAL = registerBlock(ConditionalBlock.NAME, new ConditionalBlock());
    public static final Block EQUALITY = registerBlock(EqualityBlock.NAME, new EqualityBlock());
    public static final Block MEMORY_CELL = registerBlock(MemoryCellBlock.NAME, new MemoryCellBlock());
    public static final Block RANDOM = registerBlock(RandomBlock.NAME, new RandomBlock());

    /** Wires and adapters */
    public static final Map<WireColor, WireBlock> WIRES = new HashMap<>();
    public static final Map<WireColor, WireAdapterBlock> ADAPTERS = new HashMap<>();

    static {
        for (WireColor color : WireColor.values()) {
            WireBlock wire = WireBlock.instantiate(color);
            registerBlock(WireBlock.getName(color), wire);
            WIRES.put(color, wire);

            WireAdapterBlock adapter = WireAdapterBlock.instantiate(color);
            registerBlock(WireAdapterBlock.getName(color), adapter);
            ADAPTERS.put(color, adapter);
        }
    }

    public static final Block JUNCTION = registerBlock(JunctionBlock.NAME, new JunctionBlock(), ModItemGroup.LOGIC);
    public static final Block BUS = registerBlock(BusBlock.NAME, new BusBlock(BlockSoundGroup.WOOL));
    public static final Block BUS_ADAPTER = registerBlock(BusAdapterBlock.NAME, new BusAdapterBlock());


    /** Register block with no associated item */
    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, LogicMod.identify(name), block);
    }

    /** Register a block with an associated item, which will be placed in the provided ItemGroup */
    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registries.BLOCK, LogicMod.identify(name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        Item item = Registry.register(Registries.ITEM, LogicMod.identify(name), new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return item;
    }

    public static void registerBlocks() {
        LogicMod.LOGGER.info("Registering blocks...");
    }
}
