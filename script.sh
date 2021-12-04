#!/bin/bash
echo Building APK.
mvn clean install
cd ./simmer-cordova
working_dir=$(pwd)
echo "Generating apk from ${working_dir}"
cordova platform add ios
cordova platform add android
cordova build --release
echo Building APK Successfully Done!!
pause