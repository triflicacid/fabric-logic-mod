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

public class ModBlocks {
    public static final LogicGateBlock AND_GATE = (LogicGateBlock) registerBlock(AndGateBlock.BLOCK_NAME, new AndGateBlock());
    public static final LogicGateBlock NAND_GATE = (LogicGateBlock) registerBlock(NandGateBlock.BLOCK_NAME, new NandGateBlock());
    public static final LogicGateBlock BUFFER_GATE = (LogicGateBlock) registerBlock(BufferGateBlock.BLOCK_NAME, new BufferGateBlock());
    public static final LogicGateBlock NOT_GATE = (LogicGateBlock) registerBlock(NotGateBlock.BLOCK_NAME, new NotGateBlock());
    public static final LogicGateBlock OR_GATE = (LogicGateBlock) registerBlock(OrGateBlock.BLOCK_NAME, new OrGateBlock());
    public static final LogicGateBlock NOR_GATE = (LogicGateBlock) registerBlock(NorGateBlock.BLOCK_NAME, new NorGateBlock());
    public static final LogicGateBlock XOR_GATE = (LogicGateBlock) registerBlock(XorGateBlock.BLOCK_NAME, new XorGateBlock());
    public static final LogicGateBlock XNOR_GATE = (LogicGateBlock) registerBlock(XnorGateBlock.BLOCK_NAME, new XnorGateBlock());

    public static final Block CONSTANT_LO = registerBlock(ConstantLoBlock.BLOCK_NAME, new ConstantLoBlock());
    public static final Block CONSTANT_HI = registerBlock(ConstantHiBlock.BLOCK_NAME, new ConstantHiBlock());
    public static final Block CLOCK = registerBlock(ClockBlock.BLOCK_NAME, new ClockBlock());

    /** Wires and adapters */
    public static final Block BLUE_WIRE = registerBlock(BlueWireBlock.BLOCK_NAME, new BlueWireBlock());
    public static final Block BLUE_WIRE_ADAPTER = registerBlock(BlueWireAdapterBlock.BLOCK_NAME, new BlueWireAdapterBlock());
    public static final Block GREEN_WIRE = registerBlock(GreenWireBlock.BLOCK_NAME, new GreenWireBlock());
    public static final Block GREEN_WIRE_ADAPTER = registerBlock(GreenWireAdapterBlock.BLOCK_NAME, new GreenWireAdapterBlock());
    public static final Block RED_WIRE = registerBlock(RedWireBlock.BLOCK_NAME, new RedWireBlock());
    public static final Block RED_WIRE_ADAPTER = registerBlock(RedWireAdapterBlock.BLOCK_NAME, new RedWireAdapterBlock());
    public static final Block YELLOW_WIRE = registerBlock(YellowWireBlock.BLOCK_NAME, new YellowWireBlock());
    public static final Block YELLOW_WIRE_ADAPTER = registerBlock(YellowWireAdapterBlock.BLOCK_NAME, new YellowWireAdapterBlock());


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
