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
Bgpio.DEBUG = true;
Bgpio.PIN_COUNT = 26;

Bgpio.codeArea = null;
Bgpio.consoleArea = null;

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
    debugInit : Bgpio.BrowserInterpreter.debugInit,
    debugStep : Bgpio.BrowserInterpreter.debugStep,
    run : Bgpio.BrowserInterpreter.run,
    stop : Bgpio.BrowserInterpreter.stop,
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
    Bgpio.runMode.selectMode(value ? 1 : 0);
    if (value) {
        Bgpio.API.connect();
    } else {
        Bgpio.API.disconnect();
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
