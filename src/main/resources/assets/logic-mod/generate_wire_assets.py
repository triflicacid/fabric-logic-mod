import itertools
import json
import os
import sys

def fwrite(filename: str, data: any):
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    with open(filename, "w") as f:
        json.dump(data, f, indent=2)

states = ("none", "input", "output")
directions = ("up", "down", "north", "south", "west", "east")

def main(color: str):
    # Wire Blockstate
    variants = {}
    for b in (True, False):
        variants[f"active=" + str(b).lower()] = {
            "model": f"logic-mod:block/wire/{color}/wire" + ("_active" if b else "")
        }
    data = { "variants": variants }
    fwrite(f"blockstates/{color}_wire.json", data)

    # Wire Model
    data = {
        "parent": "block/cube_all",
        "textures": {
            "all": f"logic-mod:block/wire/{color}/side"
        }
    }
    fwrite(f"models/block/wire/{color}/wire.json", data)

    data = {
        "parent": "block/cube_all",
        "textures": {
            "all": f"logic-mod:block/wire/{color}/side_active"
        }
    }
    fwrite(f"models/block/wire/{color}/wire_active.json", data)

    # Wire Adapter Blockstates
    variants = {}

    for product in itertools.product(states, repeat=len(directions)):
        for b in (False, True):
            bs = str(b).lower()
            variant = f"active={bs}," + ','.join([directions[i] + "=" + product[i] for i in range(len(product))])
            name = f"{bs}_" + '_'.join(product)
            variants[variant] = { "model": f"logic-mod:block/wire/{color}/adapter/{name}" }

            # Model
            path = f"logic-mod:block/wire/{color}/adapter_"
            post = "_active" if b else ""
            data = {
                "parent": "block/orientable",
                "textures": {
                    "up": path + product[0] + post,
                    "down": path + product[1] + post,
                    "north": path + product[2] + post,
                    "south": path + product[3] + post,
                    "west": path + product[4] + post,
                    "east": path + product[5] + post,
                    "particle": f"logic-mod:block/wire/{color}/side{post}"
                }
            }
            fwrite(f"models/block/wire/{color}/adapter/{name}.json", data)

    data = { "variants": variants }
    fwrite(f"blockstates/{color}_wire_adapter.json", data)

    # Wire item model
    data = {
      "parent": "item/generated",
      "textures": {
        "layer0": f"logic-mod:item/{color}_wire"
      }
    }
    fwrite(f"models/item/{color}_wire.json", data)

    # Wire adapter item model
    name = '_'.join([states[0]] * len(directions))
    data = {
      "parent": "item/generated",
      "textures": {
        "layer0": f"logic-mod:item/{color}_wire_adapter"
      }
    }
    fwrite(f"models/item/{color}_wire_adapter.json", data)

if __name__ == "__main__":
    colors = sys.argv[1:]
    string = ', '.join(colors)

    if input(f"Create the texture asset files for {string} [y/n] ? ") != "y":
        exit(1)

    for color in colors:
        main(color)

    print("Done.")