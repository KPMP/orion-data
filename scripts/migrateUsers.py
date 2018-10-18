#!/usr/bin/python

import json
import os

rootdir = "/data/prod"
output = open ("/data/prod/combinedUserData.json", "w+")

userNames = []
for subdir, dirs, files in os.walk(rootdir):
    for potentialFile in files:
        if potentialFile == 'metadata.json':
            with open(os.path.join(subdir, potentialFile), "r") as f:
                data = json.load(f)
                user = {};
                submitterName = data["submitterFirstName"] + data["submitterLastName"]
                if submitterName in userNames:
                    continue
                else:
                    user["firstName"] = data["submitterFirstName"]
                    user["lastName"] = data["submitterLastName"]
                    user["displayName"] = ""
                    user["email"] = ""
                    userNames.append(submitterName)
                    output.write(json.dumps(user) + "\n")
                    
