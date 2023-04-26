package net.triflicacid.logicmod.script;

import java.util.*;
import java.util.stream.Collectors;

public class AST {
    public static class ParseError extends Exception {
        public ParseError(int idx, String message) {
            super("Parse error @ " + idx + ": " + message);
        }
    }

    public static class ExecError extends Exception {
        public ExecError(int idx, String message) {
            super("Execution error @ " + idx + ": " + message);
        }
    }

    private final List<Token> lines = new ArrayList<>();
    public final Map<String, Boolean> symbols = new HashMap<>();

    public boolean eval() throws ExecError {
        boolean last = false;
        for (Token line : lines) {
            last = line.eval(this);
        }
        return last;
    }

    public String toString() {
        return lines.stream().map(Object::toString).collect(Collectors.joining(";\n", "", ";"));
    }

    public static final Map<String, BooleanFunctions.BooleanFunction> funcs = new HashMap<>();
    static {
        funcs.put(BooleanFunctions.Random.NAME, new BooleanFunctions.Random());
        funcs.put(BooleanFunctions.Not.NAME, new BooleanFunctions.Not());
        funcs.put(BooleanFunctions.And.NAME, new BooleanFunctions.And());
        funcs.put(BooleanFunctions.Nand.NAME, new BooleanFunctions.Nand());
        funcs.put(BooleanFunctions.Or.NAME, new BooleanFunctions.Or());
        funcs.put(BooleanFunctions.Nor.NAME, new BooleanFunctions.Nor());
        funcs.put(BooleanFunctions.Xor.NAME, new BooleanFunctions.Xor());
        funcs.put(BooleanFunctions.Xnor.NAME, new BooleanFunctions.Xnor());
    }

    public void build(String input) throws ParseError {
        lines.clear();
        int i = 0, end = input.length();
        while (true) {
            int lineEnd = findNext(input, i, ';');
            if (lineEnd == -1)
                lineEnd = end;
            String str = input.substring(i, lineEnd);
            if (str.length() != 0) {
                Token token = buildSubValue(str, 0);
                lines.add(token);
            }
            if (lineEnd == end) {
                break;
            } else {
                i = lineEnd + 1;
            }
        }
    }

    private Token buildSubValue(String input, int globalIdx) throws ParseError {
        int i = 0;
        while (i < input.length() && input.charAt(i) == ' ')
            i++;
        if (i < input.length() && input.charAt(i) == '(') {
            int end = findEndingBracket(input, i);
            return buildSubValue(input.substring(i + 1, end), globalIdx + i + 1);
        }
        int j = i;
        while (j < input.length() && Character.isAlphabetic(input.charAt(j)))
            j++;
        if (i == j) {
            throw new ParseError(globalIdx + j, "Unexpected end of input");
        }

        String name = input.substring(i, j);
        if (j >= input.length()) { // Lonely symbol
            if (name.equalsIgnoreCase("true")) { // Constant true
                return ConstantToken.create(globalIdx + i, true);
            } else if (name.equalsIgnoreCase("false")) { // Constant false
                return ConstantToken.create(globalIdx + i, false);
            } else {
                return SymbolToken.create(globalIdx + i, name);
            }
        } else if (input.charAt(j) == '(') { // Function
            var func = funcs.get(name);
            if (func == null) {
                throw new ParseError(globalIdx + i, "No function called \"" + name + "\"");
            }

            int end = findEndingBracket(input, j);
            if (end == -1) {
                throw new ParseError(globalIdx + j, "No closing bracket found");
            }
            List<Token> args = new ArrayList<>();
            j++;
            int min = func.getMinArgs(), max = func.getMaxArgs();
            while (true) {
                int argEnd = findNext(input, j, ',');
                if (argEnd == -1)
                    argEnd = end;
                String argString = input.substring(j, argEnd);
                if (argString.length() != 0) {
                    Token val = buildSubValue(argString, globalIdx + j);
                    args.add(val);

                    if (max != -1 && args.size() > max) {
                        throw new ParseError(globalIdx + i, "Expected ')', found ',' in argument list for function \""
                                + name + "\" (maximum " + max + " argument(s))");
                    }
                }
                if (argEnd == end) {
                    break;
                } else {
                    j = argEnd + 1;
                }
            }
            if (min != -1 && args.size() < min) {
                throw new ParseError(globalIdx + i, "Expected ',', found ')' in argument list for function \"" + name
                        + "\" (minimum " + min + " argument(s))");
            }
            Token[] a = args.toArray(Token[]::new);
            return FunctionToken.create(globalIdx + i, func, a);
        }

        int k = j;
        while (k < input.length() && input.charAt(k) == ' ')
            k++;

        if (input.charAt(k) == '=') {
            k++;
            while (k < input.length() && input.charAt(k) == ' ')
                k++;
            String expr = input.substring(k);
            Token exprValue = buildSubValue(expr, globalIdx + k);
            return AssignmentToken.create(globalIdx + k, name, exprValue);
        } else {
            String s = String.valueOf(input.charAt(k));
            throw new ParseError(globalIdx + k + 1, "Syntax error: " + s);
        }
    }

