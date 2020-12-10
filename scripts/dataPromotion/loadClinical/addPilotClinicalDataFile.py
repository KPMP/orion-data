import mysql.connector
import os
import uuid
import csv

from dotenv import load_dotenv
load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')
clinical_file_name = os.environ.get('pilot_clinical_filename')
relase_ver = os.environ.get('release_ver')

mydb = mysql.connector.connect(
    host="localhost",
    user=mysql_user,
    password=mysql_pwd,
    database="knowledge_environment"
)
mycursor = mydb.cursor(buffered=True)

file_uuid = str(uuid.uuid4())
new_file_name= file_uuid + "_" + clinical_file_name
file_size = os.path.getsize('./' + clinical_file_name)

query = "INSERT INTO file (dl_file_id, file_name, package_id, file_size, protocol, release_ver) " + \
    "VALUES (%s, %s, %s, %s, %s, %s)";
values = (file_uuid, new_file_name, file_uuid, file_size, "KPMP Main Protocol", relase_ver);

mycursor.execute(query, values);
mydb.commit();
mycursor.close();

file_id = mycursor.lastrowid


# This next part is dumb...I couldn't get the code to do this insert correctly, so instead I am
# just printing out the insert statements, and then I execute them manually
with open('./' + clinical_file_name) as csv_file:
    csv_reader = csv.DictReader(csv_file)
    for row in csv_reader:
        mycursor = mydb.cursor(buffered=True, dictionary=True)
        particpant_id = row['Participant ID'];

        query = "INSERT INTO file_participant (file_id, participant_id) " + \
                "VALUES (%s, (SELECT participant_id FROM participant WHERE redcap_id='%s'));";
        values = (file_id, particpant_id);

        print(query % values)
        # mycursor.execute(query, values);

        mycursor.close();


mydb.close();