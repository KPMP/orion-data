import pymongo
import mysql.connector
from dotenv import load_dotenv
import os
import sys
import csv
import json

def insertIntoSpatialViewerInfo(spatialViewerInfoInsertCursor, use_spatial_viewer, config_type, file_id, metadata_type_id):
    if use_spatial_viewer and config_type:
        sv_file_info_sql = "INSERT IGNORE INTO sv_file_info (file_id, config_type, metadata_type_id) VALUES (%s, %s, %s)"
        spatialViewerInfoInsertCursor.execute(sv_file_info_sql, (file_id, config_type, metadata_type_id,))
    elif use_spatial_viewer and not config_type:
        print("Exiting due to config_type not being found on file_id: " + file_id)
        sys.exit()
    else:
        pass

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')
EXPRESSION_MATRIX_METADATA_TYPES = [4,21,7]
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
    cursor3 = mydb.cursor(buffered=True)
    cursor4 = mydb.cursor(buffered=True)
    cursor5 = mydb.cursor(buffered=True)
except:
    print("Can't connect to MySQL")
    print("Make sure you have tunnel open to the KE database, e.g.")
    print("ssh ubuntu@qa-atlas.kpmp.org -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306")
    os.sys.exit()

try:
    mongo_client = pymongo.MongoClient("mongodb://localhost:27017/", serverSelectionTimeoutMS=5000)
    database = mongo_client["dataLake"]
    packages = database["packages"]
    files = database["files"]
except:
    print("Can't connect to Mongo")
    os.sys.exit()

query = ("SELECT * FROM file_pending")
cursor1.execute(query)
update_count = 0
for (package_id, file_name, protocol, metadata_type_id, participant_id, release_ver, use_spatial_viewer, config_type) in cursor1:
    participant_array = participant_id.split(",")
    insert_sql = "INSERT IGNORE INTO file (dl_file_id, file_name, package_id, file_size, protocol, metadata_type_id, release_ver, use_spatial_viewer, config_type) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
    if metadata_type_id in EXPRESSION_MATRIX_METADATA_TYPES:
        new_file_name = package_id + "_expression_matrix.zip"
        file_id = package_id
        file_size = 0
    else:
        result = packages.find_one({ "_id": package_id, "files.fileName": file_name}, {"files.$":1})
        resultFile = None
        if result is None:
            print("File " + file_name + " in package " + package_id + "not found in packages collection. Looking in files collection . . . ")
            resultFile = files.find_one({ "packageId": package_id, "fileName": file_name})
        else:
            new_file_name = result["files"][0]["_id"] + "_" + file_name
            file_size = result["files"][0]["size"]
            file_id = result["files"][0]["_id"]
        if resultFile is not None:
            new_file_name = resultFile["_id"] + "_" + file_name
            file_size = resultFile["fileSize"]
            file_id = resultFile["_id"]
        if new_file_name is None:
            print("File " + file_name  + " in package " + package_id + " was not found. Exiting.")
            sys.exit()

    file_exists_sql = "SELECT file_id FROM file WHERE dl_file_id = %s"
    cursor4.execute(file_exists_sql, (file_id,))

    if cursor4.rowcount == 0:
        val = (file_id, new_file_name, package_id, file_size, protocol, metadata_type_id, release_ver, use_spatial_viewer, config_type)
        update_count = update_count + 1
        print(insert_sql % val)
        cursor2.execute(insert_sql, val)
        new_file_id = cursor2.lastrowid
        print(cursor2.fetchwarnings())

        for (p_id) in participant_array:
            p_id = p_id.strip()
            find_p_sql = "SELECT participant_id FROM participant WHERE redcap_id = %s"
            cursor3.execute(find_p_sql, (p_id,))
            (p_internal_id,) = cursor3.fetchone()
            sql2 = "INSERT IGNORE INTO file_participant (file_id, participant_id) VALUES (%s, %s)"
            val2 = (new_file_id, p_internal_id)
            print(sql2 % val2)
            cursor2.execute(sql2, val2)
            warning = cursor2.fetchwarnings()
            if warning is not None:
                print(warning)

        insertIntoSpatialViewerInfo(cursor5, use_spatial_viewer, config_type, new_file_id, metadata_type_id)

        mydb.commit()
    else:
        print("File " + new_file_name + " already exists. Skipping")

print(str(update_count) + " files inserted")




