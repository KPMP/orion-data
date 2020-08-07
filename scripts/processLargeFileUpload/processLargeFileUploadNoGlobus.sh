#!/bin/bash

if [ -z "$1" ]
  then
    echo "ERROR -- Missing parameter. Usage: ./processLargeFileUploadNoGlobus.sh [packageID]. "
    exit -1
fi

packageId=$1

packageDir="/data/dataLake/package_$packageId"
globusDir="/globus/${GLOBUS_DIR}/${packageId}"

function checkEmptyDir {
   if [ $(ls -A "$1" | wc -l) -eq 0 ]; then
      echo "ERROR -- No files found in ${globusDir}. "
      exit -1
   fi
}

checkEmptyDir $globusDir

mkdir -p "$packageDir"
rm "$packageDir/metadata.json"

gFiles=("${globusDir}"/*)

directoryAlreadyFound=false
for file in "${gFiles[@]}"; do
   if [ -d "$file" ]; then
      if [ "$directoryAlreadyFound" = true ]; then
         echo "ERROR -- Too many subdirectories. "
         exit -1
      fi
      globusDir=$file
      echo "Setting Globus path to subdirectory: $file . "
      directoryAlreadyFound=true
   fi
done

checkEmptyDir $globusDir

gFiles=("${globusDir}"/*)

for file in "${gFiles[@]}"; do
   if [ -d "$file" ]; then
      echo "ERROR -- Too many nested subdirectories. "
      exit -1
   fi
done

cp "${globusDir}"/* "${packageDir}"

dlFiles=("${packageDir}"/*)

mismatchedFiles=($(echo ${gFiles[@]##*/} ${dlFiles[@]##*/} | tr ' ' '\n' | sort | uniq -u ))

if [ "${#mismatchedFiles[@]}" -gt 0 ]; then
   echo "ERROR -- The following filenames don't match: ${mismatchedFiles[*]}. "
   exit -1
fi

for file in "${gFiles[@]}"; do
   if [ $(stat -c%s "$file") -ne $(stat -c%s "$packageDir/${file##*/}") ]; then
      echo "ERROR -- File size mismatch for $file. "
      exit -1
   fi
done

node scripts/processLargeFileUpload/updateMongo.js "${packageId}"

result=$?

if [ $result == 0 ]; then
	java -cp build/libs/orion-data.jar -Dloader.main=org.kpmp.RegenerateZipFiles org.springframework.boot.loader.PropertiesLauncher
	zipResult=$?
	if [ $zipResult == 0 ]; then
		data='{"packageId":"'
		data+=$packageId
		data+='", "state":"UPLOAD_SUCCEEDED", "largeUploadChecked":true}'
		url="http://state-spring:3060/v1/state/host/upload_kpmp_org"
		curl --header "Content-Type: application/json" --request POST --data "${data}" "${url}"
                curl "http://localhost:3030/v1/clearCache"
	else
		echo "Zip failed...exiting. "
		exit -1
	fi
else
	echo "Mongo update failed...exiting. "
	exit -1
fi

exit
