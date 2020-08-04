import pymongo
import mysql.connector
from dotenv import load_dotenv
import os
import csv
import json

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

mydb = mysql.connector.connect(
    host="localhost",
    user=mysql_user,
    password=mysql_pwd,
    database="knowledge_environment"
)
mycursor = mydb.cursor(buffered=True)
mycursor2 = mydb.cursor(buffered=True)

mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
database = mongo_client["dataLake"]
ke_files = database["keFiles"]
packages = database["packages"]

# Move file and participant data from ES index to KE tables (file, file_participant)
#
# for file in ke_files.find():
#     sql = "INSERT INTO file (file_id, file_name, package_id, access, file_size, protocol) VALUES (%s, %s, %s, %s, %s, %s)"
#     val = (file["file_id"], file["file_name"],file["package_id"], file["access"], file["file_size"],file["protocol"])
#     print(sql % val)
#     mycursor.execute(sql, val)
#     mydb.commit()
#     for participant_id in file["cases"]["samples"]["participant_id"]:
#         sql2 = "INSERT INTO file_participant (file_id, participant_id) VALUES (%s, %s)"
#         val2 = (file["file_id"], participant_id)
#         mycursor.execute(sql2, val2)
#         mydb.commit()

# Get package ID from dataLake.packages and update KE file table
#
# query = ("SELECT file_id FROM file WHERE package_id = ''")
# mycursor.execute(query)
# for (file_id,) in mycursor:
#     result = packages.find_one({ "files._id": file_id})
#     print(file_id + " " + result["_id"])
#     sql = "UPDATE file SET package_id = %s WHERE file_id = %s"
#     val = (result["_id"], file_id)
#     mycursor2.execute(sql, val)
#     mydb.commit()

# Update KE file table with metadata type from spreadsheet
#
# with open('./atlas_files.csv') as csv_file:
#     csv_reader = csv.DictReader(csv_file)
#     for row in csv_reader:
#         query = "SELECT file_id FROM file WHERE package_id = %s AND LOCATE(%s, file_name) > 0"
#         if row["metadata_type_id"] in ("7", "4", "5"):
#             file_suffix = "expression_matrix.zip"
#         else:
#             file_suffix = row["file_name"]
#         val = (row["package_id"], file_suffix)
#         mycursor.execute(query, val)
#         for (file_id,) in mycursor:
#             query2 = "UPDATE file SET metadata_type_id = %s WHERE file_id = %s"
#             val2 = (row["metadata_type_id"], file_id)
#             print(query2 % val2)
#             mycursor2.execute(query2, val2)
#             mydb.commit()

# Adds the metadata types from a file
#
# with open('./metadata_types.csv') as csv_file:
#     csv_reader = csv.DictReader(csv_file)
#     for row in csv_reader:
#         query = ("INSERT INTO metadata_type (metadata_type_id, experimental_strategy, data_type, data_category, data_format, platform, workflow_type, access, kpmp_data_type)"
#          " VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)")
#         val = (row["metadata_type_id"], row["experimental_strategy"], row["data_type"], row["data_category"], row["data_format"], row["platform"], row["workflow_type"], row["access"], row["kpmp_data_type"])
#         print(query % val)
#         mycursor.execute(query, val)
#         mydb.commit()

# Adds the clinical data from file
# non_clinical_rows = ["Participant ID", "Age (Years) (Binned)", "Sex", "Tissue Source", "Protocol", "Sample Type", "Tissue Type"]
# with open('./d801f97e-d032-11ea-a504-a4c3f0f6c2ae_20200721_OpenAccessMainProtocolClinicalData.csv') as csv_file:
#     csv_reader = csv.DictReader(csv_file)
#     for row in csv_reader:
#         clinical_json = {}
#         for key in row.keys():
#             if key not in non_clinical_rows:
#                 clinical_json[key] = row[key]
#         query = ("INSERT INTO participant (participant_id, age_binned, sex, tissue_source, protocol, sample_type, tissue_type, clinical_data)"
#                  "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)")
#         val = (row["Participant ID"], row["Age (Years) (Binned)"], row["Sex"], row["Tissue Source"], row["Protocol"], row["Sample Type"], row["Tissue Type"], json.dumps(clinical_json))
#         print(query % val)
#         mycursor.execute(query, val)
#         mydb.commit()

mycursor.close()
mydb.close()

