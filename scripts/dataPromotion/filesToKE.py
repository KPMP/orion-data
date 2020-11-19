import pymongo
import mysql.connector
from dotenv import load_dotenv
import os
import csv
import json
from argparse import ArgumentParser

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

parser = ArgumentParser(description="Add files from a .csv to the KE file table. " +
                                    "The .csv should have the following fields and header: file_name, package_id, access, protocol, metadata_type_id")


parser.add_argument("-f", "--file", dest="input_file",
                    help="input file", required=True)
args = parser.parse_args()

try:
    mydb = mysql.connector.connect(
        host="localhost",
        user=mysql_user,
        password=mysql_pwd,
        database="knowledge_environment"
    )
    mycursor = mydb.cursor(buffered=True)
    mycursor2 = mydb.cursor(buffered=True)
except:
    print("Can't connect to MySQL")
    print("Make sure you have tunnel open to the KE database, e.g.")
    print("ssh ubuntu@qa-atlas.kpmp.org -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306")

try:
    mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
    database = mongo_client["dataLake"]
    packages = database["packages"]
except:
    print("Can't connect to Mongo")

try:
    with open(args.input_file) as csv_file:
        csv_reader = csv.DictReader(csv_file)
        sql = "INSERT INTO file (file_id, file_name, package_id, access, file_size, protocol, metadata_type_id) VALUES (%s, %s, %s, %s, %s, %s, %s)"
        for row in csv_reader:
            result = packages.find_one({ "_id": row["package_id"], "files.fileName": row["file_name"]})
            val = ()
            print(sql % val)
            mycursor.execute(sql, val)
            mydb.commit()
except:
    print("Cannot open csv file: " + args.input_file)




