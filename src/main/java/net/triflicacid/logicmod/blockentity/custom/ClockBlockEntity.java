package net.triflicacid.logicmod.blockentity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.ClockBlock;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;

/**
 * The block entity for AbstractClockBlock -- track the tick we are on, and update the clock's state when dictated by on/off tick count.
 */
public class ClockBlockEntity extends BlockEntity {
    /** How many ticks are we active for? */
    private int onTickCount;
    /** How many ticks are we inactive for? */
    private int offTickCount;
    /** How many ticks has passed since last toggle */
    private int ticks = 0;
    /** Are we currently active or inactive? Used to set the ACTIVE property of the clock block */
    private boolean active = false;

    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.CLOCK, pos, state);

        setOnTickCount(20);
        setOffTickCount(20);
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

    /** Register a tick and increment, updating internal properties as necessary.
     * Check to see if the associated ClockBlock's ACTIVE property needs updating
     */
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

    public static void tick(World world, BlockPos pos, BlockState state, ClockBlockEntity blockEntity) {
        if (!world.isClient() && !state.get(ClockBlock.LOCKED)) {
            blockEntity.registerTick();

            if (state.get(ClockBlock.ACTIVE) != blockEntity.isActive()) {
                ((ClockBlock) state.getBlock()).update(state, (ServerWorld) world, pos, blockEntity.isActive());
            }
        }
    }
}
