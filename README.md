# Logic Mod

This mod adds basic logic utilities to Minecraft

# Features

## Logic Gates

Logic gates accept inputs and outputs are signal only if a predicate is met.
- Inputs : if accept only one input, this is opposite the output. If accepts two inputs, these are left/right of the input.
- Output : the output is indicated visually by the icon on the block
  - If the output is on, particles will be emitted from the block

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
  - No use at the moment