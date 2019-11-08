[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6af5499a0365459e8f755ec19589534a)](https://www.codacy.com/manual/rlreamy/orion-data?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KPMP/orion-data&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/KPMP/orion-data.svg?branch=develop)](https://travis-ci.org/KPMP/orion-data)

# orion-data
  **Description**: Repo for the KPMP upload tool back-end

## RegenerateZipFiles
To regenerate zip files:
 1. Connect to mongo
 2. Set `regenerateZip` to true for any packages you want to regenerate zip files for
 3. Connect to server you need to regenerate zips for
 4. Navigate to heavens-docker/orion
 5. Bash into the spring container
 `docker exec -it spring bash`
 6. Rebuild the orion-data jar
 `./gradlew build -x test`
 7. Run the zip generator
 `java -cp build/libs/orion-data.jar -Dloader.main=org.kpmp.RegenerateZipFiles org.springframework.boot.loader.PropertiesLauncher`

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
