#!/usr/bin/env python2
from configparser import Error
from dotenv import load_dotenv
import os
from collections import OrderedDict
import mysql.connector
from argparse import ArgumentParser

load_dotenv()

destination_bucket = os.environ.get('destination_bucket')
source_bucket = os.environ.get('source_bucket')
datalake_dir = os.environ.get('datalake_dir')

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

parser = ArgumentParser(description="Move files to S3")
parser.add_argument("-v", "--release_ver",
                    dest="release_ver",
                    help="target release version",
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

query = ("SELECT file_id, package_id, file_name, metadata_type_id FROM file WHERE release_ver = " + args.release_ver + "AND file_name NOT IN (SELECT file_name FROM moved_files)")
cursor.execute(query)
update_count = 0

for (file_id, package_id, file_name, metadata_type_id) in cursor:
    datalake_package_dir = datalake_dir + "/package_" + package_id + "/"
    original_file_name = file_name[37:]
    file_path = datalake_package_dir + original_file_name
    expression_file_names = "barcodes.tsv.gz features.tsv.gz matrix.mtx.gz"
    if file_name:
        object_name = package_id + "/" + file_name
        print("Looking for: " + file_path)
        if file_name.endswith('expression_matrix.zip'):
            if metadata_type_id == 21:
                query2 = "SELECT file_name FROM file_pending WHERE package_id = %s AND metadata_type_id = %s"
                cursor2.execute(query2, (package_id, metadata_type_id))
                expression_file_names = cursor2.fetchone()[0].replace(";", "")
            print("Creating expression matrix zip file for: " + expression_file_names)
            expression_file_names_arr = expression_file_names.split()
            if not os.path.exists(datalake_package_dir + expression_file_names_arr[0]):
                for expression_file_name in expression_file_names_arr:
                    source_object = source_bucket + "/package_" + package_id + "/" + expression_file_name
                    command_string = "aws s3 cp s3://" + source_object + " " + datalake_package_dir + expression_file_name
                    print(command_string)
                    os.system(command_string)
            command_string = "cd " + datalake_package_dir + " && zip expression_matrix.zip " + expression_file_names
            print(command_string)
            os.system(command_string)
            file_size = os.path.getsize(file_path)
            values = (file_size, file_id)
            update_sql = "UPDATE file SET file_size = %s WHERE file_id = %s"
            print(update_sql % values)
            cursor2.execute(update_sql, values)
        if not os.path.exists(file_path):
            source_object = source_bucket + "/package_" + package_id + "/" + original_file_name
            print("File not found locally. Trying S3: " + source_object)
            try:
                command_string = 'aws s3 cp "s3://' + source_object + '" "s3://' + destination_bucket + '/' + object_name + '"'
                os.system(command_string)
                update_count = update_count + 1
            except:
                print(err)
                pass
        else:
            try:
                print("Moving " + object_name)
                command_string = 'aws s3 cp "' + file_path + '" s3://' + destination_bucket + '/' + object_name + '"'
                response = os.system(command_string)
                if(response != 0):
                    raise Exception("Failed to upload file " + file_path + " onto s3 bucket " + destination_bucket + "/" + object_name)
                update_count = update_count + 1
                insert_sql = "INSERT INTO moved_files (file_name) VALUES (%s)"
                cursor2.execute(insert_sql, (file_name,))
                mydb.commit()
            except Exception as err:
                print(err)
                pass

    else:
        print("No file name in record.")
    print("\n")
mydb.commit()
mydb.close()
print(str(update_count) + " files moved")