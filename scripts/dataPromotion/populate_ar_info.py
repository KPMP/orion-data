import mysql.connector
from dotenv import load_dotenv
import os

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')
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

query = ("SELECT file_id, release_ver, release_sunset FROM file")
cursor1.execute(query)

for (file_id, release_ver, release_sunset) in cursor1:
    print(file_id, release_ver, release_sunset)
    insert_sql = "INSERT INTO ar_file_info (file_id, release_version, release_sunset_version) VALUES (%s, %s, %s)"
    cursor2.execute(insert_sql, (file_id, release_ver, release_sunset,))

mydb.commit()
mydb.close()
