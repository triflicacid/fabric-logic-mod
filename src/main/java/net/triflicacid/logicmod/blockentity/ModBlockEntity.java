package net.triflicacid.logicmod.blockentity;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Util;
import net.triflicacid.logicmod.LogicMod;
import net.triflicacid.logicmod.block.ModBlocks;
import net.triflicacid.logicmod.block.custom.ClockBlock;
import net.triflicacid.logicmod.block.custom.PulseBlock;
import net.triflicacid.logicmod.block.custom.wire.BusBlock;
import net.triflicacid.logicmod.blockentity.custom.BusBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.ClockBlockEntity;
import net.triflicacid.logicmod.blockentity.custom.PulseBlockEntity;

public class ModBlockEntity {
    public static BlockEntityType<ClockBlockEntity> CLOCK = registerBlockEntity(ClockBlock.NAME, BlockEntityType.Builder.create(ClockBlockEntity::new, ModBlocks.CLOCK));
    public static BlockEntityType<BusBlockEntity> BUS = registerBlockEntity(BusBlock.NAME, BlockEntityType.Builder.create(BusBlockEntity::new, ModBlocks.BUS, ModBlocks.BUS_ADAPTER));
    public static BlockEntityType<PulseBlockEntity> PULSE = registerBlockEntity(PulseBlock.NAME, BlockEntityType.Builder.create(PulseBlockEntity::new, ModBlocks.PULSE));

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType.Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, name, builder.build(type));
    }

    public static void registerBlockEntities() {
        LogicMod.LOGGER.info("Registering block entities...");
    }
}
