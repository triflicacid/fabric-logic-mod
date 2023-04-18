package net.triflicacid.logicmod.block.custom.logicgate;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.triflicacid.logicmod.block.custom.AbstractBooleanBlock;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;

import java.util.*;

import static net.triflicacid.logicmod.util.Util.messageAll;
import static net.triflicacid.logicmod.util.Util.specialToText;

public abstract class LogicGateBlock extends AbstractBooleanBlock implements AdvancedWrenchable, Analysable {
    public static final int TICK_DELAY = 2;
    private final int minInputs;
    private final int maxInputs;

    public LogicGateBlock(int inputCount, boolean initiallyActive) {
        this(inputCount, inputCount, initiallyActive);
    }

    public LogicGateBlock(int minInputs, int maxInputs, boolean initiallyActive) {
        super(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly(), true, initiallyActive);
        if (minInputs < 1 || minInputs > 3) {
            throw new IllegalStateException("Invalid number of minInputs to logic gate: " + minInputs);
        }
        if (maxInputs < 1 || maxInputs > 3) {
            throw new IllegalStateException("Invalid number of maxInputs to logic gate: " + maxInputs);
        }
        if (maxInputs < minInputs) {
            throw new IllegalStateException("Invalid relation of maxInputs to minInputs to logic gate: " + minInputs + " and " + maxInputs);
        }
        this.minInputs = minInputs;
        this.maxInputs = maxInputs;
    }

    public abstract boolean logicalFunction(boolean[] inputs);

    public abstract String getType();

    /** Return array indicating if each direction returned by getInputDirections is receiving power */
    public boolean[] areInputsReceivingPower(World world, BlockPos pos, BlockState state) {
        Direction dirFacing = state.get(FACING);
        Direction dirLeft = dirFacing.rotateYCounterclockwise();
        Direction dirRight = dirFacing.rotateYClockwise();
        Set<Direction> directions = new HashSet<>();

        BlockState stateFacing = world.getBlockState(pos.offset(dirFacing));
        if (minInputs == 1 && maxInputs == 1) {
            directions.add(dirFacing);
        } else {
            BlockState stateLeft = world.getBlockState(pos.offset(dirLeft));
            BlockState stateRight = world.getBlockState(pos.offset(dirRight));
            if (minInputs == 2 && maxInputs == 2) {
                directions.add(dirLeft);
                directions.add(dirRight);
            } else if (minInputs == 3 && maxInputs == 3) {
                directions.add(dirFacing);
                directions.add(dirLeft);
                directions.add(dirRight);
            } else {
                if (!stateFacing.isAir()) directions.add(dirFacing);
                if (!stateLeft.isAir()) directions.add(dirLeft);
                if (!stateRight.isAir()) directions.add(dirRight);
            }
        }

        boolean[] receiving = new boolean[Math.max(directions.size(), minInputs)];

        Iterator<Direction> it = directions.iterator();
        int i = 0;
        while (it.hasNext()) {
            receiving[i++] = getPower(world, pos, state, it.next()) > 0;
        }

        while (i < minInputs) {
            receiving[i++] = false;
        }

        return receiving;
    }

    public void update(World world, BlockState state, BlockPos pos, Direction from) {
        boolean isActive = state.get(ACTIVE);
        boolean shouldBeActive = logicalFunction(areInputsReceivingPower(world, pos, state));

        if (isActive != shouldBeActive) {
            world.setBlockState(pos, state.with(ACTIVE, shouldBeActive));
        }
    }

    public abstract boolean isNotVariant();

    public abstract LogicGateBlock getInverse();

    @Override
    public BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            LogicGateBlock inverse = this.getInverse();
            if (inverse == null) {
                return null;
            } else {
                return inverse.getDefaultState().with(FACING, state.get(FACING));
            }
        } else {
            return null;
        }
    }

    @Override
    public void onAnalyse(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        if (!world.isClient) {
            super.onAnalyse(world, pos, state, side, player, playerFacing);
            player.sendMessage(Text.literal("Gate type: ").append(specialToText(getType())));
        }
    }
}
