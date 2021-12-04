@ECHO OFF
ECHO Building APK.
mvn clean install
CD ./simmer-cordova
SET location=%cd%
ECHO Generating apk from %location%
cordova platform add android
cordova build
cordova build --release
ECHO Building APK Successfully Done!!
PAUSE
