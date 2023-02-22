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

class PackageChecker:

    def __init__(self):
        try:
            mongo_client = pymongo.MongoClient("mongodb://localhost:27017/", serverSelectionTimeoutMS=5000)
            self.dataLake= mongo_client['dataLake']
        except:
            print("Unable to connect to database")

    def find_empty_packages(self):
        empty_package_list = []
        missing_package_list = []
        packages = self.dataLake.packages.find({})
        for package in packages:
            package_id = package["_id"]
            package_states = self.dataLake.state.find({"packageId": package_id}).sort("stateChangeDate", -1).limit(1)
            for state in package_states:
                if state['state'] == "UPLOAD_SUCCEEDED":
                    try:
                        directory = "/data/dataLake/package_";
                        files = os.listdir(directory + package_id)
                        if len(files) == 0:
                            empty_package_list.append(package_id)
                        else:
                            for file in files:
                                print(file)
                                if file == "metadata.json" and len(files) == 1:
                                    empty_package_list.append(package_id)
                    except:
                        missing_package_list.append(package_id)

        if len(empty_package_list) > 0:
            message = "TESTING: Missing files in packages: " + ', '.join(empty_package_list)

            requests.post(
                slack_url,
                headers={'Content-type': 'application/json', },
                data='{"text":"' + message+'"}')
        if len(missing_package_list) > 0:
            message = "TESTING: Missing package directories for packages: " + ', '.join(missing_package_list)
            # requests.post(
            #     slack_url,
            #     headers={'Content-type': 'application/json', },
            #     data='{"text":"' + message + '"}')



if __name__ == "__main__":
    checker = PackageChecker()
    checker.find_empty_packages()