[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6af5499a0365459e8f755ec19589534a)](https://www.codacy.com/manual/rlreamy/orion-data?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KPMP/orion-data&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/KPMP/orion-data.svg?branch=develop)](https://travis-ci.org/KPMP/orion-data)

# orion-data
  **Description**: Repo for the KPMP upload tool back-end

## Documentation
Visit [kpmp.github.io/dlu](http://kpmp.io.github.io/dlu)

## Removing packages
1. Connect to Mongo by opening ssh session to prod-upload
- `ssh <username>@172.20.66.165 -L 27017:localhost:27017`
2. Within mongo, delete package from package and file collection
3. Delete package off of the DLU
4. sudo rm /nfs/corenfs/kpmp-data/prod/dataLake/package_<package_id>
5. Navigate to clearCache URL to clear the old cache
- `https://upload.kpmp.org/api/v1/clearCache`
6. Navigate to upload and confirm packages no longer exist

## Getting and storing the credentials for Google Drive
 1. Get the credentials.json file from kpmp-secure/orion-data and put it in the `src/main/resources` directory.
 2. Get the StoredCredential file from kpmp-secure/orion-data and put it in the `tokens` directory (create if it doesn't  exist).
 3. Restart the spring container.

## Creating new credentials files for Google Drive
NOTE: You need to create a new credentials file if the permissions change
 1. Delete (if necessary) the old `tokens` directory.
 2. Bring the application down and then back up.
 3. Spring will generate a URL and print it to stdout, grab it and open it in a browser.
 4. Authenticate as kpmp-datalake@umich.edu and grant the requested access.
 5. The redirect to localhost will probably fail, since you're not running the app locally. Copy this URL.
 6. Go into the spring container and do a wget on the pasted URL. This will create the credentials file and the app will start running.

## Creating new credentials files for Globus
 1. Delete (if necessary) the `StoredCredential` file in the `globus_tokens` directory.
 2. Bring the application down and then back up.
 3. Spring will generate a URL and print it to stdout (you may have to look in Kibana), grab it and open it in a browser.
 4. Authenticate.
 5. The redirect to localhost will probably fail, since you're not running the app locally. Copy this URL.
 6. Go into the spring container and do a wget on the pasted URL. This will create the credentials file and the app will start running.

# Build
`./gradlew build docker`
The default tag is the github branch name if no verison is provided
To pass a version when building the docker image execute
`./gradlew build docker -Ptag=<tagNumber>`