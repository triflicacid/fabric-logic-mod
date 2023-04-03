package net.triflicacid.logicmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.World;

public abstract class AbstractTickingBlock extends Block implements BlockEntityProvider {
    public AbstractTickingBlock(Settings settings) {
        super(settings);
    }

    protected static <T extends BlockEntity> BlockEntityTicker<T> checkType(World world, BlockEntityType<T> givenType, BlockEntityType<? extends AbstractFurnaceBlockEntity> expectedType) {
        return world.isClient ? null : checkType2(givenType, expectedType, AbstractFurnaceBlockEntity::tick);
    }

    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType2(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}
