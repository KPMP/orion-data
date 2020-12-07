import pymongo
import mysql.connector
from dotenv import load_dotenv
import os
import csv
import json

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')
EXPRESSION_MATRIX_METADATA_TYPES = [4,21]
cursor = None

try:
    mydb = mysql.connector.connect(
        host="localhost",
        user=mysql_user,
        password=mysql_pwd,
        database="knowledge_environment"
    )
    mydb.get_warnings = True
    cursor1 = mydb.cursor(buffered=True)
    cursor2 = mydb.cursor(buffered=True)
except:
    print("Can't connect to MySQL")
    print("Make sure you have tunnel open to the KE database, e.g.")
    print("ssh ubuntu@qa-atlas.kpmp.org -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306")
    os.sys.exit()

try:
    mongo_client = pymongo.MongoClient("mongodb://localhost:27017/", serverSelectionTimeoutMS=5000)
    database = mongo_client["dataLake"]
    packages = database["packages"]
except:
    print("Can't connect to Mongo")
    os.sys.exit()

query = ("SELECT * FROM file_pending")
cursor1.execute(query)
update_count = 0
for (package_id, file_name, protocol, metadata_type_id, participant_id, release_ver) in cursor1:
    insert_sql = "INSERT IGNORE INTO file (file_id, file_name, package_id, file_size, protocol, metadata_type_id, release_ver) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    if metadata_type_id in EXPRESSION_MATRIX_METADATA_TYPES:
        new_file_name = package_id + "_expression_matrix.zip"
        file_id = package_id
        file_size = 0
    else:
        result = packages.find_one({ "_id": package_id, "files.fileName": file_name}, {"files.$":1})
        new_file_name = result["files"][0]["_id"] + "_" + file_name
        file_size = result["files"][0]["size"]
        file_id = result["files"][0]["_id"]

    val = (file_id, new_file_name, package_id, file_size, protocol, metadata_type_id, release_ver)
    update_count = update_count + 1
    print(insert_sql % val)
    cursor2.execute(insert_sql, val)
    print(cursor2.fetchwarnings())

    sql2 = "INSERT IGNORE INTO file_participant (file_id, participant_id) VALUES (%s, %s)"
    val2 = (result["files"][0]["_id"], participant_id)
    print(sql2 % val2)
    cursor2.execute(sql2, val2)
    warning = cursor2.fetchwarnings()
    if warning is not None:
        print(warning)
    mydb.commit()
print(str(update_count) + " rows inserted")




