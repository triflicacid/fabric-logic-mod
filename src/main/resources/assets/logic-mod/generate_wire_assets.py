import itertools
import json
import os

def fwrite(filename, data):
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    with open(filename, "w") as f:
        json.dump(data, f, indent=2)

if input("This will create a host of files, potentially overwriting existing files. Continue [y/n] ?") != "y":
    exit(1)

color = input("Enter wire color: ")

states = ("none", "input", "output")
directions = ("up", "down", "north", "south", "west", "east")

# Wire Blockstate

data = { "variants": { "": { "model": f"logic-mod:block/wire/{color}/wire" } } }
fwrite(f"blockstates/{color}_wire_block.json", data)

# Wire Model
data = {
    "parent": "block/cube_all",
    "textures": {
        "all": f"logic-mod:block/wire/{color}/side"
    }
}
fwrite(f"models/block/wire/{color}/wire.json", data)

# Wire Adapter Blockstates
variants = {}

for product in itertools.product(states, repeat=len(directions)):
    variant = ','.join([directions[i] + "=" + product[i] for i in range(len(product))])
    name = '_'.join(product)
    variants[variant] = { "model": f"logic-mod:block/wire/{color}/variant/{name}" }

    # Model
    path = F"logic-mod:block/wire/{color}/adapter_"
    data = {
        "parent": "block/orientable",
        "textures": {
            "up": path + product[0],
            "down": path + product[1],
            "north": path + product[2],
            "south": path + product[3],
            "west": path + product[4],
            "east": path + product[5],
            "particle": F"logic-mod:block/wire/{color}/side"
        }
    }
    fwrite(f"models/block/wire/{color}/variant/{name}.json", data)

data = { "variants": variants }
fwrite(f"blockstates/{color}_wire_adapter_block.json", data)

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