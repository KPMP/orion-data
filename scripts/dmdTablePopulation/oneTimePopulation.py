import pymongo
from pymongo import MongoClient
from argparse import ArgumentParser
import mysql.connector
import logging

parser = ArgumentParser(description='Dump from DLU to DMD tables')
parser.add_argument('-dluHost', '--dlu-mongo-host', dest='mongo_host', help='hostname for dlu mongo', required=True)
parser.add_argument('-dluPot', '--dlu-mongo-port', dest='mongo_port', help='port for dlu mongo', required=True)
parser.add_argument('-dmdHost', '--dmd-mariadb-host', dest='mariadb_host', help='hostname for dmd mariadb',
                    required=True)
parser.add_argument('-dmdPass', '--dmd-mariadb-password', dest='mariadb_pass', help='password for dmd tables',
                    required=True)
parser.add_argument('-dmdUser', '--dmd-mariadb-user', dest='mariadb_user', help='username for dmd tables',
                    required=True)

args = parser.parse_args()


def get_data_lake():
    try:
        CONNECTION_STRING = "mongodb://" + args.mongo_host + ":" + args.mongo_host + "/"
        client = MongoClient(CONNECTION_STRING, serverSelectionTimeoutMS=5000)
        database = client['dataLake']
        return database
    except:
        logging.error('Unable to connect to dlu mongo with connection string: ' + CONNECTION_STRING)
        exit(-1)


def get_dmd_connection():
    try:
        dmd = mysql.connector.connect(
            host=args.mariadb_host,
            user=args.mariadb_user,
            password=args.mariadb_pass,
            port=3307,
            database="data_management",
            autocommit=True,
            connect_timeout=5000
        )
        dmd.get_warnings = True
        return dmd
    except Exception as err:
        logging.error('Unable to connect to dmd')
        logging.error(err)
        exit(-1)


def get_submitter_name(data_lake, package):
    submitter_collection = data_lake['users']
    submitter = submitter_collection.find_one({'_id': package['submitter'].id})
    first_name = ''
    last_name = ''
    if submitter['firstName'] is not None:
        first_name = submitter['firstName']
    if submitter['lastName'] is not None:
        last_name = submitter['lastName']
    full_name = first_name + ' ' + last_name
    return full_name


def is_error(data_lake, package):
    state_collection = data_lake['state']
    package_id = package['_id']
    states = state_collection.find({'packageId': package_id}).sort('stateChangeDate', pymongo.DESCENDING)

    try:
        state = states.next()
        current_state = state['state']
        if 'FAILED' in current_state:
            return 1
        else:
            return 0
    except StopIteration:
        logging.error('Found no states for given package: ' + package_id)


def insert_packages(data_lake, dmd):
    packages_collection = data_lake['packages']

    for package in packages_collection.find():
        full_name = get_submitter_name(data_lake, package)
        package_in_error = is_error(data_lake, package)
        insert_query = "INSERT INTO dlu_package_inventory (dlu_package_id, dlu_created, dlu_tis, dlu_packageType, " \
                       "dlu_subject_id, dlu_lfu, dlu_submitter, dlu_error) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"

        large_file_upload = 0
        if 'largeFilesChecked' in package:
            if package['largeFilesChecked']:
                large_file_upload = 1

        try:
            cursor = dmd.cursor(buffered=False)
            cursor.execute(insert_query, (
                package['_id'], package['createdAt'], package['tisName'], package['packageType'], package['subjectId'],
                large_file_upload, full_name, package_in_error))
            insert_files(package, dmd)
        except Exception as error:
            logging.error(f'Unable to insert package {package}. Error: {error}')
            raise error


def insert_files(package, dmd):
    files = package['files']
    for file in files:
        insert_query = 'INSERT INTO dlu_file (dlu_fileName, dlu_package_id, dlu_file_id, dlu_filesize, dlu_md5checksum)' \
                       'VALUES(%s, %s, %s , %s, %s)'

        checksum = None
        if 'md5checksum' in file:
            checksum = file['md5checksum']

        try:
            cursor = dmd.cursor(buffered=False)
            cursor.execute(insert_query, (file['fileName'], package['_id'], file['_id'], int(file['size']), checksum))
        except Exception as error:
            logging.error(f'Unable to insert record: {file} for package: {package}. Error: {error}')
            exit(0)



if __name__ == '__main__':
    insert_packages(get_data_lake(), get_dmd_connection())
