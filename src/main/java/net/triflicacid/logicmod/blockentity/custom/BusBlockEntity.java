package net.triflicacid.logicmod.blockentity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.util.WireColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BusBlockEntity extends BlockEntity {
    protected static final List<WireColor> COLORS = Arrays.stream(WireColor.values()).filter(c -> c != WireColor.WHITE).collect(Collectors.toList());
    protected Map<WireColor, Integer> powerMap = new HashMap<>();

    protected static String getKeyName(WireColor color) {
        String str = color.toString();
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() + "Power";
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
            if (powerMap.get(color) != map.get(color))
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
