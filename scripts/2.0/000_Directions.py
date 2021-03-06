#!/usr/bin/python

print ""
print ""
print "These scripts are to do the migration of data from v1.75 to v2.0"
print "We are migrating from MySql to Mongo, and also changing the directory names where the data are stored."
print ""
print "Directions:"
print ""
print "With a clean dataLake database in mongo"
print ""
print "1) Generate a file containing the unique set of users from the metadata files on disk"
print "  cd ~/apps/orion-data/scripts/2.0"
print "  ./010_migrateUsers.py"
print ""
print "2) Load users into mongo"
print "  cd ~/orionstack"
print "  docker cp /data/combinedUserData.json mongodb:."
print "  docker exec -it mongodb bash"
print "  mongoimport --db dataLake --collection users --file combinedUserData.json"
print ""
print "3) Export user info from mongo so we can get the ids for the users"
print "  Inside mongodb container:  mongoexport --db dataLake --collection users > exportedUsers.json"
print "  exit the container"
print "  docker cp mongodb:/exportedUsers.json /data/."
print ""
print "4) Generate the package metadata"
print "  cd ~/apps/orion-data/scripts/2.0"
print "  ./020_migratePackageMetadata.py"
print ""
print "5) Load package metadata into mongo"
print "  cd ~/orionstack"
print "  docker cp /data/combinedcombinedMetadata.json mongodb:."
print "  docker exec -it mongodb bash"
print "  mongoimport --db dataLake --collection packages --file combinedMetadata.json"
print ""
print "6) Migrate directories"
print "  cd ~/apps/orion-data/scripts/2.0"
print "  ./030_migrateDirectories.py"
print ""
print "7) Regenerate zip files"
print "  cd ~/orionstack"
print "  docker exec -it spring bash"
print "  ./gradlew build"
print "  java -cp build/libs/orion-data.jar -Dloader.main=org.kpmp.RegenerateZipFiles org.springframework.boot.loader.PropertiesLauncher"
print ""


