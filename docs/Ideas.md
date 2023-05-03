# Ideas

Below are listed various ideas for this mod. They may or may not be implemented.

Every bullet is ranked 0-10 for desirability/likeliness.

## New Content
- [**7**] C-element. Takes two inputs.
  - `normal` mode, symbol `C`
    - Both LO: output LO.
    - Both HI: output HI.
    - Otherwise: retain previous state
  - `up` mode, symbol `C+`
    - Both LO: output LO
    - `B` is HI: output HI.
    - Otherwise: retain previous state
  - `down` mode, symbol `C-`
    - `B` is HI: output LO.
    - Both HI: output HI.
    - Otherwise: retain previous state
- [**3**] Single adder/subtractor/multiplier/divider for single signal strengths
- [**6**] Not sure of a name, but pulls up a signal to 15 or pushes down a signal to 0 if the signal exceeds or is less than a given signal strength e.g. 7

## Modifications
- [**2**] Make WireAdapterBlock non-dependent on AbstractWireBlock -- remove POWER property, replace with a boolean ACTIVE. This would reduce the number of blockstates and decrease loading time.

## Textures
