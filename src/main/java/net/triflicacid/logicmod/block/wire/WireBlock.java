package net.triflicacid.logicmod.block.wire;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.triflicacid.logicmod.interfaces.AdvancedWrenchable;
import net.triflicacid.logicmod.interfaces.Analysable;
import net.triflicacid.logicmod.interfaces.WireConnectable;
import net.triflicacid.logicmod.util.PipeModel;
import net.triflicacid.logicmod.util.WireColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.triflicacid.logicmod.util.Util.numberToText;

/**
 * The concrete implementation of AbstractWireBlock
 */
public class WireBlock extends AbstractWireBlock implements Analysable, AdvancedWrenchable {
    public static final PipeModel MODEL = new PipeModel(3, 9);

    protected WireBlock(WireColor color) {
        super(FabricBlockSettings.of(Material.WOOL).sounds(BlockSoundGroup.WOOL).breakInstantly(), color);
        this.setDefaultState(PipeModel.setDefaultState(this.getDefaultState()));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return MODEL.getVoxelShape(state, world, pos, context);
    }

    /** Get the name of a wire block of a given color */
    public static String getName(WireColor color) {
        return color + "_wire";
    }

    @Override
    public List<Text> onAnalyse(World world, BlockPos pos, BlockState state, Direction side, Direction playerFacing) {
        List<Text> messages = new ArrayList<>();
        messages.add(Text.literal("Color: ").append(Text.literal(color.toString()).formatted(color.getFormatting())));
        messages.add(Text.literal("Power: ").append(numberToText(state.get(POWER))));
        return messages;
    }

    /** Should we be connected to the block in the given direction */
    private boolean shouldConnect(World world, BlockPos pos, Direction dir) {
        BlockState dstState = world.getBlockState(pos.offset(dir));
        return dstState.getBlock() instanceof WireConnectable connectable && connectable.shouldWireConnect(getWireColor());
    }

    /** Update model's direction states from every direction */
    protected void updateModel(World world, BlockState state, BlockPos pos) {
        boolean different = false;
        for (Direction dir : Direction.values()) {
            BooleanProperty prop = PipeModel.getPropertyFromDirection(dir);
            boolean bl = shouldConnect(world, pos, dir);
            if (bl != state.get(prop)) {
                state = state.with(prop, bl);
                different = true;
            }
        }

        if (different) {
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        }
    }

    /** Update model's states from one direction */
    protected void updateModel(World world, BlockState state, BlockPos pos, Direction dir) {
        BooleanProperty prop = PipeModel.getPropertyFromDirection(dir);
        boolean bl = shouldConnect(world, pos, dir);
        if (bl != state.get(prop)) {
            world.setBlockState(pos, state.with(prop, bl), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        PipeModel.appendBlockStates(builder);
        super.appendProperties(builder);
    }

    /** Return class instance for a wire of said color */
    public static WireBlock instantiate(WireColor color) {
        return new WireBlock(color) {};
    }

    @Override
    public @Nullable BlockState applyAdvancedWrench(World world, BlockPos pos, BlockState state, Direction side, PlayerEntity player, Direction playerFacing) {
        updateModel(world, state, pos);
        return null;
    }
}
