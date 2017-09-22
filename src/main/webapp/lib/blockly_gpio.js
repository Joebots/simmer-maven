/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * @fileoverview Description.
 */
'use strict';

var Bgpio = Bgpio || {};

Bgpio.workspace = null;
Bgpio.DEBUG = true;
Bgpio.PIN_COUNT = 26;

Bgpio.codePanel = null;
Bgpio.jsConsole = null;

Bgpio.init = function(container, params) {
    Bgpio.workspace = Blockly.inject(container, params);
    Bgpio.workspace.addChangeListener(Bgpio.renderCode);
    
    Bgpio.clearJsConsole();
    Bgpio.WebSocket.init();
    return Bgpio.workspace;
};

Bgpio.setBlocks = function(blocks) {
    Blockly.Xml.domToWorkspace(blocks, Bgpio.workspace);
}

Bgpio.resize = function() {
    Blockly.svgResize(Bgpio.workspace);
}

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
    debugInit : Bgpio.JsInterpreter.debugInit,
    debugStep : Bgpio.JsInterpreter.debugStep,
    run : Bgpio.JsInterpreter.run,
    stop : Bgpio.JsInterpreter.stop,
    updateState_ : function() {
        for (var i = 0; i < this.types.length; i++) {
            var modeText = document.getElementById('mode' + this.types[i]);
            if (i === this.selected) {
                modeText.style.display = 'inline';
            } else {
                modeText.style.display = 'none';
            }
        }
        var simulationContent = document.getElementById('simulationContentDiv');
        var executionContent = document.getElementById('executionContentDiv');
        if (this.selected === 0) {
            simulationContent.style.display = 'block';
            executionContent.style.display = 'none';
            this.debugInit = Bgpio.JsInterpreter.debugInit;
            this.debugStep = Bgpio.JsInterpreter.debugStep;
            this.run = Bgpio.JsInterpreter.run;
            this.stop = Bgpio.JsInterpreter.stop;
        } else {
            simulationContent.style.display = 'none';
            executionContent.style.display = 'block';
            this.debugInit = Bgpio.PythonInterpreter.debugInit;
            this.debugStep = Bgpio.PythonInterpreter.debugStep;
            this.run = Bgpio.PythonInterpreter.run;
            this.stop = Bgpio.PythonInterpreter.stop;
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

Bgpio.renderCode = function() {
    // Only regenerate the code if a block is not being dragged
    if (!Bgpio.codePanel || Blockly.dragMode_ != 0) {
        return;
    }
    // Render Code with latest change highlight and syntax highlighting
    Bgpio.codePanel.textContent = Bgpio.generateJavaScriptCode();
    Bgpio.codePanel.innerHTML = prettyPrintOne(pyPre.innerHTML, 'js', false);
};

/*******************************************************************************
 * Right content related
 ******************************************************************************/
Bgpio.setPinDefaults = function() {
    console.log("Bgpio.setPinDefaults()");
    //for (var i = 1; i <= Bgpio.PIN_COUNT; i++) {
    //    document.getElementById('pin' + i).className = 'pinDefault';
    //}
};

Bgpio.setPinDigital = function(pinNumber, isPinHigh) {
    console.log("Bgpio.setPinDigital(" + pinNumber + ", " + isPinHigh + ")");
    //var pin = document.getElementById('pin' + pinNumber);
    //pin.className = isPinHigh ? 'pinDigitalHigh' : 'pinDigitalLow';
};

Bgpio.appendTextJsConsole = function(text) {
    if (Bgpio.jsConsole) {
        Bgpio.jsConsole.textContent += text + '\n';
    }
};

Bgpio.clearJsConsole = function(text) {
    if (Bgpio.jsConsole) {
        Bgpio.jsConsole.textContent = 'Simulated print output.\n';
    }
};

/*******************************************************************************
 * Other
 ******************************************************************************/
Bgpio.getRaspPiIp = function() {
    var ipField = document.getElementById('raspPiIp');
    var ip = ipField.value;
    if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
            .test(ip)) {
        ipField.style.color = "green";
        return ipField.value;
    }
    ipField.style.color = "red";
    return null;
};
