package net.triflicacid.logicmod.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.blockentity.ProgrammableBlockEntity;
import net.triflicacid.logicmod.interfaces.Analysable;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.*;

public class ProgrammableBlock extends Block implements Analysable, BlockEntityProvider {
    public static final String NAME = "programmable";

    public ProgrammableBlock() {
        super(Settings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakInstantly());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            update(world, state, pos);
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            BlockState sourceState = world.getBlockState(sourcePos);
            // Only update if: air (was destroyed), or could have a redstone signal
            if (sourceState.isOf(Blocks.AIR) || sourceState.emitsRedstonePower()) {
                update(world, state, pos);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(world, state, pos);
    }

    /** Update the block, with the update emanating from said direction (provided if neighborUpdate) */
    public void update(World world, BlockState state, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ProgrammableBlockEntity program) {
            boolean change = false;

            // Update inputs
            for (Direction direction : program.inputs) {
                boolean value = getPower(world, pos, state, direction) > 0;
                String name = direction.asString();
                if (!program.hasSymbol(name) || value != program.getSymbol(name)) {
                    program.setSymbol(name, value);
                    change = true;
                }
            }

            if (change || program.needsUpdate) {
                program.needsUpdate = false;
                program.eval();

                for (Direction direction : program.outputs) {
                    world.updateNeighbor(pos.offset(direction), this, pos);
                }
            }
        }
    }


    /** Get power being received in a given direction */
    public static int getPower(World world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos dstPos = pos.offset(direction);
        BlockState dstState = world.getBlockState(dstPos);
        return dstState.getStrongRedstonePower(world, dstPos, direction);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getStrongRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ProgrammableBlockEntity program) {
            Direction source = direction.getOpposite();
            return program.outputs.contains(source) ? program.getSymbol(source.asString()) ? 15 : 0 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ProgrammableBlockEntity(pos, state);
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();

        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ProgrammableBlockEntity program) {
            if (Screen.hasAltDown()) {
                for (String name : program.getSymbols()) {
                    messages.add(Text.literal("").append(specialToText(name)).append(" = ").append(booleanToText(program.getSymbol(name))));
                }
            } else {
                messages.add(Text.literal("Inputs: ").append(specialToText(program.inputsToString())));
                messages.add(Text.literal("Outputs: ").append(specialToText(program.outputsToString())));
                messages.add(Text.literal("Program: ").append(numberToText(program.readProgram().length())).append(" bytes"));
                messages.add(Text.literal("Error: ").append(program.hasError()
                        ? Text.literal(program.getError()).formatted(Formatting.RED)
                        : commentToText("none")));
                messages.add(commentToText("Press ALT to view variables"));
            }
        }

        return messages;
    }
}
