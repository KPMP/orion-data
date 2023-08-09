## Overview
This script is meant to copy the information from the data lake mongo database into the DMD tables. This is meant to be run once to move the data over and we will have other code that will continually update these tables as things happen in the data lake uploader.

## Requirements
You will need python3 and pip installed on your machine. You will also need to create a tunnel to the data lake Mongo of your choosing (example using my mappings) and chose a different local port so as not to conflict with the local Mongo:

    ssh kpmpUploader-prod-supernova1 -L 27018:localhost:27017

Additionally, you will need a tunnel to the DMD tables of your choosing (note that these run on 3307):

    ssh kpmpUploader-prod-supernova1 -L 3307:localhost:3307

You will need to install the necessary libraries:

    pip install -r requirements.txt

Lastly, you will run the script thusly (filling in the correct password):

    python3 oneTimePopulation.py -dluHost=localhost -dluPort=27018 -dmdHost=localhost -dmdUser=root -dmdPass='<password>'

The script will take at least 10 minutes to run as it needs to loop through all of the packages and all of the files associated with each package to fill in the necessary tables.

## Results
At the end, you should see that the dlu_file and the dlu_package_inventory tables are both filled in. 
