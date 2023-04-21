import json

dir = input("Enter direction of side face: ")

for i in range(0,16):
    data = {
        "parent": "logic-mod:block/generic_" + dir,
        "textures": {
            "top": "logic-mod:block/output/" + str(i),
            "face": "logic-mod:block/white_indicator" + ("" if i == 0 else "_active")
        }
    }

    with open(f"./{i}.json", "w") as f:
        json.dump(data, f, indent=2)