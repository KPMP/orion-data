import mysql.connector
import os
import csv
import json

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

mydb = mysql.connector.connect(
    host="localhost",
    user=mysql_user,
    password=mysql_pwd,
    database="knowledge_environment"
)
mycursor = mydb.cursor(buffered=True)

# Adds the clinical data from file
# Replace the filename as appropriate
non_clinical_rows = ["Participant ID", "Age (Years) (Binned)", "Sex", "Tissue Source", "Protocol", "Sample Type", "Tissue Type"]
with open('./d801f97e-d032-11ea-a504-a4c3f0f6c2ae_20200721_OpenAccessMainProtocolClinicalData.csv') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    for row in csv_reader:
        clinical_json = {}
        for key in row.keys():
            if key not in non_clinical_rows:
                clinical_json[key] = row[key]
        query = ("INSERT INTO participant (participant_id, age_binned, sex, tissue_source, protocol, sample_type, tissue_type, clinical_data)"
                 "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)")
        val = (row["Participant ID"], row["Age (Years) (Binned)"], row["Sex"], row["Tissue Source"], row["Protocol"], row["Sample Type"], row["Tissue Type"], json.dumps(clinical_json))
        print(query % val)
        mycursor.execute(query, val)
        mydb.commit()