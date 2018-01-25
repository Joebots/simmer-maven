/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @fileoverview Description.
 */
'use strict';

var Bgpio = Bgpio || {};

Bgpio.workspace = null;
Bgpio.eventBus = null;
Bgpio.DEBUG = false;
Bgpio.PIN_COUNT = 26;

Bgpio.codeArea = null;
Bgpio.consoleArea = null;

Bgpio.BrowserInterpreter = new Worker("lib/worker_interpreter.js");
Bgpio.BrowserInterpreter.onmessage = function (event) {
    var method = event.data.method;
    var params = event.data.params;
    if (!method) {
        return;
    }
    switch (method) {
        case 'notifyStarted':
            Bgpio.notifyStarted(params.debug);
            break;
        case 'notifyStopped':
            Bgpio.notifyStopped();
            break;
        case 'clearJsConsole':
            Bgpio.clearJsConsole();
            break;
        case 'appendTextJsConsole':
            Bgpio.appendTextJsConsole(params.text);
            break;
        case 'highlightBlock':
            Bgpio.workspace.highlightBlock(params.id);
            break;
        case 'gpioWrite':
            Bgpio.API.gpioWrite(params.pin, params.value);
            break;
        case 'gpioRead':
            Bgpio.API.gpioRead(params.pin, function (value) {
                Bgpio.BrowserInterpreter.postMessage({method: 'callback', params: {callbackId: params.callbackId, args: [value]}});
            });
            break;
        case 'gpioOn':
            Bgpio.API.gpioOn(params.pin, function (value) {
                Bgpio.BrowserInterpreter.postMessage({method: 'callback', params: {callbackId: params.callbackId, args: [value]}});
            });
            break;
    }
};

Bgpio.API = Bgpio.SimmerAPI;

Bgpio.init = function(container, params) {
    Bgpio.workspace = Blockly.inject(container, params);
    Bgpio.workspace.addChangeListener(Bgpio.renderCode);
    
    Bgpio.clearJsConsole();
    return Bgpio.workspace;
};

Bgpio.setBlocks = function(xmlText) {
    var xml = Blockly.Xml.textToDom(xmlText);
    Blockly.Xml.domToWorkspace(xml, Bgpio.workspace);
};

Bgpio.getBlocks = function() {
    if (Bgpio.workspace) {
        var xml = Blockly.Xml.workspaceToDom(Bgpio.workspace);
        return Blockly.Xml.domToText(xml);
    }
    return null;
};

Bgpio.getBlocksCount = function() {
    if (Bgpio.workspace) {
        return Bgpio.workspace.getTopBlocks().length;
    }
    return 0;
};

Bgpio.clearBlocks = function() {
    if (Bgpio.workspace) {
        Bgpio.workspace.clear();
    }
};

Bgpio.setCodeArea = function(element) {
    Bgpio.codeArea = element;
};

Bgpio.setConsoleArea = function(element) {
    Bgpio.consoleArea = element;
};

Bgpio.resize = function() {
    Blockly.svgResize(Bgpio.workspace);
};

Bgpio.runMode = {
    selected : 0,
    types : [ 'Simulation', 'Execution' ],
    getSelectedMode : function() {
        return this.types[this.selected];
    },
    selectMode : function(id) {
        this.selected = id;
        this.updateState_();
    },
    selectNextMode : function() {
        this.selected++;
        if (this.selected >= this.types.length)
            this.selected = 0;
        this.updateState_();
    },
    debugInit : function() {
        Bgpio.workspace.traceOn(true);
        Bgpio.workspace.highlightBlock(null);
        Bgpio.clearJsConsole();
        Bgpio.BrowserInterpreter.postMessage({method: 'debugInit', params: {code: Bgpio.generateJavaScriptCode()}});
    },
    debugStep : function() {
        Bgpio.BrowserInterpreter.postMessage({method: 'debugStep', params: null});
    },
    run : function() {
        Bgpio.workspace.traceOn(true);
        Bgpio.workspace.highlightBlock(null);
        Bgpio.clearJsConsole();
        Bgpio.BrowserInterpreter.postMessage({method: 'run', params: {code: Bgpio.generateJavaScriptCode()}});
    },
    stop : function() {
        Bgpio.BrowserInterpreter.postMessage({method: 'stop', params: null});
    },
    API : Bgpio.SimmerAPI,
    updateState_ : function() {
        if (this.selected === 0) {
            Bgpio.API = Bgpio.SimmerAPI;
            Bgpio.clearJsConsole('Simulated print output.\n');
        } else {
            if (Bgpio.BoardAPI) {
                Bgpio.API = Bgpio.BoardAPI;
                Bgpio.clearJsConsole('Board print output.\n');
            } else {
                Bgpio.clearJsConsole('Board not found.\n');
            }
        }
    },
};

