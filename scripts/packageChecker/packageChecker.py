import pymongo
import logging
import requests
import os
from dotenv import load_dotenv

load_dotenv()

slack_passcode = os.environ.get('slack_passcode')
logger = logging.getLogger("packageChecker")
logging.basicConfig(level=logging.ERROR)
slack_url = "https://hooks.slack.com/services/" + slack_passcode
data_directory = os.environ.get('data_directory')

class PackageChecker:

    def __init__(self):
        try:
            mongo_client = pymongo.MongoClient("mongodb://localhost:27017/", serverSelectionTimeoutMS=5000)
            self.dataLake= mongo_client['dataLake']
        except:
            print("Unable to connect to database")

    def get_expected_files(self, package):
        expected_file_information = package['files']
        expected_file_names = []
        for file_info in expected_file_information:
            expected_file_names.append(file_info['fileName'])
        return expected_file_names

    def find_empty_packages(self):
        empty_package_list = []
        missing_package_list = []
        missing_files_list = []
        packages = self.dataLake.packages.find({})
        for package in packages:
            package_id = package["_id"]
            package_states = self.dataLake.state.find({"packageId": package_id}).sort("stateChangeDate", -1).limit(1)
            for state in package_states:
                if state['state'] == "UPLOAD_SUCCEEDED":
                    try:
                        directory = data_directory + "/package_";
                        files = os.listdir(directory + package_id)
                        expected_file_names = self.get_expected_files(package)
                        actual_file_names = []
                        if len(files) == 0:
                            empty_package_list.append(package_id)
                        else:
                            for file in files:
                                actual_file_names.append(file)
                                missing_files = set(expected_file_names) - set(actual_file_names)
                                missing_files_list = list(missing_files)
                                if file == "metadata.json" and len(files) == 1:
                                    empty_package_list.append(package_id)
                            if (not set(expected_file_names).issubset(set(actual_file_names))) and not all(p == "metadata.json" for p in actual_file_names):
                                empty_package_list.append(package_id)
                            
                    except:
                        missing_package_list.append(package_id)
                        
        if len(missing_files_list) > 0:
            print(missing_files_list)
            
        # if len(empty_package_list) > 0:
        #     message = "Missing files in packages: " + ', '.join(empty_package_list)
        #     print(message)
            # requests.post(
            #     slack_url,
            #     headers={'Content-type': 'application/json', },
            #     data='{"text":"' + message+'"}')
        # if len(missing_package_list) > 0:
        #     message = "Missing package directories for packages: " + ', '.join(missing_package_list)
        #     print(message)
            # requests.post(
            #     slack_url,
            #     headers={'Content-type': 'application/json', },
            #     data='{"text":"' + message + '"}')



if __name__ == "__main__":
    checker = PackageChecker()
    checker.find_empty_packages()
