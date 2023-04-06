# Logic Mod

This mod adds basic logic utilities to Minecraft

**IMPORTANT** some asset files are not included by default. To generate them, follow the following steps:
- Navigate to `src/mainresources/assets/logic-mod/`
- Run `python generate-wire-assets.py` providing the following values each run:
  - `blue`
  - `green`
  - `red`
  - `yellow`

# Features

## Logic Gates

Logic gates accept inputs and outputs are signal only if a predicate is met.
- Inputs : if accept only one input, this is opposite the output. If accepts two inputs, these are left/right of the input.
- Output : the output is indicated visually by the icon on the block, and by a black indicator lamp which glows red when the gate is active.

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
  |---|---|------------|
  | 0 | 0 | 1          |
  | 0 | 1 | 1          |
  | 1 | 0 | 1          |
  | 1 | 1 | 0          |

- BUFFER gate
  - Propagates the input

- NOT gate
  - Inverts the input

  | A | `¬ A` |
  |---|-------|
  | 0 | 1     |
  | 1 | 0     |

- Conditional gate
  - Either returns its left/right input depending on whether it is recieving power from behind

  | In | Out   |
    |----|-------|
  | 0  | Right |
  | 1  | Left  |

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
  |---|---|-----------------------|
  | 0 | 0 | 1                     |
  | 0 | 1 | 0                     |
  | 1 | 0 | 0                     |
  | 1 | 1 | 1                     |


## Other Blocks

- Clock
  - Pulses on and off a predetermined amount of ticks (default 20)
    - Stored as NBT data in `OnTickCount` and `OffTickCount` respectively
    - Currently, there is no way to change these other than the `/data` command
  - May be locked by powering from behind
  - The advanced wrench locks/unlocks the clock (pauses/resumes pulsing)

- Input
  - Emits a constant signal 0-15
  - Signal defaults to 0
  - Right-clicking with an advanced wrench increments the signal (hold shift to decrement).

## Tools

- Wrench
  - Unless otherwise specified, applying the wrench rotates the block clockwise around the Y axis (horizontally)
  - Holding shift will rotate anti-clockwise
  - Crafting recipe:

  | # |   | # |
    |---|---|---|
  |   | # |   |
  |   | # |   |

  where `#` is an iron bar

- Advanced Wrench
  - Configures certain blocks
  - Crafted equivalently to the wrench, but using gold ingots

## Wires and Adapters
Wires are blocks which carry redstone signals. Key features:
- Signals are transmitted immediately.
- Signal does not decay.
- Signal is transferred to any adjacent wire blocks *of the same color*. This means that different colored wires may be adjacent without interfering.
- To get a signal in/out of a wire, use an *adapter* of the same color.

Adapters are wires with extra, optional functionality. The behaviour of each face may be changed by right-clicking on it with an advanced wrench, and will cycle between
- *Normal mode*, indicated by a black square. No unique behaviour.
- *Input mode*, indicated by a blue-filled square. This will accept any redstone signal into the wire.
- *Output mode*, indicated by an orange-filled square. This will output the wire's redstone signal to any block adjacent to this face. The signal will also transfer to an adjacent adapter if the face is in input mode, allowing for communication between different colored wires.

Applying the wrench on an adapter rotates it around the axis the player is looking in.


There are four wire/adapter variants:
- Blue
- Green
- Red
- Yellow
