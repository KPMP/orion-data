# The Service Layer for the MiKTMC Uploader

# Requires
* Java 21

# Build
`./gradlew build docker`
The default tag is the github branch name if no verison is provided
To pass a version when building the docker image execute
`./gradlew build docker -Ptag=<tagNumber>`

## ImageMagick required for build
### To install on a mac
brew update && brew upgrade && brew install imagemagick
