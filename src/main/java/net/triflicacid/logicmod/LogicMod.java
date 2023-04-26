package net.triflicacid.logicmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.item.ModItemGroup;
import net.triflicacid.logicmod.item.ModItems;
import net.triflicacid.logicmod.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	   A Minecraft mod which adds basic logical components into the game.
 *     Copyright (C) 2023 TriflicAcid
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class LogicMod implements ModInitializer {
	public static final String MOD_ID = "logic-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger("logic-mod");

	/** Get identifier for thing in this mod's namespace */
	public static Identifier identify(String thing) {
		return new Identifier(MOD_ID, thing);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initialising mod (MOD_ID=" + MOD_ID + ")");
		ModItemGroup.registerItemGroups();
		ModBlockEntity.registerBlockEntities();
		ModItems.registerItems();
		ModBlocks.registerBlocks();
		ModScreenHandlers.register();
	}
}