    /** Return index of ending bracket, or -1 if not found */
    private static int findEndingBracket(String string, int start) {
        int count = 0;
        for (int i = start; i < string.length(); i++) {
            if (string.charAt(i) == '(') {
                count++;
            } else if (string.charAt(i) == ')') {
                count--;

                if (count == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    /** Return index of next item, or -1 if not found */
    private static int findNext(String string, int start, Character target) {
        int bcount = 0;
        for (int i = start; i < string.length(); i++) {
            Character c = string.charAt(i);
            if (c == '(') {
                bcount++;
            } else if (c == ')') {
                bcount--;
            } else if (c.equals(target)) {
                if (bcount == 0)
                    return i;
            }
        }

        return -1;
    }
}

interface Evaluable {
    boolean eval(AST tree) throws AST.ExecError;
}

record Token(int idx, Object data) implements Evaluable {

    @Override
    public boolean eval(AST tree) throws AST.ExecError {
        if (data instanceof Evaluable) {
            try {
                return ((Evaluable) data).eval(tree);
            } catch (IllegalArgumentException e) {
                throw new AST.ExecError(idx, e.getMessage());
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }
}

class ConstantToken implements Evaluable {
    private boolean data;

    @Override
    public boolean eval(AST tree) {
        return data;
    }

    @Override
    public String toString() {
        return data ? "true" : "false";
    }

    public static Token create(int idx, boolean data) {
        ConstantToken k = new ConstantToken();
        k.data = data;
        return new Token(idx, k);
    }
}

class SymbolToken implements Evaluable {
    private String name;
    private int idx;

    @Override
    public boolean eval(AST tree) throws AST.ExecError {
        if (tree.symbols.containsKey(name)) {
            return tree.symbols.get(name);
        } else {
            throw new AST.ExecError(idx, "Cannot resolve symbol '" + name + "'");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public static Token create(int idx, String name) {
        SymbolToken k = new SymbolToken();
        k.name = name;
        k.idx = idx;
        return new Token(idx, k);
    }
}

class FunctionToken implements Evaluable {
    public BooleanFunctions.BooleanFunction func;
    public Token[] args;

    @Override
    public boolean eval(AST tree) throws AST.ExecError {
        boolean[] xs = new boolean[args.length];
        for (int i = 0; i < args.length; i++) {
            xs[i] = args[i].eval(tree);
        }
        return func.apply(xs);
    }

    @Override
    public String toString() {
        return func + "(" + Arrays.stream(args).map(Token::toString).collect(Collectors.joining(", ")) + ")";
    }

    public static Token create(int idx, BooleanFunctions.BooleanFunction func, Token[] args) {
        FunctionToken f = new FunctionToken();
        f.func = func;
        f.args = args;
        return new Token(idx, f);
    }
}

class AssignmentToken implements Evaluable {
    private String name;
    private Token token;

    @Override
    public boolean eval(AST tree) throws AST.ExecError {
        boolean value = token.eval(tree);
        tree.symbols.put(name, value);
        return value;
    }

    @Override
    public String toString() {
        return name + " = " + token;
    }

    public static Token create(int idx, String name, Token token) {
        AssignmentToken k = new AssignmentToken();
        k.name = name;
        k.token = token;
        return new Token(idx, k);
    }
}
