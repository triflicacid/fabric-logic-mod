# Logic Mod

This mod adds basic logic utilities to Minecraft

**IMPORTANT** some asset files are not included by default. To generate them, follow the following steps:
- Navigate to `src/main/resources/assets/logic-mod/`
- Run `python generate-wire-assets.py blue green red yellow purple orange`

# Features

**NOTE** All blocks that accept input(s), unless otherwise stated, take **strong** redstone inputs and produce **weak** redstone outputs.

## Logic Gates

Logic gates accept inputs and outputs are signal only if a predicate is met.
- Inputs : different gates accept a different number of inputs
  - One input: opposite of indicator.
  - Two inputs: left/right of indicator.
  - Three inputs: both of the above.

  Some two-input gates can be extended to three inputs. This will occur is there is a (potential) redstone-emitting block behind it.

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

  - Two inputs, but extends to work with three

- NAND gate
  - Outputs if either input is off (not-and)

  | A | B | `¬(A & B)` |
  |---|---|------------|
  | 0 | 0 | 1          |
  | 0 | 1 | 1          |
  | 1 | 0 | 1          |
  | 1 | 1 | 0          |

  - Two inputs, but extends to work with three

- BUFFER gate
  - Propagates the input
  - One input

- NOT gate
  - Inverts the input

  | A | `¬ A` |
  |---|-------|
  | 0 | 1     |
  | 1 | 0     |

  - One input

- OR gate
  - Outputs if either inputs are on

  | A | B | `A \| B` |
  |---|---|----------|
  | 0 | 0 | 0        |
  | 0 | 1 | 1        |
  | 1 | 0 | 1        |
  | 1 | 1 | 1        |

  - Two inputs, but extends to work with three

- NOR gate
  - Outputs if both inputs are off (not-or)

  | A | B | `¬(A \| B)` |
  |---|---|-------------|
  | 0 | 0 | 1           |
  | 0 | 1 | 0           |
  | 1 | 0 | 0           |
  | 1 | 1 | 0           |

  - Two inputs, but extends to work with three

- XOR gate
  - Outputs if either inputs are on, but not both

  | A | B | `(A \| B) & ¬(A & B)` |
  |---|---|-----------------------|
  | 0 | 0 | 0                     |
  | 0 | 1 | 1                     |
  | 1 | 0 | 1                     |
  | 1 | 1 | 0                     |

  - Two inputs, but extends to work with three

- XNOR gate
  - Output if both inputs are equal (not-xor)

  | A | B | `¬(A & B) \| (A & B)` |
  |---|---|-----------------------|
  | 0 | 0 | 1                     |
  | 0 | 1 | 0                     |
  | 1 | 0 | 0                     |
  | 1 | 1 | 1                     |

  - Two inputs, but extends to work with three


## Other Blocks

- Clock
  - Pulses on and off a predetermined amount of ticks (default 20)
    - Stored as NBT data in `OnTickCount` and `OffTickCount` respectively
    - Currently, there is no way to change these other than the `/data` command
  - May be locked by powering from behind
  - The advanced wrench locks/unlocks the clock (pauses/resumes pulsing)

- Conditional gate
  - Either returns its left/right input depending on whether it is receiving power from behind

  | In | Out   |
  |----|-------|
  | 0  | Right |
  | 1  | Left  |

- Equality gate
  - If both inputs are equal (or all three if a third is connected) then outputs the received signal, else outputs 0.

- Input
  - Emits a constant signal 0-15
  - Signal defaults to 0
  - Right-clicking with an advanced wrench increments the signal (hold shift to decrement).
  - Powering from behind disables the input

- Memory Cell
  - Stores a redstone signal in memory, which it outputs
- Accepts a control input from the side, and a write line from the left.
    - 0: `nothing`. Don't output anything (signal 0).
    - 1: `read`. Output the stored signal.
    - 2: `write`. Sets internal memory to signal it is receiving from its left.
    - 3: `clear`. Clears the internal memory (sets it to 0).
  - Use the advanced wrench to manually increment the stored redstone signal (shift to decrement).

- Output
  - Given a signal, displays its signal strength

- Random Signal Generator
  - If powered from behind, it will generate and output a new number. This will happen every 2 ticks whiles powered.
  - If in binary mode, generates either a 0 or a 1, else generates a number in the range 0-15.
    - Give a 1-tick pulse to change number once!
  - Toggle in/out of binary mode using an advanced wrench.

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

**NOTE** Adapters accept strong inputs only, and emits strong outputs.

There are six wire/adapter variants:
- Blue
- Green
- Orange
- Purple
- Red
- Yellow

The bus component resembles a white wire, but has an internal cable for each wire color. The bus adapter is different to the normal wire adapters: it has no side modes. It cannot accept nor output a redstone signal. If a colored wire is placed adjacent to the bus adapter, that color's signal is fed into the bus.
Basically, a bus acts as all the colored wires condensed into one, and the adapters allow for individual colored wires to input/output that color's signal.

The bus cable and adapter are white to symbolise all the colors combined.