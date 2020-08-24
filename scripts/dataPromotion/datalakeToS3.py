#!/usr/bin/env python2
import pymongo
from minio import Minio
from minio.error import (ResponseError)
from dotenv import load_dotenv
import os
import csv
import sys
from os import path
from collections import OrderedDict

load_dotenv()

minio_access_key = os.environ.get('minio_access_key')
minio_secret_key = os.environ.get('minio_secret_key')
destination_bucket = os.environ.get('destination_bucket')
source_bucket = os.environ.get('source_bucket')
datalake_dir = os.environ.get('datalake_dir')
minio_host = os.environ.get('minio_host')
mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
database = mongo_client["dataLake"]
packages = database["packages"]

file_sizes = OrderedDict()

minio_client = Minio(minio_host, access_key=minio_access_key, secret_key=minio_secret_key, secure=False)

if len(sys.argv) == 3 and sys.argv[1] == '-f':
    using_file_answer = 'Y'
    if sys.argv[2]:
        input_file_name = sys.argv[2]
else:
    print ("Usage: datalakeToS3.py -f [csv_file.csv]")
    sys.exit()

with open(input_file_name) as csv_file:
    csv_reader = csv.DictReader(csv_file)
    no_rows = True
    for row in csv_reader:
        no_rows = False
        datalake_package_dir = datalake_dir + "/package_" + row['package_id'] + "/"
        file_path = datalake_package_dir + row['filename']
        filename = None
        if row['filename'].endswith('expression_matrix.zip'):
            filename = row['package_id'] + "_" + "expression_matrix.zip"
            file_sizes[row['package_id']] = os.path.getsize(file_path)
        else:
            result = packages.find_one({ "_id": row['package_id'], "files.fileName": row['filename']}, {"_id": 0, "files.$": 1})
            if not result is None:
                filename = result['files'][0]['_id'] + "_" + row['filename']
            else:
                print("No files found for " + row['package_id'] + "," + row['filename'])
        if filename:
            object_name = row['package_id'] + "/" + filename
            if not path.exists(file_path):
                source_object = source_bucket + "/package_" + row['package_id'] + "/" + row['filename']
                print("File not found locally. Trying S3: " + source_object)
                try:
                    command_string = "aws s3 cp s3://" + source_object + " s3://" + destination_bucket + "/" + object_name
                    os.system(command_string)
                    #minio_client.copy_object(destination_bucket, object_name, source_object)
                except ResponseError as err:
                    print(err)
                    pass
            else:
                print("Moving " + object_name)
                try:
                    minio_client.fput_object(destination_bucket, object_name, file_path)
                except ResponseError as err:
                    print(err)
                    pass
        else:
            print("Skipping " + row['package_id'] + "," + row['filename'])

if no_rows:
    print('Please add some entries to "files_to_s3.txt"')

for key, value in file_sizes.items():
    print(key + "," + str(value))

