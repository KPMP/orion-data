import json

with open('/Users/rlreamy/Desktop/redcap/redcap-chunk1.json') as file:
    data = json.load(file)

questions = data['redcap_metadata_filtered']

# for key in questions:
#     print(key['field_label'] + '\t' + key['field_name'])
#
# transform_questions = data[0]['transform_metadata']['CalcVars']
# for key in transform_questions:
#     print(key['description'] + '\t' + key['field_name'])

redcap_records = data['transform_records']
redcap_records_fields = set()
redcap_records_dict = dict()

for key in redcap_records:
    print(key)
    redcap_records_fields.add(key['field_name'])
    record_dict = dict()
    record_dict[key['field_name']] = key['field_value']

    if (key['record_id'] in redcap_records_dict) :
        redcap_records_dict[key['record_id']][key['field_name']] = key['field_value']
    else:
        redcap_records_dict[key['record_id']] = dict()
        redcap_records_dict[key['record_id']][key['field_name']] = key['field_value']

header = 'KPMP_Id' + '\t'
for field in redcap_records_fields:
    header += field + '\t'

print(header)

for key in redcap_records_dict:
    line = key + '\t'
    for field in redcap_records_fields:
        if field in redcap_records_dict[key]:
            line += str(redcap_records_dict[key][field]) + '\t'
        else:
            line += '\t'
#    print(line)

