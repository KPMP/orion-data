#!/usr/bin/python

import json
import os
import zipfile

rootdir = "/data"
        
def zipdir(path, zipFileHandle):
    for root, dirs, files in os.walk(path):
        for file in files:
            if os.path.join(root,file) != archiveName:
                zipFileHandle.write(os.path.join(root, file), file, compress_type=zipfile.ZIP_DEFLATED)
        
    
if __name__ == '__main__':
    for root, dirs, files in os.walk(rootdir):
        for file in files:
            if file == 'metadata.json':
                with open(os.path.join(root, file), "r") as metadataFile:
                    data = json.load(metadataFile)
                    packageId = data["id"]
                    archiveName = root + '/' + packageId + '.zip'
                    zipFile = zipfile.ZipFile(archiveName, 'w', allowZip64 = True)
		    print("zipping directory: " + root)
                    zipdir(root, zipFile )
                    zipFile.close()
         
