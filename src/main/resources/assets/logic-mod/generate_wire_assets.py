import json
import os
import sys

def fwrite(filename: str, data: any):
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    with open(filename + ".json", "w") as f:
        json.dump(data, f, indent=2)

modes = ("none", "input", "output")

power_inactive = "0"
power_active = "|".join(map(str, range(1, 16)))

def main(color):
    # Adapter blockstate
    multipart = []
    for (dir, axis, rot) in [("north", None, 0), ("east", "y", 90), ("south", "y", 180), ("west", "y", 270), ("up", "x", 270), ("down", "x", 90)]:
        for mode in modes:
            for active in (False, True):
                data = {
                    "apply": {
                        "model": f"logic-mod:block/wire/{color}/adapter_{mode}" + ("_active" if active else "")
                    },
                    "when": {
                        dir: mode,
                        "power": power_active if active else power_inactive
                    }
                }
                if axis is not None:
                    data["apply"][axis] = rot
                multipart.append(data)

    data = { "multipart": multipart }
    fwrite(f"blockstates/{color}_wire_adapter", data)

    # Adapter models
    for mode in modes:
        for active in (False, True):
            data = {
                "parent": "block/template_single_face",
                "textures": {
                    "texture": f"logic-mod:block/wire/{color}/adapter_{mode}" + ("_active" if active else "")
                }
            }
            fwrite(f"models/block/wire/{color}/adapter_{mode}" + ("_active" if active else ""), data)

    # Wire blockstate
    multipart = []
    for active in (False, True):
        post = "_active" if active else ""
        multipart.append({
            "apply": {
                "model": f"logic-mod:block/wire/{color}/wire{post}"
            },
            "when": {
                "power": power_active if active else power_inactive
            }
        })
    data = { "multipart": multipart }
    fwrite(f"blockstates/{color}_wire", data)

    # Wire models
    for active in (False, True):
        post = "_active" if active else ""
        data = {
          "parent": "block/cube_all",
          "textures": {
            "all": f"logic-mod:block/wire/{color}/side{post}"
          }
        }
        fwrite(f"models/block/wire/{color}/wire{post}", data)

if __name__ == "__main__":
    colors = sys.argv[1:]
    string = ', '.join(colors)

    if input(f"Create the texture asset files for {string} [y/n] ? ") != "y":
        exit(1)

    for color in colors:
        main(color)

    print("Done.")
    
        
