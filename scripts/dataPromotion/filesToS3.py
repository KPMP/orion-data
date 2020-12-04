#!/usr/bin/env python2
from minio import Minio
from minio.error import (ResponseError)
from dotenv import load_dotenv
import os
from collections import OrderedDict
import mysql.connector
from argparse import ArgumentParser

load_dotenv()

minio_access_key = os.environ.get('minio_access_key')
minio_secret_key = os.environ.get('minio_secret_key')
destination_bucket = os.environ.get('destination_bucket')
source_bucket = os.environ.get('source_bucket')
datalake_dir = os.environ.get('datalake_dir')
minio_host = os.environ.get('minio_host')

minio_client = Minio(minio_host, access_key=minio_access_key, secret_key=minio_secret_key, secure=False)

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

parser = ArgumentParser(description="Move files to S3")
parser.add_argument("-v", "--release_ver",
                    dest="release_ver",
                    help="target release version")

args = parser.parse_args()

try:
    mydb = mysql.connector.connect(
        host="localhost",
        user=mysql_user,
        password=mysql_pwd,
        database="knowledge_environment"
    )
    mydb.get_warnings = True
    cursor = mydb.cursor(buffered=True)
    cursor2 = mydb.cursor(buffered=True)
except:
    print("Can't connect to MySQL")
    os.sys.exit()

query = ("SELECT file_id, package_id, file_name FROM file WHERE release_ver = " + args.release_ver)
cursor.execute(query)
update_count = 0

for (file_id, package_id, file_name) in cursor:
    datalake_package_dir = datalake_dir + "/package_" + package_id + "/"
    file_path = datalake_package_dir + file_name
    if file_name:
        object_name = package_id + "/" + file_name
        if file_name.endswith('expression_matrix.zip'):
            print("Creating expression matrix zip file for: " + file_name)
            command_string = "cd " + datalake_package_dir + " && zip " + file_name + " barcodes.tsv.gz features.tsv.gz matrix.mtx.gz"
            print(command_string)
            os.system(command_string)
            file_size = os.path.getsize(file_path)
            values = (file_size, file_id)
            update_sql = "UPDATE file SET file_size = %s WHERE file_id = %s"
            print(update_sql % values)
            #cursor2.execute(update_sql, values)
        if not path.exists(file_path):
            source_object = source_bucket + "/package_" + package_id + "/" + file_name
            print("File not found locally. Trying S3: " + source_object)
            try:
                command_string = "aws s3 cp s3://" + source_object + " s3://" + destination_bucket + "/" + object_name
                print(command_string)
                #os.system(command_string)
            except ResponseError as err:
                print(err)
                pass
        else:
            print("Moving " + object_name)
            try:
                #minio_client.fput_object(destination_bucket, object_name, file_path)
            except ResponseError as err:
                print(err)
                pass
    else:
        print("Skipping " + package_id + "," + file_name)
