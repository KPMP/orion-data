#!/usr/bin/env python2
from dotenv import load_dotenv
import os
from collections import OrderedDict
import mysql.connector
from argparse import ArgumentParser

load_dotenv()

destination_bucket = "kpmp-knowledge-environment-sunsetted"
source_bucket = "kpmp-knowledge-environment"

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

parser = ArgumentParser(description="Sunset files")
parser.add_argument("-v", "--release_sunset",
                    dest="release_sunset",
                    help="target release sunset version",
                    required=True)

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
    print("Make sure you have tunnel open to the KE database, e.g.")
    print("ssh ubuntu@qa-atlas.kpmp.org -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306")
    os.sys.exit()

query = ("SELECT file_id, package_id, file_name FROM file WHERE release_sunset = " + args.release_sunset)
cursor.execute(query)
update_count = 0

for (file_id, package_id, file_name) in cursor:
    if file_name:
        object_name = package_id + "/" + file_name
        source_object = source_bucket + "/" + object_name
        try:
            command_string = "aws s3 mv s3://" + source_object + " s3://" + destination_bucket + "/" + object_name
            print(command_string)
            #os.system(command_string)
            update_count = update_count + 1
        except ResponseError as err:
            print(err)
            pass
    else:
        print("No file name in record.")
    print("\n")

print(str(update_count) + " files moved")