# Install Python modules
pip install -r requirements.txt

# Moving files from DL to S3
1. Add packageIDs,filenames to files_to_s3.txt one per line
2. Execute 'python filesToS3.py'

# Move the files from “file_pending” table to the “file” table in the Staging DB
1. Requires a connection to the DLU Mongo and the Staging DB MySQL (e.g. through tunnels)
2. Expression Matrix files get a filesize of 0 and should be updated when they are created.
3. Execute 'python filesToKE.py'

# Adds clinical data and participants from a CSV file to the Staging Database
1. Requires a connection to the Staging DB MySQL (e.g. through a tunnel)
2. Edit script to point to clinical .csv file
3. Execute 'python clinicalToKE.py'
