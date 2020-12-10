import mysql.connector
import os
import csv
import json

from dotenv import load_dotenv
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

# Adds the clinical data from file
# Replace the filename as appropriate
non_clinical_rows = ["Participant ID", "secondary_id", "Tissue Source", "Protocol", "Sample Type", "Tissue Type", "Sex", "Age (Years) (Binned)"]
with open('./20201203_OpenAccessMainProtocolClinicalData.csv') as csv_file:
    csv_reader = csv.DictReader(csv_file)
    inserted = [];
    updated = [];
    for row in csv_reader:
        clinical_json = {}
        for key in row.keys():
            if key not in non_clinical_rows:
                clinical_json[key] = row[key]
        # Need to see if this participant is already in the db
        # if it is, replace with new values, otherwise add new row
        
        query = "SELECT redcap_id FROM participant WHERE redcap_id = '" + row["Participant ID"] + "'";
        mycursor.execute(query);

        if not mycursor.rowcount:
            inserted.append(row["Participant ID"]);
            query = ("INSERT INTO participant (old_participant_id, redcap_id, age_binned, sex, tissue_source, protocol, sample_type, tissue_type, clinical_data)"
                    "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)")
            val = (row["secondary_id"], row["Participant ID"], row["Age (Years) (Binned)"], row["Sex"], row["Tissue Source"], row["Protocol"], row["Sample Type"], row["Tissue Type"], json.dumps(clinical_json))
            print(query % val)
            mycursor.execute(query, val)
            mydb.commit()
        else:
            updated.append(row["Participant ID"]);
            query = "UPDATE participant SET old_participant_id = %s, redcap_id = %s, age_binned = %s, sex = %s, tissue_source = %s, protocol = %s, sample_type=%s, tissue_type=%s, clinical_data = %s WHERE old_participant_id = %s";
            values = (row["secondary_id"], row["Participant ID"], row["Age (Years) (Binned)"], row["Sex"], row["Tissue Source"], row["Protocol"], row["Sample Type"], row["Tissue Type"], json.dumps(clinical_json), row["Participant ID"]);
            print(query % values)
            mycursor.execute(query, values)
            mydb.commit()

print ("Inserted " + str(len(inserted)) + " records");
print(" , ".join(inserted))
print ("Updated " + str(len(updated)) + " records");
print(" , ".join(updated))
mycursor.close()
mydb.close()