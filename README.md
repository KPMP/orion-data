[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6af5499a0365459e8f755ec19589534a)](https://www.codacy.com/manual/rlreamy/orion-data?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KPMP/orion-data&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/KPMP/orion-data.svg?branch=develop)](https://travis-ci.org/KPMP/orion-data)

# orion-data
  **Description**: Repo for the KPMP upload tool back-end

## Documentation
Visit [kpmp.github.io/dlu](https://kpmp.github.io/dlu/index.html)

## Removing packages
1. Connect to Mongo by opening ssh session to prod-upload
- `ssh <username>@172.20.66.165 -L 27017:localhost:27017`
2. Within mongo, delete package from package and file collection
3. Delete package off of the DLU
4. sudo rm /nfs/corenfs/kpmp-data/prod/dataLake/package_<package_id>
5. Navigate to clearCache URL to clear the old cache
- `https://upload.kpmp.org/api/v1/clearCache`
6. Navigate to upload and confirm packages no longer exist

## Globus application registration
This application is registered in Globus here -- [https://app.globus.org/](https://app.globus.org/settings/developers) -- in the "KPMP Data Lake" project as "KPMP Data Lake Uploader". You need to grant the application's userID write access to the Guest Collection being used as the Data Lake INBOX. 

## Creating new credentials files for Globus
 1. Delete (if necessary) the `StoredCredential` file in the `globus_tokens` directory.
 2. Bring the application down and then back up.
 3. Spring will generate a URL and print it to stdout (you may have to look in Kibana), grab it and open it in a browser.
 4. Authenticate.
 5. The redirect to localhost will probably fail, since you're not running the app locally. Copy this URL.
 6. Go into the spring container and do a wget on the pasted URL. This will create the credentials file and the app will start running. NOTE: if "localhost" doesn't work for this URL, try "127.0.0.1".

## Using REDCap Endpoint
1. Get your token: https://upload.kpmp.org/api/v1/token
2. POST REDCap data (in JSON format) to https://upload.kpmp.org:3030/v1/redcap?token=[token string]
3. Check for success response

# Build
`./gradlew build docker`
The default tag is the github branch name if no verison is provided
To pass a version when building the docker image execute
`./gradlew build docker -Ptag=<tagNumber>`
