#!/usr/bin/python

import json
import os

rootdir = "/data"
output = open ("/data/combinedMetadata.json", "w")

for subdir, dirs, files in os.walk(rootdir):
    for potentialFile in files:
        if potentialFile == 'metadata.json':
            with open(os.path.join(subdir, potentialFile)) as f:
                packageIndexHolder = {}
                packageIndex = {}
                packageMetadata = {}
                submitter = {}
                data = json.load(f)
                packageIndex["_id"] = data["id"]
                packageIndex["_index"] = "data_lake"
                packageIndex["_type"] = "doc"
                packageIndexHolder["index"] = packageIndex
                packageMetadata["subjectId"] = data["subjectId"]
                packageMetadata["experimentId"] = data["experimentId"]
                packageMetadata["experimentDate"] = data["experimentDate"]
                packageMetadata["createdAt"] = data["createdAt"]
                packageMetadata["packageType"] = data["packageType"]
                submitter["firstName"] = data["submitterFirstName"];
                submitter["lastName"] = data["submitterLastName"]
                packageMetadata["submitter"] = submitter
                packageMetadata["institution"] = data["institution"]
                packageMetadata["join_field"] = "package"
                
                fileMetadatas = []
                files = []
                for fileUpload in data['files']:
                    fileMetadata = {}
                    fileMetadataHolder = {}
                    fileMetadata["_index"] = "data_lake"
                    fileMetadata["_type"] = "doc"
                    fileMetadata["_id"] = fileUpload["universalId"]
                    fileMetadata["routing"] = data["id"]
                    fileMetadataHolder["index"] = fileMetadata
                    file = {}
                    file["path"] = fileUpload["path"]
                    file["size"] = fileUpload["size"]
                    file["fileName"] = fileUpload["fileName"]
                    file["description"] = fileUpload["description"]
                    joinField = {}
                    joinField["name"] = "file";
                    joinField["parent"] = data["id"]
                    file["join_field"] = joinField
                    fileMetadatas.append(fileMetadataHolder)
                    files.append(file)
                

            output.write(json.dumps(packageIndexHolder) + "\n")
            output.write(json.dumps(packageMetadata) + "\n")
            for index, value in enumerate(fileMetadatas):
                output.write(json.dumps(fileMetadatas[index]) + "\n")
                output.write(json.dumps(files[index]) + "\n")
                
output.write("\n")