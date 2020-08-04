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

## Option 3:
### Generate using the Knowledge Environment database. 
Requirements: Make sure the knowledge_environment MYSQL database is available on 3306.
### All Records
1. Execute python datalakeDatabaseToAtlasIndex.py' without arguments 
### By release version
1. Execute python datalakeDatabaseToAtlasIndex.py -v <release_ver>'
### Per file
1. Execute python datalakeDatabaseToAtlasIndex.py -f <file_>'

