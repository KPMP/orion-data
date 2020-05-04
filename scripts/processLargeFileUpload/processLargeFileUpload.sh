#!/bin/bash

currentDir=$(pwd)

source ${currentDir}/.env
echo ${GLOBUS_DROPBOX_ENDPOINT}
echo "**** It would be wise to start me in Screen ***"
echo "Package ID:"
read packageId

packageDir="/data/dataLake/package_$packageId"
transferStatus="NOT_STARTED"
mkdir "${packageDir}"

cd ~/globusconnectpersonal-3.0.4/
globus login
taskId=$(globus transfer --format unix --jmespath 'task_id' ${GLOBUS_DROPBOX_ENDPOINT}:${GLOBUS_DROPBOX_PATH}${packageId} ${LOCAL_ENDPOINT}:"${packageDir}" --recursive)

while [[ "$transferStatus" != "SUCCEEDED" && "$transferStatus" != "FAILED" ]]
do
  transferStatus=$(globus task show --format unix --jmespath 'status' "${taskId}")
done

if [ "$transferStatus" == "FAILED" ]
then
	echo "Transfer Failed. Globus task id was: $taskId"
	exit
fi

cd "${currentDir}"

node updateMongo.js "${packageId}"

result=$?

if [ $result == 0 ]; then
	java -cp /home/pathadmin/apps/orion-data/build/libs/orion-data.jar -Dloader.main=org.kpmp.RegenerateZipFiles org.springframework.boot.loader.PropertiesLauncher
	zipResult=$?
	if [ $zipResult == 0 ]; then
		data='{"packageId":"'
		data+=$packageId
		data+='", "state":"UPLOAD_SUCCEEDED", "largeUploadChecked":true}'
		url="http://localhost:3060/v1/state/host/upload_kpmp_org"
		curl --header "Content-Type: application/json" --request POST --data "${data}" "${url}"
	else
		echo "Zip failed...exiting"
		exit -1
	fi
else
	echo "Mongo update failed...exiting."
	exit -1
fi



exit
