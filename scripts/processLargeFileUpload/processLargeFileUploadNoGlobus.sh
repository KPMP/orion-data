#!/bin/bash

source "${currentDir}"/.env

echo "**** It would be wise to start me in Screen ***"

echo "Package ID:"
read packageId

packageDir="/data/dataLake/package_$packageId"
globusDir="/globus/PROD_INBOX/${packageId}"

function checkEmptyDir {
   if [ -z "$(ls -A $1)" ]; then
      echo "ERROR -- No files found in ${globusDir}"
      exit -1
   fi
}

checkEmptyDir $globusDir

rm "$packageDir/metadata.json"

gFiles=("${globusDir}"/*)

directoryAlreadyFound=false
for file in "${gFiles[@]}"; do
   if [ -d "$file" ]; then
      if [ "$directoryAlreadyFound" = true ]; then
         echo "ERROR -- Too many subdirectories"
         exit -1
      fi
      globusDir=$file
      echo "Setting Globus path to subdirectory: $file"
      directoryAlreadyFound=true
   fi
done

checkEmptyDir $globusDir

gFiles=("${globusDir}"/*)

for file in "${gFiles[@]}"; do
   if [ -d "$file" ]; then
      echo "ERROR -- Too many nested subdirectories"
      exit -1
   fi
done

if [ "${#gFiles[@]}" -eq 0 ]; then
   echo "ERROR -- No files found in ${globusDir}"
   exit -1
fi

cp "${globusDir}"/* "${packageDir}"

dlFiles=("${packageDir}"/*)

mismatchedFiles=($(echo ${gFiles[@]##*/} ${dlFiles[@]##*/} | tr ' ' '\n' | sort | uniq -u ))

if [ "${#mismatchedFiles[@]}" -gt 0 ]; then
   echo "ERROR -- The following filenames don't match: ${mismatchedFiles[*]}"
   exit -1
fi

for file in "${gFiles[@]}"; do
   if [ $(stat -c%s "$file") -ne $(stat -c%s "$packageDir/${file##*/}") ]; then
      echo "ERROR -- File size mismatch for $file"
      exit -1
   fi
done

node updateMongo.js "${packageId}"

result=$?

if [ $result == 0 ]; then
	java -cp /home/pathadmin/apps/orion-data/build/libs/orion-data.jar -Dloader.main=org.kpmp.RegenerateZipFiles org.springframework.boot.loader.PropertiesLauncher
	zipResult=$?
	if [ $zipResult == 0 ]; then
		data='{"packageId":"'
		data+=$packageId
		data+='", "state":"UPLOAD_SUCCEEDED", "largeUploadChecked":true}'
		url="http://localhost:3060/v1/state/host/${DLU_INSTANCE}"
		curl --header "Content-Type: application/json" --request POST --data "${data}" "${url}"
                curl "http://localhost:3030/v1/clearCache"
	else
		echo "Zip failed...exiting"
		exit -1
	fi
else
	echo "Mongo update failed...exiting."
	exit -1
fi

exit