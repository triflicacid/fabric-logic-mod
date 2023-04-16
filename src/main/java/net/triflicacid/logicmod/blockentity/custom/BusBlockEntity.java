package net.triflicacid.logicmod.blockentity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.util.WireColor;

import java.util.*;

import static net.triflicacid.logicmod.util.Util.capitalise;

public class BusBlockEntity extends BlockEntity {
    public static final List<WireColor> COLORS = Arrays.asList(WireColor.values());
    protected Map<WireColor, Integer> powerMap = new HashMap<>();

    protected static String getKeyName(WireColor color) {
        String str = color.toString();
        return capitalise(str) + "Power";
    }

    public BusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.BUS, pos, state);
        for (WireColor color : COLORS)
            powerMap.put(color, 0);
    }

    public int getPower(WireColor color) {
        return powerMap.get(color);
    }

    public Map<WireColor, Integer> getPowerMap() {
        return powerMap;
    }

    public void setPower(WireColor color, int value) {
        if (value < 0) value = 0;
        else if (value > 15) value = 15;
        powerMap.put(color, value);
    }

    public boolean arePowerMapsEqual(Map<WireColor, Integer> map) {
        for (WireColor color : COLORS) {
            if (!Objects.equals(powerMap.get(color), map.get(color)))
                return false;
        }
        return true;
    }

    public boolean containsPower() {
        for (WireColor color : COLORS)
            if (powerMap.get(color) > 0)
                return true;
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        for (WireColor color : powerMap.keySet()) {
            nbt.putInt(getKeyName(color), getPower(color));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        for (WireColor color : powerMap.keySet()) {
            setPower(color, nbt.getInt(getKeyName(color)));
        }
    }
}
