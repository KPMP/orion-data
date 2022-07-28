from zipfile import ZipFile
import os

EXCLUDED_TYPES = ['.jpg', 'metadata.json', '.DS_Store']

def is_not_excluded_type(filename: str, excludedTypes: list):
    for excludedType in excludedTypes:
        
        if filename.lower().endswith(excludedType.lower()):
            return False
    return True

def zip_package_data(zipName: str, folderToZip: str):
    with ZipFile(zipName+'.zip', 'w') as zippedPackage:
        for file in os.walk(folderToZip):
            for filename in file[2]:
                if is_not_excluded_type(filename, EXCLUDED_TYPES):
                    zippedPackage.write(file[0]+'/'+filename)
                    
    return zipName

if __name__ == "__main__":
    zip_package_data('packageid_lipidomics', 'folder-to-zip/')
