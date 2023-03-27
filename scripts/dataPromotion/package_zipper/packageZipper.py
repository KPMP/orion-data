from zipfile import ZipFile
import os

EXCLUDED_TYPES = ['.jpg', 'metadata.json', '.DS_Store']

def is_not_excluded_type(filename: str, excludedTypes: list):
    for excludedType in excludedTypes:
        
        if filename.lower().endswith(excludedType.lower()):
            return False
    return True

def zip_package_data(zipName: str, folderToZip: str, packageId: str):
    with ZipFile(zipName, 'w') as zippedPackage:
        for root, dir, files in os.walk(folderToZip):
            for filename in files:
                if is_not_excluded_type(filename, EXCLUDED_TYPES):
                    zippedPackage.write(folderToZip+'/'+filename, 'package_'+packageId+'/'+filename)

def zip_package_cleanup(zipName: str):
    os.remove(zipName)

if __name__ == "__main__":
    zip_package_data('packageid_lipidomics.zip', 'folder-to-zip/', 'packageid')
