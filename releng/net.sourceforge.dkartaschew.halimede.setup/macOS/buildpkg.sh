#!/bin/bash

_JRE="OpenJDK11U-jre_x64_mac_hotspot_11.0.10_9.tar.gz"
_URL="https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.10%2B9/OpenJDK11U-jre_x64_mac_hotspot_11.0.10_9.tar.gz"
_JRE_FOLDER="jdk-11.0.10+9-jre"
_BUILD=`date '+%Y%m%d'`

# Clean up
rm -fR "Halimede CA.app"

# Get JRE
if [ ! -f "${_JRE}" ]; then
    wget ${_URL}
fi

# Extract the zip
unzip "Halimede CA-macosx.cocoa.x86_64.zip"
tar -xzf ${_JRE}
mv ${_JRE_FOLDER} jre
mv jre "Halimede CA.app/"

# Code sign application
# TODO:

/usr/local/bin/packagesbuild -v --package-version "1.1.0.$_BUILD" "Halimede CA.pkgproj"

# Code sign installation package
# TODO:

mv "build/Halimede CA.pkg" ./

# Clean up
rm -fR "Halimede CA.app"
rm -fR build
