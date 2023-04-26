package net.triflicacid.logicmod.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.AbstractPowerBlock;
import net.triflicacid.logicmod.block.PulseBlock;

/**
 * Blockentity for PulseBlock to keep track of our pulse length, and to make sure that pulses do not overlap.
 */
public class PulseBlockEntity extends BlockEntity {
    /** Pulse duration in ticks */
    private int duration = 10;
    /** How many ticks has passed since last toggle? */
    private int ticks = 0;
    /** Is the PulseBlock currently active? */
    private boolean active = false;
    /** The last signal strength we received from behind.
     * Used to make sure that the current pulse is finished before starting a new one.
     */
    public int lastBehind = 0;

    public PulseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.PULSE, pos, state);
    }

    public int getDuration() {
        return duration;
    }

    public void incrementDuration(int delta) { setDuration(duration + delta); }

    public void setDuration(int count) {
        if (count < 1)
            return;

        duration = count;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Duration", getDuration());
        nbt.putBoolean("Active", this.active);
        nbt.putInt("Ticks", this.ticks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        setDuration(nbt.getInt("Duration"));
        this.ticks = nbt.getInt("Ticks");
        this.active = nbt.getBoolean("Active");
    }

    public void registerTick() {
        ticks++;
        if (ticks > duration) {
            ticks = 0;
            active = !active;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.ticks = 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, PulseBlockEntity blockEntity) {
        if (!world.isClient()) {
            int behind = AbstractPowerBlock.getPower(world, pos, state, state.get(PulseBlock.FACING));
            if (behind != blockEntity.lastBehind || blockEntity.active) {
                blockEntity.lastBehind = behind;
                if (blockEntity.active) {
                    blockEntity.registerTick();
                } else if (behind > 0) {
                    blockEntity.setActive(true);
                }

                boolean shouldBeActive = blockEntity.isActive();
                if (state.get(PulseBlock.ACTIVE) != shouldBeActive) {
                    ((PulseBlock) state.getBlock()).update(world, state, pos, shouldBeActive);
                }
            }
        }
    }
}
