#!/usr/bin/env python2
import pymongo
from minio import Minio
from minio.error import (ResponseError, BucketAlreadyOwnedByYou, BucketAlreadyExists)
from datetime import datetime
from dotenv import load_dotenv
import os
import csv

load_dotenv()

minio_access_key = os.environ.get('minio_access_key')
minio_secret_key = os.environ.get('minio_secret_key')
destination_bucket = os.environ.get('destination_bucket')
datalake_dir = os.environ.get('datalake_dir')

mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
database = mongo_client["dataLake"]
packages = database["packages"]

minio_client = Minio('localhost:9000', access_key='AKIATCLUH4TBFJYTJ3O6', secret_key='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', secure=False)

with open('./files_to_s3.txt') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    no_rows = True
    for row in csv_reader:
        no_rows = False
        result = packages.find_one({ "_id": row['package_id'], "files.fileName": row['filename']}, {"_id": 0, "files.$": 1})
        if not result is None:
            datalake_package_dir = datalake_dir + "/package_" + row['package_id'] + "/"
            file_path = datalake_package_dir + row['filename']
            file_id = result['files'][0]['_id']
            filename = file_id + "_" + row['filename']
            object_name = row['package_id'] + "/" + filename
            print("Moving " + object_name)
            try:
                minio_client.fput_object(destination_bucket, object_name, file_path)
            except ResponseError as err:
                print(err)
        else:
            print("No files found for " + row['package_id'] + "," + row['filename'])
if no_rows:
    print('Please add some entries to "files_to_s3.txt"')

