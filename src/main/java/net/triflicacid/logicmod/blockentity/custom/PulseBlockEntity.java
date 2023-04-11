package net.triflicacid.logicmod.blockentity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.PulseBlock;
import net.triflicacid.logicmod.block.custom.SignalRecieverBlock;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;

public class PulseBlockEntity extends BlockEntity {
    private int duration = 10;
    private int ticks = 0; // How many ticks has passed since last toggle
    private boolean active = false;
    public int lastBehind = 0;

    public PulseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.PULSE, pos, state);
    }

    public int getDuration() {
        return duration;
    }

    public void incrementDuration(int delta) { setDuration(duration + delta); };

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
            int behind = SignalRecieverBlock.getPower(world, pos, state, state.get(PulseBlock.FACING));
            if (behind != blockEntity.lastBehind || blockEntity.active) {
                blockEntity.lastBehind = behind;
                if (blockEntity.active) {
                    blockEntity.registerTick();
                } else if (behind > 0) {
                    blockEntity.setActive(true);
                }

                if (state.get(PulseBlock.ACTIVE) != blockEntity.isActive()) {
                    ((PulseBlock) state.getBlock()).update(state, (ServerWorld) world, pos);
                }
            }
        }
    }
}
