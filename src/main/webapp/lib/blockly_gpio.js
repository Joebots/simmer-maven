/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * @fileoverview Description.
 */
'use strict';

class RunMode{
    selected = 0
    types = [ "Simulation", "Execution" ]
    blocklyGpio

    constructor(blocklyGpio) {
        this.blocklyGpio = blocklyGpio
    }

    getSelectedMode() {
        return this.types[this.selected];
    }
    selectMode(id) {
        this.selected = id;
        this.updateState_();
    }
    selectNextMode() {
        this.selected++;
        if (this.selected >= this.types.length)
            this.selected = 0;
        this.updateState_();
    }
    debugInit() {
        this.blocklyGpio.workspace.traceOn(true);
        this.blocklyGpio.workspace.highlightBlock(null);
        this.blocklyGpio.clearJsConsole();
        this.blocklyGpio.BrowserInterpreter.postMessage({method: 'debugInit', params: {code: this.blocklyGpio.generateJavaScriptCode()}});
    }
    debugStep() {
        this.blocklyGpio.BrowserInterpreter.postMessage({method: 'debugStep', params: null});
    }
    run() {
        this.blocklyGpio.workspace.traceOn(true);
        this.blocklyGpio.workspace.highlightBlock(null);
        this.blocklyGpio.clearJsConsole();
        let renderedCode = this.blocklyGpio.generateJavaScriptCode()
        console.log("run()", renderedCode)
        this.blocklyGpio.BrowserInterpreter.postMessage({method: 'run', params: {code: renderedCode}});
    }
    stop() {
        this.blocklyGpio.BrowserInterpreter.postMessage({method: 'stop', params: null});
    }
    updateState_() {
        if (this.selected === 0) {
            this.blocklyGpio.API = new SimmerAPI();
            this.blocklyGpio.clearJsConsole('Simulated print output.\n');
        } else {
            if (this.blocklyGpio.BoardAPI) {
                this.blocklyGpio.API = BlocklyGPIO.BoardAPI;
                this.blocklyGpio.clearJsConsole('Board print output.\n');
            } else {
                this.blocklyGpio.clearJsConsole('Board not found.\n');
            }
        }
    }
};

// singleton
class BlocklyGPIO{

    static instance

    workspace = null;
    eventBus = null;
    DEBUG = false;

    PIN_COUNT = 26;
    codeArea = null;

    consoleArea = null;
    API = SimmerAPI

    BrowserInterpreter = new Worker("lib/worker_interpreter.js");
    runMode = undefined

    constructor() {
        if(!BlocklyGPIO.instance){

            this.runMode = new RunMode(this)
            BlocklyGPIO.instance = this
            this.API = new SimmerAPI()
            console.log("BlocklyGPIO.constructor", this);

            this.BrowserInterpreter.onmessage = function (event) {
                var method = event.data.method;
                var params = event.data.params;
                if (!method) {
                    return;
                }
                switch (method) {
                    case 'notifyStarted':
                        BlocklyGPIO.instance.notifyStarted(params.debug);
                        break;
                    case 'notifyStopped':
                        BlocklyGPIO.instance.notifyStopped();
                        break;
                    case 'clearJsConsole':
                        BlocklyGPIO.instance.clearJsConsole();
                        break;
                    case 'appendTextJsConsole':
                        BlocklyGPIO.instance.appendTextJsConsole(params.text);
                        break;
                    case 'highlightBlock':
                        BlocklyGPIO.instance.workspace.highlightBlock(params.id);
                        break;
                    case 'gpioWrite':
                        BlocklyGPIO.instance.API.gpioWrite(params.pin, params.value);
                        break;
                    case 'servoWrite':
                        BlocklyGPIO.instance.API.servoWrite(params.pin, params.angle);
                        break;
                    case 'gpioRead':
                        BlocklyGPIO.instance.API.gpioRead(params.pin, function (value) {
                            BrowserInterpreter.postMessage({method: 'callback', params: {callbackId: params.callbackId, args: [value]}});
                        });
                        break;
                    case 'gpioOn':
                        BlocklyGPIO.instance.API.gpioOn(params.pin, function (value) {
                            BrowserInterpreter.postMessage({method: 'callback', params: {callbackId: params.callbackId, args: [value]}});
                        });
                        break;
                    case 'onI2CEvent':
                        BlocklyGPIO.instance.API.onI2CEvent(params.address, params.register, params.messageLength, function (value) {
                            BrowserInterpreter.postMessage({method: 'callback', params: {callbackId: params.callbackId, args: [value]}});
                        });
                        break;
                }
            };
            this.API = new SimmerAPI()
        }

        return BlocklyGPIO.instance
    }

    initWorkspace(container, params) {
        console.log("BlocklyGPIO.initWorkspace", container, params, this);

        this.workspace = Blockly.inject(container, params);
        this.workspace.addChangeListener(this.renderCode);
        this.clearJsConsole();
        return this.workspace;
    };

