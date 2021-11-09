BrowserInterpreter = {};

self.importScripts('acorn/acorn_interpreter.js');

BrowserInterpreter.DEBUG = true;
BrowserInterpreter.myInterpreter = null;
BrowserInterpreter.stepping = false;
BrowserInterpreter.hasCallbacks = false;
BrowserInterpreter.pauseProcess = false;
BrowserInterpreter.sleepTime = 0;

BrowserInterpreter.API = {
    _callbacks: {},
    reset: function () {
        BrowserInterpreter.API._callbacks = {};
        self.postMessage({method: 'reset', params: null})
    },
    gpioWrite: function (pin, value) {
        self.postMessage({method: 'gpioWrite', params: {pin: pin, value: value}})
    },
    servoWrite: function (pin, angle) {
        self.postMessage({method: 'servoWrite', params: {pin: pin, angle: angle}})
    },
    gpioRead: function (pin, callback) {
        var callbackId = "gpioRead_" + pin;
        BrowserInterpreter.API._callbacks[callbackId] = callback;
        self.postMessage({method: 'gpioRead', params: {pin: pin, callbackId: callbackId}})
    },
    gpioOn: function (pin, callback) {
        var callbackId = "gpioOn_" + pin;
        BrowserInterpreter.API._callbacks[callbackId] = callback;
        self.postMessage({method: 'gpioOn', params: {pin: pin, callbackId: callbackId}})
    },
    onI2CEvent: function (address, register, messageLength, callback) {
        var callbackId = "onI2CEvent_" + address + register + messageLength;
        BrowserInterpreter.API._callbacks[callbackId] = callback;
        self.postMessage({method: 'onI2CEvent', params: {address: address, register: register, messageLength: messageLength, callbackId: callbackId}})
    }
};

BrowserInterpreter.debugInit = function (code) {
    if (BrowserInterpreter.DEBUG)
        console.log("Init JavaScript debug");
    BrowserInterpreter.stepping = true;
    BrowserInterpreter.prepareNewRun();

    // Generate JavaScript code and parse it
    BrowserInterpreter.myInterpreter = new Interpreter(code, BrowserInterpreter.debugInterpreterInit);
    BrowserInterpreter.debugStep();
};

BrowserInterpreter.debugStep = function () {
    if (BrowserInterpreter.DEBUG)
        console.log("JavaScript debug step");
    var recursiveStep = function () {
        notifyStarted(true);
        try {
            var ok = BrowserInterpreter.myInterpreter.step();
            ok = ok || BrowserInterpreter.hasCallbacks;
        } finally {
            if (!ok) {
                // Program complete, no more code to execute.
                if (BrowserInterpreter.DEBUG)
                    console.log("Javascript Debug steps ended");
                notifyStopped();
                return;
            }
        }
        if (BrowserInterpreter.pauseProcess) {
            // A block has been highlighted. Pause execution here.
            notifyPaused();
            BrowserInterpreter.pauseProcess = false;
        } else {
            BrowserInterpreter.processId = setTimeout(recursiveStep, BrowserInterpreter.sleepTime);
            BrowserInterpreter.sleepTime = 0;
        }
    };
    recursiveStep();
};

BrowserInterpreter.run = function (code) {
    if (BrowserInterpreter.DEBUG)
        console.log("Running JavaScript simulation");
    BrowserInterpreter.stepping = false;
    notifyStarted(false);
    BrowserInterpreter.prepareNewRun();

    // Generate JavaScript code and parse it
    BrowserInterpreter.myInterpreter = new Interpreter(code, BrowserInterpreter.debugInterpreterInit);

    var recursiveStep = function () {
        var stop = false;
        try {
            var ok = BrowserInterpreter.myInterpreter.step();
            ok = ok || BrowserInterpreter.hasCallbacks;
        } finally {
            if (!ok)
                stop = true;
        }
        if (BrowserInterpreter.pauseProcess) {
            stop = true;
        } else {
            // Add the next step to the event loop to not freeze the browser
            BrowserInterpreter.processId = setTimeout(recursiveStep, BrowserInterpreter.sleepTime);
            BrowserInterpreter.sleepTime = 0;
        }
        if (stop) {
            clearTimeout(BrowserInterpreter.processId);
            BrowserInterpreter.API.reset();
            notifyStopped();
        }
    };
    recursiveStep();
};

BrowserInterpreter.stop = function () {
    if (BrowserInterpreter.DEBUG)
        console.log("Manually stopping running JavaScript");

    BrowserInterpreter.hasCallbacks = false
    BrowserInterpreter.pauseProcess = true;
    clearTimeout(BrowserInterpreter.processId);
    //setPinDefaults();
    BrowserInterpreter.API.reset();
    notifyStopped();
};

BrowserInterpreter.prepareNewRun = function () {
    BrowserInterpreter.pauseProcess = false;
    //setPinDefaults();
};



/*******************************************************************************
 * Below functions prepare the interpreter external API calls
 ******************************************************************************/
