'''
    Generate blockstate file for a block with `facing` and `power` properties
'''

import json

power_inactive = "0"
power_active = "|".join(map(str, range(1, 16)))

if __name__ == "__main__":
    name = input("Enter name: ")
    filename = f"blockstates/{name}.json"

    if input(f"This will create <{filename}>. Continue? [y/n] ") != "y":
        exit(1)

    multipart = []
    for (dir, rot) in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        for active in (False, True):
            multipart.append({
                "apply": {
                    "model": f"logic-mod:block/{name}" + ("_active" if active else ""),
                    "y": rot
                },
                "when": {
                    "facing": dir,
                    "power": power_active if active else power_inactive
                }
            })
    data = { "multipart": multipart }

    with open(filename, "w") as f:
        json.dump(data, f, indent=2)
        print(f"Wrote data to <{filename}>")
