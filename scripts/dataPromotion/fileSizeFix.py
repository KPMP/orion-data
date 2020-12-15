import mysql.connector
from dotenv import load_dotenv
import os
import csv
import json

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

datalake_dir = os.environ.get('datalake_dir')

try:
    mydb = mysql.connector.connect(
        host="localhost",
        user=mysql_user,
        password=mysql_pwd,
        database="knowledge_environment",
        autocommit=True
    )
    mydb.get_warnings = True
    cursor1 = mydb.cursor(buffered=True)
    cursor2 = mydb.cursor(buffered=True)
except:
    print("Can't connect to MySQL")
    print("Make sure you have tunnel open to the KE database, e.g.")
    print("ssh ubuntu@qa-atlas.kpmp.org -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306")
    os.sys.exit()

query = ("SELECT file_id, file_name, package_id FROM file WHERE file_size = 0")
cursor1.execute(query)
update_count = 0

for (file_id, file_name, package_id) in cursor1:
    datalake_package_dir = datalake_dir + "/package_" + package_id + "/"
    original_file_name = file_name[37:]
    file_path = datalake_package_dir + "expression_matrix.zip"
    file_size = os.path.getsize(file_path)
    values = (file_size, file_id)
    update_sql = "UPDATE file SET file_size = %s WHERE file_id = %s"
    print(update_sql % values)
    cursor2.execute(update_sql, values)



