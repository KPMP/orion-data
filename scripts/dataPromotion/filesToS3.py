from dotenv import load_dotenv
import os
import mysql.connector
from argparse import ArgumentParser
from package_zipper import packageZipper

load_dotenv()

destination_bucket = os.environ.get('destination_bucket')
source_bucket = os.environ.get('source_bucket')
datalake_dir = os.environ.get('datalake_dir')

mysql_user = os.environ.get('mysql_user')
mysql_pwd = os.environ.get('mysql_pwd')

parser = ArgumentParser(description='Move files to S3')
parser.add_argument('-v', '--release_ver',
                    dest='release_ver',
                    help='target release version',
                    required=True)

args = parser.parse_args()
global update_count
update_count = 0
try:
    mydb = mysql.connector.connect(
        host='localhost',
        user=mysql_user,
        password=mysql_pwd,
        database='knowledge_environment'
    )
    mydb.get_warnings = True
    cursor = mydb.cursor(buffered=True)
    cursor2 = mydb.cursor(buffered=True)
except:
    print('Can\'t connect to MySQL')
    print('Make sure you have tunnel open to the KE database, e.g.')
    print('ssh atlas-ke -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306')
    os.sys.exit()

def update_file_size(file_path, file_id):
    file_size = os.path.getsize(file_path)
    values = (file_size, file_id)
    update_sql = 'UPDATE file SET file_size = %s WHERE file_id = %s'
    print(update_sql % values)
    cursor2.execute(update_sql, values)

def fetch_expression_file_names(package_id, metadata_type_id):
    query2 = 'SELECT file_name FROM file_pending WHERE package_id = %s AND metadata_type_id = %s'
    cursor2.execute(query2, (package_id, metadata_type_id))
    expression_file_names = cursor2.fetchone()[0].replace(';', '')
    return expression_file_names

def copy_local_file_to_s3_destination(file_path, object_name):
    command_string = 'aws s3 cp "' + file_path + '" s3://' + destination_bucket + '/' + object_name
    print(command_string)
    response = os.system(command_string)
    if(response != 0):
        raise Exception('Failed to upload file ' + file_path + ' onto s3 bucket ' + destination_bucket + '/' + object_name)
    else:
        print('File copied to S3')

def copy_s3_file_to_s3_destination(source_object, object_name):
                    command_string = 'aws s3 cp "s3://' + source_object + '" "s3://' + destination_bucket + '/' + object_name + '"'
                    os.system(command_string)


def insert_into_moved_files(file_name):
                    insert_sql = 'INSERT INTO moved_files (file_name) VALUES (%s)'
                    cursor2.execute(insert_sql, (file_name,))
                    mydb.commit()

def move_files(file_path, object_name, file_name):
    if os.path.exists(file_path):
        try:
            print('Moving ' + object_name)
            copy_local_file_to_s3_destination(file_path, object_name)
            global update_count
            update_count = update_count + 1
            insert_into_moved_files(file_name)
        except Exception as err:
            print(err)
            pass
    else:
        source_object = source_bucket + '/package_' + package_id + '/' + original_file_name
        print('File not found locally. Trying S3: ' + source_object)
        try:
            copy_s3_file_to_s3_destination(source_object, object_name)
            update_count = update_count + 1
            insert_into_moved_files(file_name)
        except:
            print(err)
            pass

query = ('SELECT ar.file_id, f.package_id, f.file_name, ar.metadata_type_id ' \
        'FROM ar_file_info as ar ' \
        'INNER JOIN file as f ' \
        'ON f.file_id = ar.file_id ' \
        'WHERE ar.release_version = {0} ' \
        'AND f.file_name ' \
        'NOT IN (SELECT file_name FROM moved_files)').format(args.release_ver)
cursor.execute(query)

for (file_id, package_id, file_name, metadata_type_id) in cursor:

    datalake_package_dir = datalake_dir + '/package_' + package_id + '/'
    original_file_name = file_name[37:]
    file_path = datalake_package_dir + original_file_name
    expression_file_names = 'barcodes.tsv.gz features.tsv.gz matrix.mtx.gz'

    if file_name:
        print('Looking for: ' + file_path)
        if file_name.endswith('expression_matrix.zip'):
            object_name = package_id + '/' + file_name
            if metadata_type_id == 21:
                expression_file_names = fetch_expression_file_names(package_id, metadata_type_id)

            print('Creating expression matrix zip file for: ' + expression_file_names)
            expression_file_names_arr = expression_file_names.split()
            
            if not os.path.exists(datalake_package_dir + expression_file_names_arr[0]):
                for expression_file_name in expression_file_names_arr:
                    source_object = source_bucket + '/package_' + package_id + '/' + expression_file_name
                    command_string = 'aws s3 cp s3://' + source_object + ' ' + datalake_package_dir + expression_file_name
                    print(command_string)
                    os.system(command_string)
          
            command_string = 'cd ' + datalake_package_dir + ' && zip expression_matrix.zip ' + expression_file_names
            print(command_string)
            os.system(command_string)
            update_file_size(file_path, file_id)
            move_files(file_path, object_name, file_name)
        else:
            print('Processing file ' + str(file_id), str(package_id), file_name, str(metadata_type_id))
            omicsType = ''
            # TODO: update values. The metadata_type_id here is a placeholder, these values will change based on the work Rachel Does
            if metadata_type_id == 41:
                omicsType = 'spatial_lipidomics'
            elif metadata_type_id == 44:
                omicsType = 'spatial_metabolomics'
            elif metadata_type_id == 45:
                omicsType = 'spatial_n-glycomics'

            zipName = '{0}_{1}.zip'.format(package_id, omicsType)
            packageZipper.zip_package_data(zipName, datalake_package_dir, package_id)
            update_file_size(file_path, file_id)
            move_files(file_path, zipName, file_name)
            packageZipper.zip_package_cleanup(zipName)
    else:
        print('No file name in record.')
    print('\n')


mydb.commit()
mydb.close()
print(str(update_count) + ' files moved')
