#!/usr/bin/python

import json
import os

rootdir = "/data"
output = open ("/data/combinedMetadata.json", "w")

for subdir, dirs, files in os.walk(rootdir):
    for potentialFile in files:
        if potentialFile == 'metadata.json':
            with open(os.path.join(subdir, potentialFile), "r") as f:
                packageIndexHolder = {}
                packageMetadata = {}
                submitter = {}
                data = json.load(f)
                packageIndex = {"_id": data["id"], "_index": "data_lake", "_type": "doc"}
                packageIndexHolder["index"] = packageIndex
                packageMetadata["subjectId"] = data["subjectId"]
                packageMetadata["experimentId"] = data["experimentId"]
                packageMetadata["experimentDate"] = data["experimentDate"]
                packageMetadata["createdAt"] = data["createdAt"]
                packageMetadata["packageType"] = data["packageType"]
                submitter["firstName"] = data["submitterFirstName"]
                submitter["lastName"] = data["submitterLastName"]
                packageMetadata["submitter"] = submitter
                packageMetadata["institution"] = data["institution"]
                packageMetadata["join_field"] = "package"
                
                fileMetadatas = []
                files = []
                for fileUpload in data['files']:
                    fileMetadata = { "_index": "data_lake", "_type": "doc", "_id": fileUpload["universalId"], "routing": data["id"]}
                    fileMetadataHolder = {}
                    fileMetadataHolder["index"] = fileMetadata
                    theFile = {}
                    theFile["path"] = fileUpload["path"]
                    theFile["size"] = fileUpload["size"]
                    theFile["fileName"] = fileUpload["fileName"]
                    theFile["description"] = fileUpload["description"]
                    joinField = {}
                    joinField["name"] = "file"
                    joinField["parent"] = data["id"]
                    theFile["join_field"] = joinField
                    fileMetadatas.append(fileMetadataHolder)
                    files.append(theFile)
                

            output.write(json.dumps(packageIndexHolder) + "\n")
            output.write(json.dumps(packageMetadata) + "\n")
            for index, value in enumerate(fileMetadatas):
                output.write(json.dumps(fileMetadatas[index]) + "\n")
                output.write(json.dumps(files[index]) + "\n")
                
output.write("\n")
