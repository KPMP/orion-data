#!/bin/bash

source "${currentDir}"/.env

echo "**** It would be wise to start me in Screen ***"

echo "Package ID:"
read packageId

packageDir="/data/dataLake/package_$packageId"
rm "$packageDir/metadata.json"

cp /globus/PROD_INBOX/"${packageId}"/* "${packageDir}"

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
