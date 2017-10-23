/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @fileoverview Description.
 */
'use strict';

var Bgpio = Bgpio || {};
Bgpio.BrowserInterpreter = {};

Bgpio.BrowserInterpreter.myInterpreter = null;
Bgpio.BrowserInterpreter.stepping = false;
Bgpio.BrowserInterpreter.pauseProcess = false;
Bgpio.BrowserInterpreter.sleepTime = 0;

Bgpio.BrowserInterpreter.debugInit = function() {
    if (Bgpio.DEBUG)
        console.log('Init JavaScript debug');
    Bgpio.BrowserInterpreter.stepping = true;
    Bgpio.BrowserInterpreter.prepareNewRun();
    
    // Generate JavaScript code and parse it
    Bgpio.BrowserInterpreter.myInterpreter = new Interpreter(
            Bgpio.BrowserInterpreter.prepareJavaScript(),
            Bgpio.BrowserInterpreter.debugInterpreterInit);
    Bgpio.BrowserInterpreter.debugStep();
};

Bgpio.BrowserInterpreter.debugStep = function() {
    if (Bgpio.DEBUG)
        console.log('JavaScript debug step');
    var recursiveStep = function() {
        notifyStarted(true);
        try {
            var ok = Bgpio.BrowserInterpreter.myInterpreter.step();
        } finally {
            if (!ok) {
                // Program complete, no more code to execute.
                if (Bgpio.DEBUG)
                    console.log('Javascript Debug steps ended');
                notifySopped();
                return;
            }
        }
        if (Bgpio.BrowserInterpreter.pauseProcess) {
            // A block has been highlighted. Pause execution here.
            notifyPaused();
            Bgpio.BrowserInterpreter.pauseProcess = false;
        } else {
            Bgpio.BrowserInterpreter.processId = setTimeout(recursiveStep, Bgpio.BrowserInterpreter.sleepTime);
            Bgpio.BrowserInterpreter.sleepTime = 0;
        }
    };
    recursiveStep();
};

Bgpio.BrowserInterpreter.run = function() {
    if (Bgpio.DEBUG)
        console.log('Running JavaScript simulation');
    Bgpio.BrowserInterpreter.stepping = false;
    notifyStarted(false);
    Bgpio.BrowserInterpreter.prepareNewRun();
    
    // Generate JavaScript code and parse it
    Bgpio.BrowserInterpreter.myInterpreter = new Interpreter(
            Bgpio.BrowserInterpreter.prepareJavaScript(),
            Bgpio.BrowserInterpreter.debugInterpreterInit);
    
    var recursiveStep = function() {
        var stop = false;
        try {
            var ok = Bgpio.BrowserInterpreter.myInterpreter.step();
        } finally {
            if (!ok)
                stop = true;
        }
        if (Bgpio.BrowserInterpreter.pauseProcess) {
            stop = true;
        } else {
            // Add the next step to the event loop to not freeze the browser
            Bgpio.BrowserInterpreter.processId = setTimeout(recursiveStep, Bgpio.BrowserInterpreter.sleepTime);
            Bgpio.BrowserInterpreter.sleepTime = 0;
        }
        if (stop) {
            notifyStopped();
        }
    };
    recursiveStep();
};

Bgpio.BrowserInterpreter.stop = function() {
    if (Bgpio.DEBUG)
        console.log('Manually stopping running JavaScript');
    Bgpio.BrowserInterpreter.pauseProcess = true;
    clearTimeout(Bgpio.BrowserInterpreter.processId);
    notifyStopped();
};

Bgpio.BrowserInterpreter.prepareNewRun = function() {
    Bgpio.BrowserInterpreter.pauseProcess = false;
    Bgpio.workspace.traceOn(true);
    Bgpio.workspace.highlightBlock(null);
    Bgpio.setPinDefaults();
    Bgpio.clearJsConsole();
};

