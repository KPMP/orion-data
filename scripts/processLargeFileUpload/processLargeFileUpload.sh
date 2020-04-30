#!/bin/bash

currentDir=$(pwd)
echo "Package ID:"
read packageId

packageDir="/data/dataLake/package_$packageId"
transferStatus="NOT_STARTED"
mkdir $packageDir

cd ~/globusconnectpersonal-3.0.4/
globus login
taskId=`globus transfer --format unix --jmespath 'task_id' 936381c8-1653-11ea-b94a-0e16720bb42f:/PROD_INBOX/$packageId 86f02d04-8a2c-11ea-b3bb-0ae144191ee3:$packageDir --recursive`

while [[ "$transferStatus" != "SUCCEEDED" && "$transferStatus" != "FAILED" ]]
do
  transferStatus=`globus task show --format unix --jmespath 'status' $taskId`
done

if [ $transferStatus == "FAILED" ]
then
	echo "Transfer Failed. Globus task id was: $taskId"
	exit
fi

node $currentDir/updateMongo.js $packageId

exit
