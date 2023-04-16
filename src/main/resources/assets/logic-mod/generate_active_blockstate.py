'''
    Generate blockstate file for a block with `facing` and `active` properties
'''

import json

if __name__ == "__main__":
    name = input("Enter name: ")
    filename = f"blockstates/{name}.json"

    if input(f"This will create <{filename}>. Continue? [y/n] ") != "y":
        exit(1)

    variants = {}
    for (dir, rot) in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        for active in (False, True):
            active_str = str(active).lower()
            variants[f"facing={dir},active={active_str}"] = {
                "model": f"logic-mod:block/{name}" + ("_active" if active else ""),
                "y": rot
            }
    data = { "variants": variants }

    with open(filename, "w") as f:
        json.dump(data, f, indent=2)
        print(f"Wrote data to <{filename}>")
