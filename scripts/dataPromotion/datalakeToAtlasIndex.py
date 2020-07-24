#!/usr/bin/env python2
import pymongo
from collections import OrderedDict
import json
import copy
import csv
import sys

class MetadataType:
    def __init__(self, experimental_strategy, data_type, data_category, data_format, platform, access, file_name_match_string):
        self.experimental_strategy = experimental_strategy
        self.data_type = data_type
        self.data_category = data_category
        self.data_format = data_format
        self.platform = platform
        self.access = access
        self.file_name_match_string = file_name_match_string

class IndexDoc:
    def __init__(self, metadata_type, file_id, file_name, file_size, project, sample_id, package_id, cases):
        self.access = metadata_type.access
        self.platform = metadata_type.platform
        self.experimental_strategy = metadata_type.experimental_strategy
        self.data_category = metadata_type.data_category
        self.file_id = file_id
        self.file_name = file_name
        self.data_format = metadata_type.data_format
        self.file_size = file_size
        self.data_type = metadata_type.data_type
        self.project = project
        self.sample_id = sample_id
        self.package_id = package_id
        self.cases = cases

class CasesIndexDoc:
    def __init__(self, provider, samples, demographics):
        self.provider = provider
        self.samples = samples
        self.demographics = demographics


def get_selector(text, array):
    j = 1
    select_text = ""
    for item in array:
        select_text += str(j) + " : " + item + "\n"
        j += 1
    array_index = int(raw_input(text +  " \n" + select_text)) - 1
    return array[array_index]

def print_index_update_json(id):
    print('{"update":{"_index":"file_cases","_id":"' + id + '"}}')

def print_index_doc_json(index_doc):
    index_doc.cases = index_doc.cases.__dict__
    print('{"doc":' + json.dumps(index_doc.__dict__) + ',"doc_as_upsert":true}')

input_file_name = './package_to_atlas_index.csv'

m1_dt_wsi = MetadataType("", "Whole Slide Images", "Pathology", "svs", "", "open", ".svs")
m2_dt_single_nuc_rna = MetadataType("Single-nucleus RNA-Seq", "Transcriptomics", "Molecular", "bam", "10x Genomics", "controlled", ".bam")
m9_dt_sub_seg_trans = MetadataType("Sub-segmental Transcriptomics", "Transcriptomics", "Molecular", "bam", "LMD Transcriptomics", "controlled", ".bam")

m3_dt_single_nuc_rna = copy.copy(m2_dt_single_nuc_rna)
m3_dt_single_nuc_rna.data_format = "fastq"
m3_dt_single_nuc_rna.file_name_match_string = ".fastq.gz"

m4_dt_single_nuc_rna = copy.copy(m2_dt_single_nuc_rna)
m4_dt_single_nuc_rna.data_format = "tsv mtx"
m4_dt_single_nuc_rna.access = "open"

m5_dt_single_nuc_rna = copy.copy(m2_dt_single_nuc_rna)
m5_dt_single_nuc_rna.data_format = "tsv"
m5_dt_single_nuc_rna.access = "open"
m5_dt_single_nuc_rna.platform = "snDrop-seq"
m5_dt_single_nuc_rna.file_name_match_string = ".tsv"

m0_dt_metadata = copy.copy(m2_dt_single_nuc_rna)
m0_dt_metadata.data_format = "xlsx"
m0_dt_metadata.platform = ""
m0_dt_metadata.access = "open"
m0_dt_metadata.file_name_match_string = ".xlsx"

metadata_types = OrderedDict()
metadata_types["0"] = m0_dt_metadata
metadata_types["1"] = m1_dt_wsi
metadata_types["2"] = m2_dt_single_nuc_rna
metadata_types["3"] = m3_dt_single_nuc_rna
metadata_types["4"] = m4_dt_single_nuc_rna
metadata_types["5"] = m5_dt_single_nuc_rna
metadata_types["9"] = m9_dt_sub_seg_trans

data_type_select = ""
for metadata_num, metadata_type in metadata_types.items():
    metadata_type_name = metadata_type.data_type + ", " + metadata_type.experimental_strategy + ", " + metadata_type.data_format + ", " + metadata_type.access
    data_type_select += metadata_num + " : " + metadata_type_name + "\n"

if len(sys.argv) > 1 and sys.argv[1] == '-f':
    using_file_answer = 'Y'
    if sys.argv[2]:
        input_file_name = sys.argv[2]
else:
    using_file_answer = raw_input('Are you using the "package_to_atlas_index.csv" file?')

def process_update_row(row):
    selected_metadata_type = metadata_types[row['metadata_type_num']]
    cases_doc = CasesIndexDoc([row['tissue_source']], {"sample_id":[row['participant_id']], "tissue_type":[row['tissue_type']], "sample_type":[row['sample_type']]},{"sex":[row['sex']], "age":[row['age']]})

    if selected_metadata_type.data_format == "tsv mtx":
        file_name = row['package_id'] + "_" + "expression_matrix.zip"
        index_doc = IndexDoc(selected_metadata_type, row['package_id'], file_name, row['file_size_exp_matrix_only'], row['protocol'], row['participant_id'], row['package_id'], cases_doc)
        print_index_update_json(row['package_id'])
        print_index_doc_json(index_doc)
    else:
        mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
        database = mongo_client["dataLake"]
        packages = database["packages"]

        result = packages.find_one({ "_id": row['package_id'] }, {"files":1})
        if not result is None:
            if result['files'] > 0:
                found_a_file = False
                for file in result['files']:
                    if file["fileName"].endswith(selected_metadata_type.file_name_match_string):
                        found_a_file = True
                        file_name = file["_id"] + "_" + file["fileName"]
                        print_index_update_json(file["_id"])
                        index_doc = IndexDoc(selected_metadata_type, file["_id"], file_name, file["size"], row['protocol'], row['participant_id'], row['package_id'], cases_doc)
                        print_index_doc_json(index_doc)
                if not found_a_file:
                    print("No files found in package matching " + selected_metadata_type.file_name_match_string + " extension")
            else:
                print("No files found in package " + package_id)
        else:
            print("Could not find any packages for ID " + package_id)

if using_file_answer not in ('Y', 'yes', 'Yes', 'y'):
    project = get_selector("Select Tissue Source: ", ["Pilot1", "KPMP Recruitment Site"])
    package_id = raw_input("Enter the package ID: ")
    participant_id = raw_input("Enter the participant ID: ")
    metadata_type_num = raw_input("Select a metadata scheme number: \n" + data_type_select)
    sample_type = ""
    tissue_type = ""
    sex = ""
    age = ""
    process_update_row({"project":project,"package_id":package_id,"participant_id":participant_id,"tissue_type":tissue_type,"sample_type":sample_type,"sex":sex,"age":age,"metadata_type_num":metadata_type_num})
else:
    with open(input_file_name) as csv_file:
        csv_reader = csv.DictReader(csv_file)
        no_rows = True
        for row in csv_reader:
            no_rows = False
            process_update_row(row)

print("\n")
