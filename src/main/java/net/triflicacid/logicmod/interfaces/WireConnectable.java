package net.triflicacid.logicmod.interfaces;

import net.triflicacid.logicmod.util.WireColor;

/** Methods return whether a wire of specified color should attach to this block */
public interface WireConnectable {
    /** Should a wire of the given color connect to this block (visually) */
    default boolean shouldWireConnect(WireColor color) { return false; }
}
