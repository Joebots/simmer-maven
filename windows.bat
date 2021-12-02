@ECHO OFF
ECHO Building APK.
mvn clean install
CD ./simmer-cordova
SET location=%cd%
ECHO Generating apk from %location%
cordova build --release android
ECHO Building APK Successfully Done!!
PAUSE
