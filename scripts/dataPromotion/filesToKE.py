import pymongo
import mysql.connector
from dotenv import load_dotenv
import os
import csv
import json

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')
EXPRESSION_MATRIX_METADATA_TYPE = 4
cursor = None

try:
    mydb = mysql.connector.connect(
        host="localhost",
        user=mysql_user,
        password=mysql_pwd,
        database="knowledge_environment"
    )
    cursor = mydb.cursor(buffered=True)
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
cursor.execute(query)
update_count = 0
for (package_id, file_name, protocol, metadata_type_id, participant_id) in cursor:
    insert_sql = "INSERT INTO file (file_id, file_name, package_id, file_size, protocol, metadata_type_id) VALUES (%s, %s, %s, %s, %s, %s)"
    if metadata_type_id == EXPRESSION_MATRIX_METADATA_TYPE:
        val = (package_id, package_id + "_expression_matrix.zip", package_id, 0, protocol, metadata_type_id)
    else:
        result = packages.find_one({ "_id": package_id, "files.fileName": file_name}, {"files.$":1})
        new_file_name = result["files"][0]["_id"] + "_" + file_name
        val = (result["files"][0]["_id"], new_file_name, package_id, result["files"][0]["size"], protocol, metadata_type_id)

    update_count = update_count + 1
    print(insert_sql % val)
    # cursor.execute(insert_sql, val)
    # mydb.commit()
print(str(update_count) + " rows inserted")




