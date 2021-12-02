#!/bin/bash
echo Building APK.
mvn clean install
cd ./simmer-cordova
working_dir=$(pwd)
echo "Generating apk from ${working_dir}"
cordova build --release android
echo Building APK Successfully Done!!
pause