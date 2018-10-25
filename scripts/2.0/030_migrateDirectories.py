#!/usr/bin/python

import os
import json
import shutil

rootdir = "/data"

for subdir, dirs, files in os.walk(rootdir):
    for potentialFile in files:
        if potentialFile == 'metadata.json':
            with open(os.path.join(subdir, potentialFile), "r") as f:
                data = json.load(f)
                universalId =  data["id"]
                newDirectoryName = os.path.join(rootdir,"package_" + universalId)
                shutil.copytree(subdir, newDirectoryName)
                    
                currentMetadataFile = os.path.join(subdir, 'metadata.json')
                newMetadataFileName = os.path.join(subdir, 'metadata.json.deprecated')
                shutil.move(currentMetadataFile, newMetadataFileName)
                os.remove(os.path.join(newDirectoryName, 'metadata.json'))
