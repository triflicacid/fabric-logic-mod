import json

gates = ["and", "buffer", "nand", "nor", "not", "or", "xnor", "xor"]

dir = input("Enter direction of side face: ")

for gate in gates:
    for active in (False,True):
        post = "_active" if active else ""
        data = {
            "parent": "logic-mod:block/generic_" + dir,
            "textures": {
                "top": "logic-mod:block/logic_gate/" + gate + post,
                "face": "logic-mod:block/white_indicator" + post
            }
        }

        with open(f"./{gate}{post}.json", "w") as f:
            json.dump(data, f, indent=2)