import os
from numpy import NaN
import mysql.connector
import pandas as pd
from dotenv import load_dotenv

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

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

except:
    print("Can't connect to MySQL")
    print("Make sure you have tunnel open to the KE database, e.g.")
    print("ssh ubuntu@qa-atlas.kpmp.org -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306")
    os.sys.exit()

def get_level(sampleId):
    sql = 'SELECT sample_metadata_value FROM sample_metadata_value WHERE sample_id=%s'
    cursor3.execute(sql, (sampleId,))

    count = 0
    for row in cursor3:
        count +=1
        sampleId = row[0]
    
    if count is not 1:
        print('getLevel count off- ', count, sampleId)

def update_level(level, sampleId):
    sql = 'UPDATE sample_metadata_value SET sample_metadata_value=%s WHERE sample_id=%s'
    cursor2.execute(sql, (level, sampleId,))


def get_internal_sample_ID(spectrack_sample_id):
    sql = "SELECT sample_id FROM sample WHERE spectrack_sample_id=%s"
    cursor1.execute(sql, (spectrack_sample_id,))

    count = 0
    for row in cursor1:
        count +=1
        sampleId = row[0]
    
    if count is not 1:
        print('count off internal sample id- ', count,spectrack_sample_id)

    if count == 1:
        return sampleId

def update():
    data = pd.read_csv('update-levels-data.csv', usecols=['Package ID', 'Level', 'Sample ID', 'config_type', 'Process Status'])
    for index, row in data.iterrows():
        packageID = row['Package ID']
        level = row['Level']
        spectrackSampleId = row['Sample ID']
        configType = row['config_type']
        processStatus = row['Process Status']

        if processStatus == 'Done' and spectrackSampleId and packageID and level is not NaN and configType == 'Light Microscopic Whole Slide Images':
            sampleId = get_internal_sample_ID(spectrackSampleId)
            update_level(level, sampleId)
            get_level(sampleId)
    mydb.commit()

def run_check():
    data = pd.read_csv('update-levels-data.csv', usecols=['Package ID', 'Level', 'Sample ID', 'config_type', 'Process Status'])
    for index, row in data.iterrows():
        packageID = row['Package ID']
        level = row['Level']
        spectrackSampleId = row['Sample ID']
        configType = row['config_type']
        processStatus = row['Process Status']

        if processStatus == 'Done' and spectrackSampleId and packageID and level is not NaN and configType == 'Light Microscopic Whole Slide Images':
            sampleId = get_internal_sample_ID(spectrackSampleId)
            update_level(level, sampleId)
            get_level(sampleId)
    mydb.commit()

update()
run_check()