/*******************************************************************************
 * Blockly related
 ******************************************************************************/
Bgpio.prepareJavaScript = function () {
    Blockly.JavaScript.addReservedWords("highlightBlock");
    Blockly.JavaScript.addReservedWords("setDiagramPin");
    Blockly.JavaScript.addReservedWords("delayMs");
    Blockly.JavaScript.addReservedWords("jsPrint");

    Blockly.JavaScript.INFINITE_LOOP_TRAP = null;
    if (BrowserInterpreter.stepping) {
        Blockly.JavaScript.STATEMENT_PREFIX = "highlightBlock(%1);\n";
    } else {
        Blockly.JavaScript.STATEMENT_PREFIX = null;
    }
    var code = generateJavaScriptCode();
    if (BrowserInterpreter.DEBUG)
        console.log("About to execute code:\n" + code);
    return code;
};

Bgpio.generateJavaScriptCode = function() {
    return Blockly.JavaScript.workspaceToCode(Bgpio.workspace);
};

Bgpio.generatePythonCode = function() {
    return Blockly.Python.workspaceToCode(Bgpio.workspace);
};

Bgpio.generateXml = function() {
    var xmlDom = Blockly.Xml.workspaceToDom(Bgpio.workspace);
    var xmlText = Blockly.Xml.domToPrettyText(xmlDom);
    return xmlText;
};

Bgpio.getCode = function() {
    if (Bgpio.codeArea) {
        return Bgpio.codeArea.getText();
    }
    return null;
};

Bgpio.renderCode = function() {
    // Only regenerate the code if a block is not being dragged
    if (!Bgpio.codeArea || Bgpio.workspace.isDragging()) {
        return;
    }
    // Render Code with latest change highlight and syntax highlighting
    Bgpio.codeArea.clear();
    Bgpio.codeArea.setText(Bgpio.generateJavaScriptCode());
};

Bgpio.setPinDefaults = function() {
    console.log("Bgpio.setPinDefaults()");
    for (var i = 0; i < Bgpio.PIN_COUNT; i++) {
        Bgpio.API.gpioWrite(i + 1, false);
    }
};

/*******************************************************************************
 * Other
 ******************************************************************************/
Bgpio.getRaspPiIp = function() {
    /*
     * var ipField = document.getElementById('raspPiIp'); var ip =
     * ipField.value; if
     * (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
     * .test(ip)) { ipField.style.color = "green"; return ipField.value; }
     * ipField.style.color = "red";
     */
    // DEBUG return "localhost";
    return null;
};

Bgpio.hasBoard = function() {
    return typeof Bgpio.BoardAPI != "undefined";
}

Bgpio.setUseBoard = function(value) {
    if (value) {
        // Board mode
        Bgpio.runMode.selectMode(1);
        Bgpio.API.connect();
    } else {
        Bgpio.API.disconnect();
        // Simulator mode
        Bgpio.runMode.selectMode(0);
    }
}

Bgpio.getUseBoard = function() {
    return this.runMode.selected === 1;
}

Bgpio.setEventBus = function(eventBus) {
    this.eventBus = eventBus;
}

Bgpio.notifyStarted = function(debug) {
    if (this.eventBus) {
        this.eventBus
                .fireEvent(new com.joebotics.simmer.client.event.InterpreterStartedEvent(
                        debug))
    }
}

Bgpio.notifyPaused = function() {
    if (this.eventBus) {
        this.eventBus
                .fireEvent(new com.joebotics.simmer.client.event.InterpreterPausedEvent())
    }
}

Bgpio.notifyStopped = function() {
    this.workspace.highlightBlock(null);
    if (this.eventBus) {
        this.eventBus
                .fireEvent(new com.joebotics.simmer.client.event.InterpreterStoppedEvent())
    }
}

Bgpio.appendTextJsConsole = function(text) {
    if (Bgpio.consoleArea) {
        Bgpio.consoleArea.appendText(text + '\n');
    }
};

Bgpio.clearJsConsole = function(text) {
    if (Bgpio.consoleArea) {
        Bgpio.consoleArea.clear();
        if (text) {
            Bgpio.consoleArea.setText(text);
        }
    }
};
