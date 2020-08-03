#!/usr/bin/env python2
from collections import OrderedDict
import json
import copy
import csv
import sys
import mysql.connector
import pprint
from dotenv import load_dotenv
import os

load_dotenv()

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

mydb = mysql.connector.connect(
    host="localhost",
    user=mysql_user,
    password=mysql_pwd,
    database="knowledge_environment"
)
mycursor = mydb.cursor(buffered=True,dictionary=True)

class IndexDoc:
    def __init__(self, access, platform, experimental_strategy, data_category, workflow_type, data_format, data_type, file_id, file_name, file_size, protocol, package_id, cases):
        self.access = access
        self.platform = platform
        self.experimental_strategy = experimental_strategy
        self.data_category = data_category
        self.workflow_type = workflow_type
        self.file_id = file_id
        self.file_name = file_name
        self.data_format = data_format
        self.file_size = file_size
        self.data_type = data_type
        self.protocol = protocol
        self.package_id = package_id
        self.cases = cases

class CasesIndexDoc:
    def __init__(self, tissue_source, samples, demographics):
        self.tissue_source = tissue_source
        self.samples = samples
        self.demographics = demographics

def get_index_update_json(id):
    return '{"update":{"_index":"file_cases","_id":"' + id + '"}}'

def get_index_doc_json(index_doc):
    index_doc.cases = index_doc.cases.__dict__
    return '{"doc":' + json.dumps(index_doc.__dict__) + ',"doc_as_upsert":true}'

input_file_id = ""
where_clause = ""

if len(sys.argv) > 1 and sys.argv[1] != "":
    input_file_id = sys.argv[1]
else:
    print("Please include a file_id")
    sys.exit()

if input_file_id:
    where_clause = " WHERE f.file_id = '" + input_file_id + "' "

query = ("SELECT f.*, fp.*, p.*, m.* FROM file f "  
          "JOIN file_participant fp on f.file_id = fp.file_id "
          "JOIN participant p on fp.participant_id = p.participant_id "
          "JOIN metadata_type m on f.metadata_type_id = m.metadata_type_id" + where_clause)

print(query)
mycursor.execute(query)
row_num = 1
for row in mycursor:
    if row_num == 1:
        cases_doc = CasesIndexDoc([row['tissue_source']], {"participant_id":[row['participant_id']], "tissue_type":[row['tissue_type']], "sample_type":[row['sample_type']]},{"sex":[row['sex']], "age":[row['age_binned']]})
        index_doc = IndexDoc(row["access"], row["platform"], row["experimental_strategy"], row["data_category"], row["workflow_type"], row["data_format"], row["data_type"], row["file_id"], row["file_name"], row["file_size"], row["protocol"], row["package_id"], cases_doc)
    else:
        index_doc.cases.tissue_source.append(row['tissue_source'])
        index_doc.cases.samples["participant_id"].append(row['participant_id'])
        index_doc.cases.samples["sample_type"].append(row['sample_type'])
        index_doc.cases.samples["tissue_type"].append(row['tissue_type'])
        index_doc.cases.demographics["age"].append(row['age_binned'])
        index_doc.cases.demographics["sex"].append(row['sex'])
    row_num += 1

print(get_index_update_json(index_doc.file_id) + "\n" +
      get_index_doc_json(index_doc))