#!/usr/bin/python

from collections import OrderedDict
import json
import os

rootdir = "/data/prod"
output = open ("/data/prod/combinedMetadata.json", "w+")
outputPretty = open ("/data/prod/combinedMetadata.pretty.json", "w+")
userInput = open("/data/prod/exportedUsers.json")
userDataStr = "[" + ",".join([line.strip() for line in userInput.readlines()]) + "]"
userData = dict([(user["lastName"], user["_id"]) for user in json.loads(userDataStr)])
for subdir, dirs, files in os.walk(rootdir):
    for potentialFile in files:
        if potentialFile == 'metadata.json':
            with open(os.path.join(subdir, potentialFile), "r") as f:
                data = json.load(f)
                metadata = {}
                metadata["_id"] = data["id"]
#                 2018-04-30 03:10:13 UTC
                createdAt = data["createdAt"]
                createdAt = createdAt.replace(" UTC", ".000Z")
                createdAt = createdAt.replace(" ", "T")
                metadata["createdAt"] = {"$date" : createdAt} 
                metadata["packageType"] = data["packageType"] 
                metadata["institution"] = data["institution"]
                metadata["protocol"] = data["protocol"]
                metadata["subjectId"] = data["subjectId"]
                experimentDate = data["experimentDate"]
                
                if experimentDate is not None:
                    experimentDate = experimentDate + ".000Z"
                    experimentDate = experimentDate.replace(" ", "T")
                    print experimentDate
                    metadata["experimentDate"] = {"$date" : experimentDate} 
                else:
                    metadata["experimentDate"] = None                
                attachments = []
                descriptions = []
                for item in data["files"]:
                    attachment = {}
                    attachment["_id"]= item["universalId"]
                    attachment["fileName"] = item["fileName"]
                    attachment["size"] = item["size"]
                    descriptions.append(item["description"])
                    attachments.append(attachment)   

                if len(set(descriptions)) == 1:
                    metadata["description"] = descriptions[0]
                else:
                    metadata["description"] = "|".join(descriptions)
                    
                metadata["files"] = attachments
                submitter = OrderedDict()
                submitter["$ref"] = "users"
                submitter["$id"] =  userData[data["submitterLastName"]] 
                metadata["submitter"] = submitter
                
                output.write(json.dumps(metadata) + "\n")
                outputPretty.write(json.dumps(metadata, indent=2) + "\n")