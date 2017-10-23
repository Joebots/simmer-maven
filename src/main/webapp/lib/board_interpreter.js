/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * @fileoverview Description.
 */
'use strict';

var Bgpio = Bgpio || {};
Bgpio.BoardInterpreter = {};

Bgpio.BoardInterpreter.debugInit = function() {
  if (Bgpio.DEBUG) console.log('Init Board debug');
  alert('Feature not yet implemented.');
};

Bgpio.BoardInterpreter.debugStep = function() {
  if (Bgpio.DEBUG) console.log('Python debug step');
  alert('Feature not yet implemented.');
};

Bgpio.BoardInterpreter.run = function() {
  var code = Bgpio.getCode();
  if (Bgpio.DEBUG) console.log('Run Python code: \n' + code);
  Bgpio.WebSocket.connect(Bgpio.getRaspPiIp());
  Bgpio.WebSocket.sendCode(code);
};

Bgpio.BoardInterpreter.stop = function() {
  if (Bgpio.DEBUG) console.log('Stop running Python code');
  alert('Feature not yet implemented.');
};
