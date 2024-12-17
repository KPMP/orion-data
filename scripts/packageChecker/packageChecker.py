import pymongo
import logging
import requests
import os
import shutil
import csv
from dotenv import load_dotenv
import numpy as np
import argparse

load_dotenv()

slack_passcode = os.environ.get('slack_passcode')
logger = logging.getLogger("packageChecker")
logging.basicConfig(level=logging.DEBUG)
slack_url = "https://hooks.slack.com/services/" + slack_passcode
data_directory = os.environ.get('data_directory')


class PackageChecker:

    def __init__(self):
        try:
            mongo_client = pymongo.MongoClient("mongodb://localhost:27017/", serverSelectionTimeoutMS=5000)
            self.dataLake = mongo_client['dataLake']
        except:
            print("Unable to connect to database")

    def get_expected_files(self, package):
        expected_file_information = package['files']
        expected_file_names = []
        for file_info in expected_file_information:
            expected_file_names.append(file_info['fileName'])
        return expected_file_names

    def find_empty_packages(self, move_derived=False):
        empty_package_list = []
        missing_package_list = []
        extra_package_list = []

        missing_files_header = ["Package ID", "Missing Files"]
        missing_files_csv = open("missing_files.csv", "w")
        missing_writer = csv.writer(missing_files_csv)
        missing_writer.writerow(missing_files_header)

        extra_files_header = ['Package ID', 'Extra Files']
        extra_files_csv = open("extra_files.csv", "w")
        extra_writer = csv.writer(extra_files_csv)
        extra_writer.writerow(extra_files_header)
        packages = self.dataLake.packages.find({})
        mongo_files = self.dataLake.files.find({})
        mongo_files_col_list = []

        for file_names in mongo_files:
            file_name = file_names['fileName']
            mongo_files_col_list.append(file_name)

        for package in packages:
            package_id = package["_id"]
            package_states = self.dataLake.state.find({"packageId": package_id}).sort("stateChangeDate", -1).limit(1)
            for state in package_states:
                if state['state'] == "UPLOAD_SUCCEEDED":
                    try:
                        directory = data_directory + "/package_" + package_id;
                        files = os.listdir(directory)
                        expected_file_names = self.get_expected_files(package)
                        actual_file_names = []
                        if len(files) == 0:
                            empty_package_list.append(package_id)
                        else:
                            for file in files:
                                ext = os.path.splitext(file)
                                if not ext[1] == ".bfmemo" and not "derived" in ext[0]:
                                    actual_file_names.append(file)

                                if file == "metadata.json" and len(files) == 1:
                                    empty_package_list.append(package_id)

                            if (not set(expected_file_names).issubset(set(actual_file_names))) and not all(
                                    p == "metadata.json" for p in actual_file_names):
                                empty_package_list.append(package_id)

                        missing_files_list = set(expected_file_names).difference(set(actual_file_names))
                        missing_files_list = ', '.join(missing_files_list)
                        disk_files = set(actual_file_names).difference(set(expected_file_names))

                        if "metadata.json" in disk_files:
                            disk_files.remove("metadata.json")

                        disk_files = ", ".join(disk_files)
                        files_list = np.setdiff1d(disk_files, mongo_files_col_list)
                        extra_files_list = list(files_list)

                        if '' in extra_files_list:
                            extra_files_list.remove("")

                        if len(missing_files_list) != 0 and file_name not in missing_files_list:
                            data = [
                                [package_id, missing_files_list]
                            ]
                            missing_writer.writerows(data)

                        if len(extra_files_list) != 0 and file_name not in extra_files_list:
                            extra_package_list.append(package_id)
                            data = [
                                [package_id, extra_files_list]
                            ]
                            extra_writer.writerows(data)
                            if (move_derived):
                                for file_name in str(extra_files_list).split(", "):
                                    clean_file_name = file_name.replace("[", "").replace("]", "").replace(",", "").replace("'", "")
                                    if clean_file_name != 'derived' and 'svs' not in clean_file_name:
                                        self.move_file_to_derived(directory, clean_file_name)

                    except:
                        missing_package_list.append(package_id)

        missing_files_csv.close()
        extra_files_csv.close()
        if len(empty_package_list) > 0:
            message = "Missing files in packages: " + ', '.join(empty_package_list)
            # requests.post(
            #     slack_url,
            #     headers={'Content-type': 'application/json', },
            #     data='{"text":"' + message + '"}')
        if len(missing_package_list) > 0:
            message = "Missing package directories for packages: " + ', '.join(missing_package_list)
            # requests.post(
            #     slack_url,
            #     headers={'Content-type': 'application/json', },
            #     data='{"text":"' + message + '"}')
        if len(extra_package_list) > 0:
            message = "Extra files for packages: " + ', '.join(extra_package_list)
            # requests.post(
            #     slack_url,
            #     headers={'Content-type': 'application/json', },
            #     data='{"text":"' + message + '"}')

    def move_file_to_derived(self, package_directory, file_name):
        derived_dir = package_directory + "/derived"
        file_path = package_directory + "/" + file_name
        if os.path.isfile(file_path):
            print(f"Moving file '{file_path}' to '{derived_dir}'.")
            if not os.path.exists(derived_dir):
                os.makedirs(derived_dir)

            try:
                shutil.move(file_path, derived_dir)
                print(f"File '{file_path}' moved to '{derived_dir}' successfully.")
            except FileNotFoundError:
                print(f"Error: File '{source_file}' not found.")
            except Exception as e:
                print(f"An error occurred: {e}")
        else:
            print(f"'{file_path}' is not a file. Skipping.")



if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-m",
        "--move_derived",
        required=False,
        action='store_true'
    )
    args = parser.parse_args()
    checker = PackageChecker()
    checker.find_empty_packages(args.move_derived)
