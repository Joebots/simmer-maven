/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @fileoverview Description.
 */
'use strict';

var Bgpio = Bgpio || {};
Bgpio.BoardInterpreter = {};

Bgpio.BoardInterpreter.startup = function() {
    Bgpio.WebSocket = io.connect();
    Bgpio.WebSocket.on('connect', function() {
        if (Bgpio.DEBUG)
            console.log("connected\n");
    });
    Bgpio.WebSocket.on('message', Bgpio.appendTextJsConsole);
    Bgpio.WebSocket.on('started', function() {
        Bgpio.notifyStarted(false);
    });
    Bgpio.WebSocket.on('stopped', function() {
        Bgpio.notifyStopped();
    });
}

Bgpio.BoardInterpreter.shutdown = function() {
    if (Bgpio.WebSocket != null) {
        Bgpio.WebSocket.close();
    }
}

Bgpio.BoardInterpreter.debugInit = function() {
    if (Bgpio.DEBUG)
        console.log('Init Board debug');
    alert('Feature not yet implemented.');
};

Bgpio.BoardInterpreter.debugStep = function() {
    if (Bgpio.DEBUG)
        console.log('Python debug step');
    alert('Feature not yet implemented.');
};

Bgpio.BoardInterpreter.run = function() {
    var code = Bgpio.getCode();
    if (Bgpio.DEBUG)
        console.log('Run code on the board: \n' + code);
    Bgpio.WebSocket.emit('run', code);
};

Bgpio.BoardInterpreter.stop = function() {
    if (Bgpio.DEBUG)
        console.log('Stop code on the board');
    Bgpio.WebSocket.emit('stop');
};
