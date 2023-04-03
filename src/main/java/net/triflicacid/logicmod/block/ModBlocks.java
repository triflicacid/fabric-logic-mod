package net.triflicacid.logicmod.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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
import net.triflicacid.logicmod.block.custom.NotGateBlock;

public class ModBlocks {
    public static final Block NOT_GATE = registerBlock(NotGateBlock.BLOCK_NAME, new NotGateBlock(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).breakInstantly()));

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
