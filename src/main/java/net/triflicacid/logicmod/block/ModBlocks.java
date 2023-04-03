package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.triflicacid.logicmod.LogicMod;
import net.triflicacid.logicmod.block.custom.AndGateBlock;
import net.triflicacid.logicmod.block.custom.LogicGateBlock;
import net.triflicacid.logicmod.block.custom.NotGateBlock;
import net.triflicacid.logicmod.block.custom.OrGateBlock;

public class ModBlocks {
    public static final LogicGateBlock AND_GATE = (LogicGateBlock) registerBlock(AndGateBlock.BLOCK_NAME, new AndGateBlock());
    public static final LogicGateBlock NOT_GATE = (LogicGateBlock) registerBlock(NotGateBlock.BLOCK_NAME, new NotGateBlock());
    public static final LogicGateBlock OR_GATE = (LogicGateBlock) registerBlock(OrGateBlock.BLOCK_NAME, new OrGateBlock());


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
