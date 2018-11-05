'use strict';

function _writeFile(fileEntry, dataObj, path) {
    // Create a FileWriter object for our FileEntry (log.txt).
    fileEntry.createWriter(function (fileWriter) {
        fileWriter.onwriteend = function () {
            alert("Saved: " + path + fileEntry.name);
        };
        fileWriter.onerror = function (e) {
            alert("Error: " + e.toString());
        };
        fileWriter.write(dataObj);
    });
}

function _readFile(fileEntry) {

    fileEntry.file(function (file) {
        var reader = new FileReader();

        reader.onloadend = function () {
            console.log("Successful file read: " + this.result);
            _displayFileData(fileEntry.fullPath + ": " + this.result);
        };
        reader.readAsText(file);
    }, _onError);
}

function _displayFileData(data) {
    console.log(data);
}

function _onError(e) {
    alert("Error: " + e.toString());
}

var FileUtils = class FileUtils {
    constructor() {
    }

    download(uri, fileName) {
        if(window.cordova) {
            this._downloadInCordova(uri, fileName);
        } else {
            this._downloadInBrowser(uri, fileName);
        }
    }

    _downloadInCordova(uri, fileName) {
        window.requestFileSystem(window.PERSISTENT, 0, function (fs) {
            console.log('file system open: ' + fs.name);
            var path = cordova.file.externalRootDirectory + "Download/";
            window.resolveLocalFileSystemURL(path, function (dirEntry) {
                dirEntry.getFile(fileName, {create: true, exclusive: false}, function (fileEntry) {
                    var request = new XMLHttpRequest();
                    request.open("GET", uri, true);
                    request.responseType = "blob";
                    request.onload = function (event) {
                        var blob = request.response; // Note: not oReq.responseText
                        if (blob) {
                            _writeFile(fileEntry, blob, path);
                        } else {
                            _onError('we didnt get an XHR response!');
                        }
                    };
                    request.send(null);
                }, _onError);
            }, _onError);
        }, _onError);
    }

    _downloadInBrowser(uri, fileName) {
        var a = document.createElement('a');
        a.setAttribute("download", fileName);
        a.setAttribute("href", uri);
        a.click();
    }

    /*
     upload(fileURL, server) {
     window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function (fs) {
     console.log('file system open: ' + fs.name);
     fs.root.getFile('bot.png', { create: true, exclusive: false }, function (fileEntry) {
     fileEntry.file(function (file) {
     var reader = new FileReader();
     reader.onloadend = function() {
     // Create a blob based on the FileReader "result", which we asked to be retrieved as an ArrayBuffer
     var blob = new Blob([new Uint8Array(this.result)], { type: "image/png" });
     var oReq = new XMLHttpRequest();
     oReq.open("POST", "http://mysweeturl.com/upload_handler", true);
     oReq.onload = function (oEvent) {
     // all done!
     };
     // Pass the blob in to XHR's send method
     oReq.send(blob);
     };
     // Read the file as an ArrayBuffer
     reader.readAsArrayBuffer(file);
     }, function (err) { console.error('error getting fileentry file!' + err); });
     }, function (err) { console.error('error getting file! ' + err); });
     }, function (err) { console.error('error getting persistent fs! ' + err); });
     }
     */
};