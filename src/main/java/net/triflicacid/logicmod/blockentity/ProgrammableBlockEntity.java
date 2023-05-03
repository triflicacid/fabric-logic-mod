package net.triflicacid.logicmod.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.triflicacid.logicmod.blockentity.ModBlockEntity;
import net.triflicacid.logicmod.script.AST;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgrammableBlockEntity extends BlockEntity {
    public Set<Direction> inputs = new HashSet<>();
    public Set<Direction> outputs = new HashSet<>();
    private String program = "";
    private AST ast = new AST();
    private String lastError = null;
    public boolean needsUpdate = false;

    public ProgrammableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.PROGRAMMABLE, pos, state);
    }

    public String readProgram() {
        return program;
    }

    /**
     * Write a program
     */
    public void writeProgram(String prog) {
        program = prog;

        try {
            ast.symbols.clear();
            ast.build(program);
            lastError = null;
        } catch (AST.ParseError e) {
            lastError = e.getMessage();
        }

    }

    public boolean hasSymbol(String name) {
        return ast.symbols.containsKey(name);
    }

    public boolean getSymbol(String name) {
        return ast.symbols.getOrDefault(name, false);
    }

    public String[] getSymbols() {
        return ast.symbols.keySet().toArray(new String[0]);
    }

    public void setSymbol(String name, boolean value) {
        ast.symbols.put(name, value);
    }

    public boolean hasError() {
        return lastError != null;
    }

    @Nullable
    public String getError() {
        return lastError;
    }

    public String inputsToString() {
        return inputs.stream().map(Direction::asString).collect(Collectors.joining(","));
    }

    public String outputsToString() {
        return outputs.stream().map(Direction::asString).collect(Collectors.joining(","));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("Program", readProgram());
        nbt.putString("Inputs", inputsToString());
        nbt.putString("Outputs", outputsToString());
        ast.symbols.clear();
        needsUpdate = true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        writeProgram(nbt.getString("Program"));

        inputs.clear();
        inputs.addAll(Arrays.stream(nbt.getString("Inputs").split(",")).map(Direction::byName).filter(Objects::nonNull).toList());

        outputs.clear();
        outputs.addAll(Arrays.stream(nbt.getString("Outputs").split(",")).map(Direction::byName).filter(Objects::nonNull).toList());
    }

    public void eval() {
        if (!hasError()) {
            try {
                ast.eval();
            } catch (AST.ExecError e) {
                lastError = e.getMessage();
            }
        }
    }
}
