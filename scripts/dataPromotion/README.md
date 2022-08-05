# Install Python modules
pip install -r requirements.txt

# Moving files from DL to S3

About:
This script reads the Datalake database to find files that have not yet been copied into the S3 bucket based on the release version of the data. Due to different package requirements, the script contains partially unique paths depending on the metadata_type_id. E.g. We zip a package with the exclusion of a few files prior to uploading.

Setup and running:

1.  Setup the .env - filesToS3.py requires the following .env variables are set:

    `destination_bucket` - the name of the S3 bucket to which the files will be moved

    `datalake_dir` - the directory in the datalake where the files are located

    `source_bucket` - Another directory to try searching if the datalake dir does not contain the files

    `mysql_user` - To access the file table inside of the knowledge environment database

    `mysql_pwd` - To access the file table inside of the knowledge environment database


2. The script also requires an argument when calling the script to indicate the release version of files to move. You'll need to get this number before you run the script.

    `-v` or `--release_version`

3. Make sure you have tunnel open to the KE database, e.g.

    `$ ssh atlas-ke -i ~/.ssh/um-kpmp.pem -L 3306:localhost:3306`

4. Execute the Datalake to S3 move script

      `$ python filesToS3.py --release-version  0`



# Move the files from “file_pending” table to the “file” table in the Staging DB
1. Requires a connection to the DLU Mongo and the Staging DB MySQL (e.g. through tunnels)
2. Expression Matrix files get a filesize of 0 and should be updated when they are created.
3. Execute 'python filesToKE.py'

# Adds clinical data and participants from a CSV file to the Staging Database
1. Requires a connection to the Staging DB MySQL (e.g. through a tunnel)
2. Edit script to point to clinical .csv file
3. Execute 'python clinicalToKE.py'
