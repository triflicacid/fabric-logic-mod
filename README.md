# Logic Mod

This mod adds basic logic utilities to Minecraft

# Features

## Logic Gates

Logic gates accept inputs and outputs are signal only if a predicate is met.
- Inputs : if accept only one input, this is opposite the output. If accepts two inputs, these are left/right of the input.
- Output : the output is indicated visually by the icon on the block
  - If the output is on, particles will be emitted from the block

The advanced wrench toggles each get between its NOT variant

This mod contain the following logic gates:

- AND gate
   - Outputs if both inputs are on
   
    | A | B | `A & B` |
    |---|---|---------|
    | 0 | 0 | 0       |
    | 0 | 1 | 0       |
    | 1 | 0 | 0       |
    | 1 | 1 | 1       |

- NAND gate
  - Outputs if either input is off (not-and)

  | A | B | `¬(A & B)` |
  |---|---|----------|
  | 0 | 0 | 1        |
  | 0 | 1 | 1        |
  | 1 | 0 | 1        |
  | 1 | 1 | 0        |

- BUFFER gate
  - Propagates the input

- NOT gate
  - Inverts the input

  | A | `¬ A` |
  |---|-------|
  | 0 | 1     |
  | 1 | 0     |

- OR gate
  - Outputs if either inputs are on

  | A | B | `A \| B` |
  |---|---|----------|
  | 0 | 0 | 0        |
  | 0 | 1 | 1        |
  | 1 | 0 | 1        |
  | 1 | 1 | 1        |

- NOR gate
  - Outputs if both inputs are off (not-or)

  | A | B | `¬(A \| B)` |
  |---|---|-------------|
  | 0 | 0 | 1           |
  | 0 | 1 | 0           |
  | 1 | 0 | 0           |
  | 1 | 1 | 0           |

- XOR gate
  - Outputs if either inputs are on, but not both

  | A | B | `(A \| B) & ¬(A & B)` |
  |---|---|-----------------------|
  | 0 | 0 | 0                     |
  | 0 | 1 | 1                     |
  | 1 | 0 | 1                     |
  | 1 | 1 | 0                     |

- XNOR gate
  - Output if both inputs are equal (not-xor)

  | A | B | `¬(A & B) \| (A & B)` |
  |---|---|-------------|
  | 0 | 0 | 1           |
  | 0 | 1 | 0           |
  | 1 | 0 | 0           |
  | 1 | 1 | 1           |

## Other

- Wrench
  - Rotates horizontally-rotatable blocks around the Y axis
  - Crafting recipe:

  | # |  | # |
  | - | - | - |
  |  | # |  |
  |  | # |  |

  where `#` is an iron bar

- Advanced Wrench
  - Configures certain blocks
  - Crafted equivalently to the wrench, but using gold ingots
  
## Signal Emitters

- Clock
  - Pulses on and off a predetermined amount of ticks (default 20)
    - Stored as NBT data in `OnTickCount` and `OffTickCount` repectively
    - Currenty no way to change these, other than the `/data` command
  - The advanced wrench locks/unlocks the clock (pauses/resumes pulsing)

- Constants
  
  The advanced wrench toggles between LO and HI

  - Constant LO
    - Outputs a directional constant signal strength of 0

  - Constant HI
    - Outputs a directional constant signal strength of 15