"use strict";

/** Common HSV hue for all blocks in this file. */
var GPIO_HUE = 250;

var RASPBERRY_PI3_PINS = {
    "2" : "3",
    "3" : "5",
    "4" : "7",
    "5" : "29",
    "6" : "31",
    "7" : "26",
    "8" : "24",
    "9" : "21",
    "10" : "19",
    "11" : "23",
    "12" : "32",
    "13" : "33",
    "14" : "8",
    "15" : "10",
    "16" : "36",
    "17" : "11",
    "18" : "12",
    "19" : "35",
    "20" : "38",
    "21" : "40",
    "22" : "15",
    "23" : "16",
    "24" : "18",
    "25" : "22",
    "26" : "37",
    "27" : "13"
};

var ODROID_C2_PINS = {
    "0" : "11",
    "1" : "12",
    "2" : "13",
    "3" : "15",
    "4" : "16",
    "5" : "18",
    "6" : "22",
    "7" : "7",
    "10" : "24",
    "11" : "26",
    "12" : "19",
    "13" : "21",
    "14" : "23",
    "21" : "29",
    "22" : "31",
    "24" : "35",
    "26" : "32",
    "27" : "36"
};

var PINS = GpioConfig || ODROID_C2_PINS;

Blockly.Blocks["pin_changed"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendDummyInput()
            .appendField("when GPIO")
            .appendField(new Blockly.FieldDropdown(arr), "PIN")
            .appendField("changed")
        this.appendStatementInput("DO")
            .appendField("do");
        this.setTooltip("");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript["pin_changed"] = function(block) {
    var pin = block.getFieldValue("PIN");
    var callback = Blockly.JavaScript.statementToCode(block, "DO");
    var code = "gpioOn('change', " + pin + ", function(pin, value) {\n" +
    "    if (pin != " + pin + ") {\n" +
    "        return;\n" +
    "    }\n" +
    "    " + callback + "\n" +
    "});"
    return code;
};

Blockly.Blocks["pin_write"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendValueInput("STATE", "pin_value")
            .appendField("set GPIO")
            .appendField(new Blockly.FieldDropdown(arr), "PIN")
            .appendField("to").setCheck("pin_value");
        this.setInputsInline(false);
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
        this.setTooltip("");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript["pin_write"] = function(block) {
    var pin = block.getFieldValue("PIN");
    var state = Blockly.JavaScript.valueToCode(block, "STATE",
            Blockly.JavaScript.ORDER_ATOMIC)
            || "0";
    state = (state === "HIGH") ? "true" : "false";
    var code = "gpioWrite(" + PINS[pin] + ", " + state + ");\n";
    return code;
};

Blockly.Blocks["pin_read"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendDummyInput().appendField("get GPIO").appendField(
                new Blockly.FieldDropdown(arr), "PIN")
        this.setOutput(true, "pin_value");
        this.setTooltip("");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript["pin_read"] = function(block) {
    var pin = block.getFieldValue("PIN");
    var code = "gpioRead(" + PINS[pin] + ");\n";
    return [ code, Blockly.JavaScript.ORDER_ATOMIC ];
};

Blockly.Blocks["pin_binary"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        this.appendDummyInput("")
            .appendField(new Blockly.FieldDropdown([ [ "HIGH", "HIGH" ],
                        [ "LOW", "LOW" ] ]), "STATE");
        this.setOutput(true, "pin_value");
        this.setTooltip("Set a pin state logic High or Low.");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {array} Completed code with order of operation.
 */
Blockly.JavaScript["pin_binary"] = function(block) {
    var code = block.getFieldValue("STATE");
    return [ code, Blockly.JavaScript.ORDER_ATOMIC ];
};
