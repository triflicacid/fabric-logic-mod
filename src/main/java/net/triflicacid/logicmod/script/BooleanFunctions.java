package net.triflicacid.logicmod.script;

public class BooleanFunctions {
    public static abstract class BooleanFunction {
        public abstract int getMinArgs();

        public abstract int getMaxArgs();

        public abstract boolean apply(boolean[] xs);
    }

    public static class Random extends BooleanFunction {
        public static String NAME = "random";

        @Override
        public int getMinArgs() {
            return 0;
        }

        @Override
        public int getMaxArgs() {
            return 0;
        }

        @Override
        public boolean apply(boolean[] xs) {
            if (xs.length == 0)
                return Math.random() >= 0.5;
            throw new IllegalArgumentException("Function " + this + " expected 0 arguments, got " + xs.length);
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class Not extends BooleanFunction {
        public static String NAME = "not";

        @Override
        public int getMinArgs() {
            return 1;
        }

        @Override
        public int getMaxArgs() {
            return 1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            if (xs.length == 1)
                return !xs[0];
            throw new IllegalArgumentException("Function " + this + " expected 1 argument, got " + xs.length);
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class And extends BooleanFunction {
        public static String NAME = "and";

        @Override
        public int getMinArgs() {
            return 2;
        }

        @Override
        public int getMaxArgs() {
            return -1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            for (boolean x : xs)
                if (!x)
                    return false;
            return true;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class Nand extends BooleanFunction {
        public static String NAME = "nand";

        @Override
        public int getMinArgs() {
            return 2;
        }

        @Override
        public int getMaxArgs() {
            return -1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            for (boolean x : xs)
                if (!x)
                    return true;
            return false;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class Or extends BooleanFunction {
        public static String NAME = "or";

        @Override
        public int getMinArgs() {
            return 2;
        }

        @Override
        public int getMaxArgs() {
            return -1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            for (boolean x : xs)
                if (x)
                    return true;
            return false;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class Nor extends BooleanFunction {
        public static String NAME = "nor";

        @Override
        public int getMinArgs() {
            return 2;
        }

        @Override
        public int getMaxArgs() {
            return -1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            for (boolean x : xs)
                if (x)
                    return false;
            return true;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class Xor extends BooleanFunction {
        public static String NAME = "xor";

        @Override
        public int getMinArgs() {
            return 2;
        }

        @Override
        public int getMaxArgs() {
            return -1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            int truthy = 0;
            for (boolean x : xs) {
                if (x) {
                    if (truthy > 0)
                        return false;

                    truthy++;
                }
            }

            return truthy == 1;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    public static class Xnor extends BooleanFunction {
        public static String NAME = "xnor";

        @Override
        public int getMinArgs() {
            return 2;
        }

        @Override
        public int getMaxArgs() {
            return -1;
        }

        @Override
        public boolean apply(boolean[] xs) {
            int truthy = 0;
            for (boolean x : xs) {
                if (x) {
                    truthy++;
                }
            }

            return truthy != 1;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }
}
