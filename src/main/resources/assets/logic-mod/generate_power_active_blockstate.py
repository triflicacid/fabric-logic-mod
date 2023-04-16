'''
    Generate blockstate file for blocks with `facing`, `power` and `active` (may be renamed)
'''

import json

power_inactive = "0"
power_active = "|".join(map(str, range(1, 16)))

if __name__ == "__main__":
    name = input("Enter name: ")
    filename = f"blockstates/{name}.json"

    if input(f"This will create <{filename}>. Continue? [y/n] ") != "y":
        exit(1)

    active_name = input("Name of 'active' property: ")

    multipart = []
    for (dir, rot) in [("north", 0), ("east", 90), ("south", 180), ("west", 270)]:
        # Inactive texture
        multipart.append({
            "apply": {
                "model": f"logic-mod:block/{name}",
                "y": rot
            },
            "when": {
                "OR": [{
                    "facing": dir,
                    "power": power_inactive
                }, {
                    "facing": dir,
                    active_name: False
                }]
            }
        })

        # Active texture
        multipart.append({
            "apply": {
                "model": f"logic-mod:block/{name}_active",
                "y": rot
            },
            "when": {
                "facing": dir,
                "power": power_active,
                active_name: True
            }
        })
    data = { "multipart": multipart }

    with open(filename, "w") as f:
        json.dump(data, f, indent=2)
        print(f"Wrote data to <{filename}>")