Bgpio.BrowserInterpreter.prepareJavaScript = function() {
    Blockly.JavaScript.addReservedWords('highlightBlock');
    Blockly.JavaScript.addReservedWords('setDiagramPin');
    Blockly.JavaScript.addReservedWords('delayMs');
    Blockly.JavaScript.addReservedWords('jsPrint');
    
    Blockly.JavaScript.INFINITE_LOOP_TRAP = null;
    if (Bgpio.BrowserInterpreter.stepping) {
        Blockly.JavaScript.STATEMENT_PREFIX = 'highlightBlock(%1);\n';
    } else {
        Blockly.JavaScript.STATEMENT_PREFIX = null;
    }
    var code = Bgpio.generateJavaScriptCode();
    if (Bgpio.DEBUG)
        console.log('About to execute code:\n' + code);
    return code;
};

/*******************************************************************************
 * Below functions prepare the interpreter external API calls
 ******************************************************************************/
Bgpio.BrowserInterpreter.debugInterpreterInit = function(interpreter, scope) {
    // Add an API function for the alert() block.
    var wrapper = function(text) {
        text = text ? text.toString() : '';
        return interpreter.createPrimitive(alert(text));
    };
    interpreter.setProperty(scope, 'alert', interpreter
            .createNativeFunction(wrapper));
    
    // Add an API function for the prompt() block.
    var wrapper = function(text) {
        text = text ? text.toString() : '';
        return interpreter.createPrimitive(prompt(text));
    };
    interpreter.setProperty(scope, 'prompt', interpreter
            .createNativeFunction(wrapper));
    
    // Add an API function for highlighting blocks.
    var wrapper = function(id) {
        id = id ? id.toString() : '';
        return interpreter.createPrimitive(highlightBlock(id));
    };
    interpreter.setProperty(scope, 'highlightBlock', interpreter
            .createNativeFunction(wrapper));
    
    // Add an API function for simulating pins
    var wrapper = function(pin, value) {
        if (Bgpio.DEBUG)
            console.log('pin->' + pin + ' set ' + value);
        Bgpio.setPinDigital(pin, value);
    };
    interpreter.setProperty(scope, 'setDiagramPin', interpreter
            .createNativeFunction(wrapper));
    
    // Add an API function for simulating pins
    var wrapper = function(pin, value) {
        if (Bgpio.DEBUG)
            console.log('get pin->' + pin);
        return Bgpio.getPinDigital(pin);
    };
    interpreter.setProperty(scope, 'getDiagramPin', interpreter
            .createNativeFunction(wrapper));
    
    // Add an API function for waiting an amount of time
    var wrapper = function(value) {
        if (Bgpio.DEBUG)
            console.log('wait for ' + value + ' ms');
        Bgpio.BrowserInterpreter.sleepTime = value;
    };
    interpreter.setProperty(scope, 'delayMs', interpreter
            .createNativeFunction(wrapper));
    
    // Add an API function for printing into the fake console
    var wrapper = function(text) {
        if (Bgpio.DEBUG)
            console.log('Print in fake console: ' + text);
        Bgpio.appendTextJsConsole(text);
    };
    interpreter.setProperty(scope, 'jsPrint', interpreter
            .createNativeFunction(wrapper));
};

Bgpio.BrowserInterpreter.parseAcornObject = function(acornObj) {
    return acornObj.isPrimitive ? acornObj.valueOf() : acornObj.toString();
};

function highlightBlock(id) {
    Bgpio.workspace.highlightBlock(id);
    Bgpio.BrowserInterpreter.pauseProcess = true;
}

function notifyStarted(debug) {
    if (Bgpio.eventBus) {
        Bgpio.eventBus
                .fireEvent(new com.joebotics.simmer.client.event.InterpreterStartedEvent(
                        debug))
    }
}

function notifyPaused() {
    if (Bgpio.eventBus) {
        Bgpio.eventBus
                .fireEvent(new com.joebotics.simmer.client.event.InterpreterPausedEvent())
    }
}

function notifyStopped() {
    Bgpio.workspace.highlightBlock(null);
    if (Bgpio.eventBus) {
        Bgpio.eventBus
                .fireEvent(new com.joebotics.simmer.client.event.InterpreterStoppedEvent())
    }
}