    setBlocks(xmlText) {
        var xml = Blockly.Xml.textToDom(xmlText);
        Blockly.Xml.domToWorkspace(xml, Bgpio.workspace);
    };

    getBlocks() {
        console.log("BlocklyGPIO.getBlocks", this);

        if (this.workspace) {
            var xml = Blockly.Xml.workspaceToDom(this.workspace);
            return Blockly.Xml.domToText(xml);
        }
        return null;
    };

    getBlocksCount() {
        console.log("BlocklyGPIO.getBlocksCount", this);

        if (this.workspace) {
            return this.workspace.getTopBlocks().length;
        }
        return 0;
    };

    clearBlocks() {
        console.log("BlocklyGPIO.clearBlocks", this);

        if (this.workspace) {
            this.workspace.clear();
        }
    };

    setCodeArea(element) {
        console.log("BlocklyGPIO.setCodeArea", this);
        this.codeArea = element;
    };

    setConsoleArea(element) {
        console.log("BlocklyGPIO.setConsoleArea", element, this);
        this.consoleArea = element;
    };

    resize() {
        console.log("BlocklyGPIO.resize", this);
        Blockly.svgResize(this.workspace);
    };

    prepareJavaScript() {
        console.log("BlocklyGPIO.prepareJavaScript", this);

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

    generateJavaScriptCode() {
        console.log("BlocklyGPIO.generateJavaScriptCode", this);

        return Blockly.JavaScript.workspaceToCode(this.workspace);
    };

    generatePythonCode() {
        console.log("BlocklyGPIO.generateXml", this);

        return Blockly.Python.workspaceToCode(this.workspace);
    };

    generateXml() {
        console.log("BlocklyGPIO.generateXml", this);

        var xmlDom = Blockly.Xml.workspaceToDom(this.workspace);
        var xmlText = Blockly.Xml.domToPrettyText(xmlDom);
        return xmlText;
    };

    getCode() {

        console.log("BlocklyGPIO.getCode", this);

/*        if (this.codeArea) {
            return this.codeArea.getText();
        }
        return null;*/
    };

    renderCode() {

        var me = this || new BlocklyGPIO()
        console.log("BlocklyGPIO.renderCode", me)

        // Render Code with latest change highlight and syntax highlighting
        setTimeout(()=>{
            console.log("me.codeArea", me.codeArea)

            // Only regenerate the code if a block is not being dragged
            if (!me.codeArea || !me.workspace || me.workspace.isDragging()) {
                return;
            }

            // me.codeArea.setText("");
            // me.codeArea.setText(me.generateJavaScriptCode());

        }, 100)
    };

    setPinDefaults() {
        console.log("BlocklyGPIO.setPinDefaults", this);

        for (var i = 0; i < PIN_COUNT; i++) {
            this.API.gpioWrite(i + 1, false);
        }
    };

    /*******************************************************************************
     * Other
     ******************************************************************************/
    getRaspPiIp() {
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

    hasBoard() {
        console.log("BlocklyGPIO.hasBoard", this);

        return typeof BoardAPI != "undefined";
    }

    setUseBoard(value) {
        console.log("BlocklyGPIO.setUseBoard", this);

        if (value) {
            // Board mode
            this.runMode.selectMode(1);
            this.API.connect();
        } else {
            this.API.disconnect();
            // Simulator mode
            this.runMode.selectMode(0);
        }
    }

    getUseBoard() {
        console.log("BlocklyGPIO.getUseBoard", this);
        return this.runMode.selected === 1;
    }

    setEventBus(eventBus) {
        console.log("BlocklyGPIO.setEventBus", this);
        this.eventBus = eventBus;
    }

    notifyStarted(debug) {
        console.log("BlocklyGPIO.notifyStarted", this);

        if (this.eventBus) {
            // this.eventBus.fireEvent(new com.joebotics.simmer.client.event.InterpreterStartedEvent(debug))
        }
    }

    notifyPaused() {
        console.log("BlocklyGPIO.notifyPaused", this);

        if (this.eventBus) {
            // this.eventBus.fireEvent(new com.joebotics.simmer.client.event.InterpreterPausedEvent())
        }
    }

    notifyStopped() {
        console.log("BlocklyGPIO.notifyStopped", this);

        this.workspace.highlightBlock(null);
        if (this.eventBus) {
            // this.eventBus.fireEvent(new com.joebotics.simmer.client.event.InterpreterStoppedEvent())
        }
    }

    appendTextJsConsole(text) {
        console.log("BlocklyGPIO.appendTextJsConsole", this);

        /*        if (this.consoleArea) {
                    this.consoleArea.appendText(text + '\n');
                }*/
    };

    clearJsConsole(text) {
        console.log("BlocklyGPIO.clearJsConsole", this);

        /*        if (this.consoleArea) {
                    this.consoleArea.setText("");
                    if (text) {
                        consoleArea.setText(text);
                    }
                }*/
    };
};

// new BlocklyGPIO();
var Bgpio = Bgpio || new BlocklyGPIO();
