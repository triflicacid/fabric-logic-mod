package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.triflicacid.logicmod.LogicMod;
import net.triflicacid.logicmod.block.custom.*;
import net.triflicacid.logicmod.block.custom.logicgate.*;
import net.triflicacid.logicmod.block.custom.wire.*;
import net.triflicacid.logicmod.item.ModItemGroup;

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
    public static final Block CLOCK = registerBlock(ClockBlock.NAME, new ClockBlock());
    public static final Block CONDITIONAL = registerBlock(ConditionalBlock.NAME, new ConditionalBlock());
    public static final Block EQUALITY = registerBlock(EqualityBlock.NAME, new EqualityBlock());

    /** Wires and adapters */
    public static final Block BLUE_WIRE = registerBlock(BlueWireBlock.NAME, new BlueWireBlock());
    public static final Block BLUE_WIRE_ADAPTER = registerBlock(BlueWireAdapterBlock.NAME, new BlueWireAdapterBlock());
    public static final Block GREEN_WIRE = registerBlock(GreenWireBlock.NAME, new GreenWireBlock());
    public static final Block GREEN_WIRE_ADAPTER = registerBlock(GreenWireAdapterBlock.NAME, new GreenWireAdapterBlock());
    public static final Block ORANGE_WIRE = registerBlock(OrangeWireBlock.NAME, new OrangeWireBlock());
    public static final Block ORANGE_WIRE_ADAPTER = registerBlock(OrangeWireAdapterBlock.NAME, new OrangeWireAdapterBlock());
    public static final Block PURPLE_WIRE = registerBlock(PurpleWireBlock.NAME, new PurpleWireBlock());
    public static final Block PURPLE_WIRE_ADAPTER = registerBlock(PurpleWireAdapterBlock.NAME, new PurpleWireAdapterBlock());
    public static final Block RED_WIRE = registerBlock(RedWireBlock.NAME, new RedWireBlock());
    public static final Block RED_WIRE_ADAPTER = registerBlock(RedWireAdapterBlock.NAME, new RedWireAdapterBlock());
    public static final Block YELLOW_WIRE = registerBlock(YellowWireBlock.NAME, new YellowWireBlock());
    public static final Block YELLOW_WIRE_ADAPTER = registerBlock(YellowWireAdapterBlock.NAME, new YellowWireAdapterBlock());


    /** Register block with no associated item */
    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(LogicMod.MOD_ID, name), block);
    }

    /** Register a block with an associated item, which will e placed in the provided ItemGroup */
    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registries.BLOCK, new Identifier(LogicMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        Item item = Registry.register(Registries.ITEM, new Identifier(LogicMod.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return item;
    }

    public static void registerBlocks() {
        LogicMod.LOGGER.info("Registering blocks...");
    }
}
