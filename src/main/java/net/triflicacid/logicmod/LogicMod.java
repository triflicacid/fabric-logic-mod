package net.triflicacid.logicmod;

import net.fabricmc.api.ModInitializer;

import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.item.ModItemGroup;
import net.triflicacid.logicmod.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicMod implements ModInitializer {
	public static final String MOD_ID = "logic-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger("logic-mod");

	@Override
	public void onInitialize() {
		LOGGER.info("Initialising mod (MOD_ID=" + MOD_ID + ")");
		ModItemGroup.registerItemGroups();
		ModItems.registerItems();
		ModBlocks.registerBlocks();
	}
}