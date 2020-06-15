#!/bin/bash
echo "Package ID:"
read packageId

data='{"packageId":"'
data+=$packageId
data+='", "state":"UPLOAD_SUCCEEDED", "largeUploadChecked":true}'
url="http://localhost:3060/v1/state/host/upload_kpmp_org"
curl --header "Content-Type: application/json" --request POST --data "${data}" "${url}"
