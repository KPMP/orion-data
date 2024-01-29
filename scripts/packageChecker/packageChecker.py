import pymongo
import logging
import requests
import os
import csv
from dotenv import load_dotenv
import numpy as np

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
            
    def check_list(sub, test_str):
      for ele in sub:
        if ele in test_str:
          return 0
        return 1

    def get_expected_files(self, package):
        expected_file_information = package['files']
        expected_file_names = []
        for file_info in expected_file_information:
            expected_file_names.append(file_info['fileName'])
        return expected_file_names

    def find_empty_packages(self):
        empty_package_list = []
        missing_package_list = []
        
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
                        directory = data_directory + "/package_";
                        files = os.listdir(directory + package_id)
                        expected_file_names = self.get_expected_files(package)
                        actual_file_names = []
                        if len(files) == 0:
                            empty_package_list.append(package_id)
                        else:
                            for file in files:
                                actual_file_names.append(file)
                                
                                if file == "metadata.json" and len(files) == 1:
                                    empty_package_list.append(package_id)
                                    
                            if (not set(expected_file_names).issubset(set(actual_file_names))) and not all(p == "metadata.json" for p in actual_file_names):
                                empty_package_list.append(package_id)
                                
                        missing_files_list = set(expected_file_names).difference(set(actual_file_names)) 
                        missing_files_list = ', '.join(missing_files_list)
                        disk_files = set(actual_file_names).difference(set(expected_file_names))
                        disk_files = ", ".join(disk_files)
                        
                        extra_files_list = np.setdiff1d(disk_files, mongo_files_col_list)
                        
                        extra_files_list.remove("metadata.json")
                        
                        
                        if len(missing_files_list) != 0 and file_name not in missing_files_list:
                          data = [
                            [package_id, missing_files_list]
                          ]
                          missing_writer.writerows(data)
                          
                        if len(extra_files_list) != 0 and file_name not in extra_files_list:
                          data = [
                            [package_id, extra_files_list]
                          ]
                          extra_writer.writerows(data)
                    except:
                        missing_package_list.append(package_id)
                      
        missing_files_csv.close()
        extra_files_csv.close()
            
        # if len(empty_package_list) > 0:
        #     message = "Missing files in packages: " + ', '.join(empty_package_list)
        #     requests.post(
        #         slack_url,
        #         headers={'Content-type': 'application/json', },
        #         data='{"text":"' + message+'"}')
        # if len(missing_package_list) > 0:
        #     message = "Missing package directories for packages: " + ', '.join(missing_package_list)
        #     requests.post(
        #         slack_url,
        #         headers={'Content-type': 'application/json', },
        #         data='{"text":"' + message + '"}')


if __name__ == "__main__":
    checker = PackageChecker()
    checker.find_empty_packages()