BrowserInterpreter.debugInterpreterInit = function (interpreter, scope) {
    // Add an API function for the alert() block.
    var wrapper = function (text) {
        text = text ? text.toString() : "";
        return interpreter.createPrimitive(alert(text));
    };
    interpreter.setProperty(scope, "alert", interpreter.createNativeFunction(wrapper));

    // Add an API function for the prompt() block.
    var wrapper = function (text) {
        text = text ? text.toString() : "";
        return interpreter.createPrimitive(prompt(text));
    };
    interpreter.setProperty(scope, "prompt", interpreter.createNativeFunction(wrapper));

    // Add an API function for highlighting blocks.
    var wrapper = function (id) {
        id = id ? id.toString() : "";
        return interpreter.createPrimitive(highlightBlock(id));
    };
    interpreter.setProperty(scope, "highlightBlock", interpreter.createNativeFunction(wrapper));

    // Add an API function for listening pin value changes
    var wrapper = function (pin, callback) {
        if (BrowserInterpreter.DEBUG)
            console.log("pin->" + pin + " on change " + callback);

        // A durty hack to add callback functions for Simmer
        var callbackNode = {
            type: "Program",
            body: callback.node.body.body
        };
        BrowserInterpreter.API.gpioOn(pin, function (result) {
            var valueCode = "var value = " + result + ";";
            interpreter.appendCode(valueCode);
            interpreter.appendCode(callbackNode);
        });
        BrowserInterpreter.hasCallbacks = true;
    };
    interpreter.setProperty(scope, "gpioOn", interpreter.createNativeFunction(wrapper));

    // Add an API function for write pin value
    var wrapper = function (pin, value) {
        if (BrowserInterpreter.DEBUG)
            console.log("pin->" + pin + " set " + value);

        BrowserInterpreter.API.gpioWrite(pin, value);
    };
    interpreter.setProperty(scope, "gpioWrite", interpreter.createNativeFunction(wrapper));

    // Add an API function for write pin value
    var wrapper = function (pin, value) {
        if (BrowserInterpreter.DEBUG)
            console.log("servo->" + pin + " set " + value);

        BrowserInterpreter.API.servoWrite(pin, value);
    };
    interpreter.setProperty(scope, "servoWrite", interpreter.createNativeFunction(wrapper));

    // Add an API function for read pin value
    var wrapper = function (pin, callback) {
        if (BrowserInterpreter.DEBUG)
            console.log("get pin->" + pin);

        BrowserInterpreter.API.gpioRead(pin, function (value) {
            callback(value);
        });
    };
    interpreter.setProperty(scope, "gpioRead", interpreter.createAsyncFunction(wrapper));

    // Add an API function for listening pin value changes
    var wrapper = function (address, register, messageLength, callback) {
        if (BrowserInterpreter.DEBUG)
            console.log(`address: ${address}, register: ${register}, messageLength: ${messageLength} on event ${callback}`);
        // A durty hack to add callback functions for Simmer
        var callbackNode = {
            type: "Program",
            body: callback.node.body.body
        };
        BrowserInterpreter.API.onI2CEvent(address, register, messageLength, function (result) {
            var valueCode = `var ${callback.node.params[0].name} = '${JSON.stringify(result)}';`;
            if (BrowserInterpreter.DEBUG) {
                console.log(`valueCode: ${valueCode}`);
                console.log(`callbackNode: ${callbackNode}`);
            }
            interpreter.appendCode(valueCode);
            interpreter.appendCode(callbackNode);
        });
        BrowserInterpreter.hasCallbacks = true;
    };
    interpreter.setProperty(scope, "onI2CEvent", interpreter.createNativeFunction(wrapper));

    // Add an API function for waiting an amount of time
    var wrapper = function (value) {
        if (BrowserInterpreter.DEBUG)
            console.log("wait for " + value + " ms");

        BrowserInterpreter.sleepTime = value;
    };
    interpreter.setProperty(scope, "sleep", interpreter.createNativeFunction(wrapper));

    // Add an API function for printing into the fake console
    var wrapper = function (text) {
        if (BrowserInterpreter.DEBUG)
            console.log("Print in fake console: " + text);

        appendTextJsConsole(text);
    };
    interpreter.setProperty(scope, "println", interpreter.createNativeFunction(wrapper));
};

function highlightBlock(id) {
    self.postMessage({method: 'highlightBlock', params: {id: id}})
    BrowserInterpreter.pauseProcess = true;
}

function notifyStarted(debug) {
    self.postMessage({method: 'notifyStarted', params: {debug: debug}})
}

function notifyStopped() {
    self.postMessage({method: 'notifyStopped', params: null})
}

function clearJsConsole() {
    self.postMessage({method: 'clearJsConsole', params: null})
}

function appendTextJsConsole(text) {
    self.postMessage({method: 'appendTextJsConsole', params: {text: text}})
}

self.onmessage = function (event) {
    var method = event.data.method;
    var params = event.data.params;
    if (!method) {
        return;
    }
    switch (method) {
        case 'debugInit':
            BrowserInterpreter.debugInit(params.code);
            break;
        case 'debugStep':
            BrowserInterpreter.debugStep();
            break;
        case 'run':
            BrowserInterpreter.run(params.code);
            break;
        case 'stop':
            BrowserInterpreter.stop();
            break;
        case 'callback':
            var callback = BrowserInterpreter.API._callbacks[params.callbackId];
            if (callback) {
                callback.apply(null, params.args)
            }
    }
};