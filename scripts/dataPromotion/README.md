# Install Python modules
pip install -r requirements.txt

# Moving files from DL to S3
1. Add packageIDs,filenames to files_to_s3.txt one per line
2. Execute 'python datalakeToS3.py'

# Creating / Updating Index Records for Atlas portal
## Option 1: 
1. Execute 'python datalakeToAtlasIndex.py'
2. Follow prompts

## Option 2:
1. Create a comma-delimited file with the same headers as package_to_atlas_index.csv
2. Execute 'python datalakeToAtlasIndex.py -f my_package_to_atlas_index.csv'
3. Redirect output to file or copy/paste into your favorite POST client. 
