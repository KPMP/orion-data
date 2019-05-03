# orion-data

[![Build Status](https://travis-ci.org/KPMP/orion-data.svg?branch=develop)](https://travis-ci.org/KPMP/orion-data)
**Description**: Repo for the KPMP upload tool back-end

## GenerateUploadReport
To generate an upload report (before production release):
1. Check out the latest from orion-data
2. Connect to prod mongo
 `ssh uploader-prod -L 27017:localhost:27017`
3. Change src/main/resources/application.properties spring.data.mongodb.uri to `spring.data.mongodb.uri=mongodb://localhost:27017/dataLake`
4. Rebuild the orion-data jar
`./gradlew build`
5. Run the report generator
`java -cp build/libs/orion-data.jar -Dloader.main=org.kpmp.GenerateUploadReport org.springframework.boot.loader.PropertiesLauncher`

This will generate a report.csv file in your current directory

**Remember to revert your application.properties file before checking it in!!**
Make sure you do not check in the report.csv

## RegenerateZipFiles
To regenerate zip files:
1. Connect to mongo
2. Set `regenerateZip` to true for any packages you want to regenerate zip files for
3. Connect to server you need to regenerate zips for
4. Navigate to heavens-docker/orion
5. Bash into the spring container
`docker exec -it spring bash`
6. Rebuild the orion-data jar
`./gradlew build`
7. Run the zip generator
`java -cp build/libs/orion-data.jar -Dloader.main=org.kpmp.RegenerateZipFiles org.springframework.boot.loader.PropertiesLauncher`

