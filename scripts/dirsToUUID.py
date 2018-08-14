#!/usr/bin/python

import json
import os

rootdir = "/cygdrive/c/data"

if __name__ == '__main__':
    for root, dirs, files in os.walk(rootdir):
        dirName = ''
        packageId = ''
        for file in files:
            if file == 'metadata.json':
                with open(os.path.join(root, file), "r") as metadataFile:
                    data = json.load(metadataFile)
                    packageId = data["id"]
                    dirName = 'package_' + packageId
        newDirPath = rootdir + '/' + dirName;
        if packageId != '':
            print "renaming " + root + " to " + newDirPath
            os.rename(root, newDirPath)
