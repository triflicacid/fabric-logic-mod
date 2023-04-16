package net.triflicacid.logicmod.blockentity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.AbstractClockBlock;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;

public abstract class AbstractClockBlockEntity extends BlockEntity {
    private int onTickCount;
    private int offTickCount;
    private int ticks = 0; // How many ticks has passed since last toggle
    private boolean active = false;

    public AbstractClockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.CLOCK, pos, state);
    }

    public int getOnTickCount() {
        return onTickCount;
    }

    public void setOnTickCount(int count) {
        if (count < 1)
            return;

        onTickCount = count;
    }

    public int getOffTickCount() {
        return offTickCount;
    }

    public void setOffTickCount(int count) {
        if (count < 1)
            return;

        offTickCount = count;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("OnTickCount", getOnTickCount());
        nbt.putInt("OffTickCount", getOffTickCount());
        nbt.putInt("Ticks", this.ticks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        setOnTickCount(nbt.getInt("OnTickCount"));
        setOffTickCount(nbt.getInt("OffTickCount"));
        this.ticks = nbt.getInt("Ticks");
    }

    /** Increase ticks */
    public void registerTick() {
        ticks++;
        if (ticks > getCurrentTickCount()) {
            ticks = 0;
            active = !active;
        }
    }

    /** Get tick count for current state */
    protected int getCurrentTickCount() {
        return active ? onTickCount : offTickCount;
    }

    /** Is the clock active? */
    public boolean isActive() {
        return active;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractClockBlockEntity blockEntity) {
        if (!world.isClient() && !state.get(AbstractClockBlock.LOCKED)) {
            blockEntity.registerTick();

            if (state.get(AbstractClockBlock.ACTIVE) != blockEntity.isActive()) {
                ((AbstractClockBlock) state.getBlock()).update(state, (ServerWorld) world, pos, blockEntity.isActive());
            }
        }
    }
}
